package com.restaurant.app.demo.repository;

import com.restaurant.app.demo.model.entity.CustomerLevelRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerLevelRuleRepository extends JpaRepository<CustomerLevelRule,String> {
}
