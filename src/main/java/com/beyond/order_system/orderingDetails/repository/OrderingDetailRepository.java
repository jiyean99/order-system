package com.beyond.order_system.orderingDetails.repository;

import com.beyond.order_system.orderingDetails.entity.OrderingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderingDetailRepository extends JpaRepository<OrderingDetails, Long> {
}
