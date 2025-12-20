package com.restaurant.app.demo.repository;

import com.restaurant.app.demo.model.dto.order.OrderResponseDto;
import com.restaurant.app.demo.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query(value = "select o from Order o join fetch o.orderItems",
           countQuery = "select count(o) from Order o")
    Page<Order> getAll(Pageable pageable);
}
