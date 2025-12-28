package com.restaurant.app.demo.repository;

import com.restaurant.app.demo.model.entity.CustomerScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerScoreHistoryRepository extends JpaRepository<CustomerScoreHistory,Long> {
}
