package com.restaurant.app.demo.repository;

import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.model.entity.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query(value = "select o from Order o join fetch o.orderItems",
           countQuery = "select count(o) from Order o")
    Page<Order> getAll(Pageable pageable);

    Optional<Order> findByUserAndStatus(User user, Status Status);
}
