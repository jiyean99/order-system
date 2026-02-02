package com.beyond.order_system.ordering.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.ordering.domain.OrderStatus;
import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.request.OrderCreateReqDto;
import com.beyond.order_system.ordering.dto.response.OrderListResDto;
import com.beyond.order_system.ordering.repository.OrderingRepository;
import com.beyond.order_system.ordering.domain.OrderingDetails;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class OrderingService {

    private final OrderingRepository orderingRepository;
    private final ProductRepository productRepository;
    private final EntityManager em;

    @Autowired
    public OrderingService(OrderingRepository orderingRepository,
                           ProductRepository productRepository,
                           EntityManager em) {
        this.orderingRepository = orderingRepository;
        this.productRepository = productRepository;
        this.em = em;
    }

    public void create(List<OrderCreateReqDto.OrderItemCreateReqDto> items, String principal) {
        Long memberId = Long.valueOf(principal);

        Ordering order = Ordering.builder()
                .member(em.getReference(Member.class, memberId))
                .orderStatus(OrderStatus.ORDERED)
                .build();

        for (OrderCreateReqDto.OrderItemCreateReqDto itemDto : items) {
            Product product = productRepository.findByIdForUpdate(itemDto.getProductId());
            long qty = itemDto.getProductCount().longValue();

            if (product.getStockQuantity() < qty) throw new IllegalArgumentException("재고 부족");

            product.decreaseStockQuantity(qty);
            order.addItem(OrderingDetails.builder()
                    .product(product)
                    .quantity(qty)
                    .build());
        }

        orderingRepository.save(order);
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

