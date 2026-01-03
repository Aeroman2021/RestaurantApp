package com.restaurant.app.demo.repository;

import com.restaurant.app.demo.model.entity.Restaurant;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    @EntityGraph(attributePaths = "menueItems")
    Optional<Restaurant> findWithMenuItemsById(Long id);
}
