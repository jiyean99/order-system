package com.beyond.order_system.ordering.service;

import com.beyond.order_system.member.domain.Member;
import com.beyond.order_system.ordering.domain.OrderStatus;
import com.beyond.order_system.ordering.domain.Ordering;
import com.beyond.order_system.ordering.dto.request.OrderCreateReqDto;
import com.beyond.order_system.ordering.repository.OrderingRepository;
import com.beyond.order_system.orderingDetails.entity.OrderingDetails;
import com.beyond.order_system.product.domain.Product;
import com.beyond.order_system.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public void create(OrderCreateReqDto dto, String principal) {
        Long memberId = Long.valueOf(principal);

        Ordering order = Ordering.builder()
                .member(em.getReference(Member.class, memberId))
                .orderStatus(OrderStatus.ORDERED)
                .build();

        for (OrderCreateReqDto.OrderItemCreateReqDto item : dto.getItems()) {
            Product product = productRepository.findByIdForUpdate(item.getProductId());

            long qty = item.getProductCount().longValue();
            if (product.getStockQuantity() < qty) {
                throw new IllegalArgumentException("재고 부족");
            }

            // 도메인 메서드 없이 직접 차감(가능)
            product.decreaseStock(product.getStockQuantity() - qty);

            OrderingDetails detail = OrderingDetails.builder()
                    .product(product)
                    .quantity(qty)
                    .build();

            order.addItem(detail);
        }

        orderingRepository.save(order);
    }
}

