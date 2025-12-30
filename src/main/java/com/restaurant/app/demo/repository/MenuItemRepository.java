package com.restaurant.app.demo.repository;

import com.restaurant.app.demo.model.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {

    @Query("""
      select count (mi.id) from MenuItem mi
      where mi.id in :ids and
      mi.restaurant.id = :restaurantId
""")
    long countByIdsAndRestaurantId(@Param("ids") Set<Long> ids,
                                   @Param("restaurantId") Long restaurantId);

}
