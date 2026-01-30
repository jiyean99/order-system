package com.beyond.order_system.ordering.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.ordering.domain.OrderStatus;
import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.request.OrderCreateReqDto;
import com.beyond.order_system.ordering.dto.response.OrderListResDto;
import com.beyond.order_system.ordering.repository.OrderingRepository;
import com.beyond.order_system.orderingDetails.entity.OrderingDetails;
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

        for (var item : items) {
            Product product = productRepository.findByIdForUpdate(item.getProductId());
            long qty = item.getProductCount().longValue();

            if (product.getStockQuantity() < qty) throw new IllegalArgumentException("재고 부족");

            product.decreaseStock(qty);
            order.addItem(OrderingDetails.builder()
                    .product(product)
                    .quantity(qty)
                    .build());
        }

        orderingRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<OrderListResDto> findAll(Pageable pageable) {

        Page<Long> idPage = orderingRepository.findIds(pageable);
        List<Long> ids = idPage.getContent();
        if (ids.isEmpty()) return List.of();

        List<Ordering> orders = orderingRepository.findAllByIdInWithMemberItemsProduct(ids);

        Map<Long, Ordering> map = orders.stream()
                .collect(Collectors.toMap(Ordering::getId, o -> o));

        return ids.stream().map(id -> {
            Ordering o = map.get(id);

            List<OrderListResDto.OrderDetailResDto> details =
                    o.getOrderItems().stream()
                            .map(oi -> OrderListResDto.OrderDetailResDto.builder()
                                    .detailId(oi.getId())
                                    .productName(oi.getProduct().getName())
                                    .productCount(oi.getQuantity())
                                    .build())
                            .toList();

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

        Page<Long> idPage = orderingRepository.findMyOrderIds(memberId, pageable);
        List<Long> ids = idPage.getContent();
        if (ids.isEmpty()) return List.of();

        List<Ordering> orders = orderingRepository.findAllByIdInWithMemberItemsProduct(ids);

        Map<Long, Ordering> map = orders.stream()
                .collect(Collectors.toMap(Ordering::getId, o -> o));

        return ids.stream().map(id -> {
            Ordering o = map.get(id);

            List<OrderListResDto.OrderDetailResDto> details =
                    o.getOrderItems().stream()
                            .map(oi -> OrderListResDto.OrderDetailResDto.builder()
                                    .detailId(oi.getId())
                                    .productName(oi.getProduct().getName())
                                    .productCount(oi.getQuantity())
                                    .build())
                            .toList();

            return OrderListResDto.builder()
                    .id(o.getId())
                    .memberEmail(o.getMember().getEmail())
                    .orderStatus(o.getOrderStatus())
                    .orderDetails(details)
                    .build();
        }).toList();
    }

}

