package com.beyond.order_system.ordering.service;

import com.beyond.order_system.common.service.RabbitMqStockService;
import com.beyond.order_system.common.service.SseAlarmService;
import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.ordering.domain.OrderStatus;
import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.request.OrderCreateReqDto;
import com.beyond.order_system.ordering.dto.response.OrderListResDto;
import com.beyond.order_system.ordering.repository.OrderingDetailRepository;
import com.beyond.order_system.ordering.repository.OrderingRepository;
import com.beyond.order_system.ordering.domain.OrderingDetails;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
// Ordering 브랜치 테스트 코드

@Slf4j
@Service
@Transactional
public class OrderingService {
    /* *********************** DI 주입 *********************** */
    private final OrderingRepository orderingRepository;
    private final ProductRepository productRepository;
    private final EntityManager em;
    private final SseAlarmService sseAlarmService;
    private final RedisTemplate<String, String> redisTemplate;
    private final RabbitMqStockService rabbitMqStockService;
    private  final OrderingDetailRepository orderingDetailRepository;
    // jwt 작업 사항 테스트

    @Autowired
    public OrderingService(OrderingRepository orderingRepository,
                           ProductRepository productRepository,
                           EntityManager em, SseAlarmService sseAlarmService, @Qualifier("stockInventory") RedisTemplate<String, String> redisTemplate, RabbitMqStockService rabbitMqStockService, OrderingDetailRepository orderingDetailRepository) {
        this.orderingRepository = orderingRepository;
        this.productRepository = productRepository;
        this.em = em;
        this.sseAlarmService = sseAlarmService;
        this.redisTemplate = redisTemplate;
        this.rabbitMqStockService = rabbitMqStockService;
        this.orderingDetailRepository = orderingDetailRepository;
    }

    // [동시성 제어 방법(1)] : 격리 수준 높이기
//    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long create(List<OrderCreateReqDto.OrderItemCreateReqDto> items, String principal) {
        Long memberId = Long.valueOf(principal);

        Ordering order = Ordering.builder()
                .member(em.getReference(Member.class, memberId))
                .orderStatus(OrderStatus.ORDERED)
                .build();

        // redis에서 재고조회를 할 때 redis의 빠른 속도로 인해 ordering 객체의 ID가 채번되지 않은 상태에서 조회를 하는 경우를 우려하여 save 위치를 상단으로 변경
        orderingRepository.save(order);

        for (OrderCreateReqDto.OrderItemCreateReqDto itemDto : items) {
            // [동시성 제어 이전 기존 코드]
            // Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(()->new EntityNotFoundException("product is not found"));

            // [동시성 제어 방법(2)] : select for update를 통한 배타락 설정(락 설정 이후 조회)
            // Product product = productRepository.findByIdForUpdate(itemDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("product is not found"));

            long qty = itemDto.getProductCount().longValue();

            // [동시성 제어 이전 기존 코드] : redis에서 재고를 조회하는 로직으로 변경함에 따라 아래의 코드 주석처리
            // if (product.getStockQuantity() < qty) throw new IllegalArgumentException("재고 부족");
            // product.decreaseStockQuantity(qty);

            // [동시성 제어 방법(3)] : redis
            // TODO 아래의 코드에서 문제점이 있다면, redis는 싱글스레드로 동시성 제어가 되지만 값을 감소하는 사이에 java 코드들 사이에서 동시성이 또 발생하게 될 수 있게 된다. 즉 조회와 감소가 한 세트로 움직여야한다. 이를 한 세트로 묶어서 redis의 단일 요청하는 기술이 있다.
            // - 단점 : 조회와 감소 요청이 분리되다보니, 동시성 문제 발생함
            // - 해결책 : 루아(lua) 스크립트를 통해 여러 작업을 단일 요청으로 묶어 해결 가능 (*루아 스크립트 : redis 명령어)
            Product product = productRepository.findById(itemDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("product is not found"));
            String remainValue = redisTemplate.opsForValue().get(String.valueOf(itemDto.getProductId()));
            int remainQuantity = Integer.parseInt(remainValue);
            if (remainQuantity < qty) {
                throw new IllegalArgumentException("재고 부족");
            } else {
                redisTemplate.opsForValue().decrement(String.valueOf(itemDto.getProductId()), itemDto.getProductCount());
            }

            OrderingDetails orderingDetails = OrderingDetails.builder()
                    .product(product)
                    .quantity(qty)
                    .ordering(order)
                    .build();
            orderingDetailRepository.save(orderingDetails);

            // [decreaseStockQuantity 로직을 대체할 rabbitMQ 메시지 발행]
            // - RDB 동기화를 위한 작업 (동기화 가능한 방법 종류 : 1. 스케줄러 활용, 2. rabbit mq 활용)
            // - Rabbit MQ의 RDB 재고 감소 메시지 발행
            rabbitMqStockService.publish(itemDto.getProductId(), itemDto.getProductCount());
        }

        // 주문 성공시 admin 유저에게 알림메시지 발송
        String message = order.getId() + "번 주문이 들어왔습니다.";

        sseAlarmService.sendMessage(1L, memberId, message);
        return order.getId();
    }

    @Transactional(readOnly = true)
    public List<OrderListResDto> findAll(Pageable pageable) {

        // 주문 ID만 먼저 가져오기
        Page<Long> idPage = orderingRepository.findIds(pageable);
        // 이번 페이지에 해당하는 주문 ID 목록
        List<Long> ids = idPage.getContent();
        // 주문이 하나도 없으면 빈 리스트 return
        if (ids.isEmpty()) return List.of();

        // IDs에 해당하는 주문들 조회
        List<Ordering> orders = orderingRepository.findAllByIdInWithMemberItemsProduct(ids);

        // id -> 주문 엔티티 로 찾을 수 있도록 Map으로 설계 구조 변경
        Map<Long, Ordering> map = orders.stream()
                .collect(Collectors.toMap(Ordering::getId, o -> o));

        // ids 순서로 DTO 조립
        return ids.stream().map(id -> {

            // 현재 id에 해당하는 주문 객체 꺼내기
            Ordering o = map.get(id);

            // 주문객체의 아이템들 OrderDetailResDto로 조립
            List<OrderListResDto.OrderDetailResDto> details =
                    o.getOrderItems().stream()
                            .map(oi -> OrderListResDto.OrderDetailResDto.builder()
                                    .detailId(oi.getId())
                                    .productName(oi.getProduct().getName())
                                    .productCount(oi.getQuantity())
                                    .build())
                            .toList();

            // 주문 DTO 조립 (주문 기본정보 + 위에서 만든 상세 목록)
            return OrderListResDto.builder()
                    .id(o.getId())
                    .memberEmail(o.getMember().getEmail())
                    .orderStatus(o.getOrderStatus())
                    .orderDetails(details)
                    .build();
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<OrderListResDto> findMyOrders(Long memberId, Pageable pageable) {

        // 주문 ID만 먼저 가져오기
        Page<Long> idPage = orderingRepository.findMyOrderIds(memberId, pageable);
        // 이번 페이지에 해당하는 주문 ID 목록
        List<Long> ids = idPage.getContent();
        // 주문이 하나도 없으면 빈 리스트 return
        if (ids.isEmpty()) return List.of();

        // IDs에 해당하는 주문들 조회
        List<Ordering> orders = orderingRepository.findAllByIdInWithMemberItemsProduct(ids);


        // id -> 주문 엔티티 로 찾을 수 있도록 Map으로 설계 구조 변경
        Map<Long, Ordering> map = orders.stream()
                .collect(Collectors.toMap(Ordering::getId, o -> o));

        // ids 순서로 DTO 조립
        return ids.stream().map(id -> {
            Ordering o = map.get(id);

            // 주문객체의 아이템들 OrderDetailResDto로 조립
            List<OrderListResDto.OrderDetailResDto> details =
                    o.getOrderItems().stream()
                            .map(oi -> OrderListResDto.OrderDetailResDto.builder()
                                    .detailId(oi.getId())
                                    .productName(oi.getProduct().getName())
                                    .productCount(oi.getQuantity())
                                    .build())
                            .toList();

            // 주문 DTO 조립
            return OrderListResDto.builder()
                    .id(o.getId())
                    .memberEmail(o.getMember().getEmail())
                    .orderStatus(o.getOrderStatus())
                    .orderDetails(details)
                    .build();
        }).toList();
    }

}

