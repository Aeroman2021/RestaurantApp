package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.entity.CustomerLevelRule;
import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import com.restaurant.app.demo.repository.CustomerLevelRuleRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class CustomerLevelEvaluator {
    private final CustomerLevelRuleRepository customerLevelRuleRepository;

    public CustomerLevelEvaluator(CustomerLevelRuleRepository customerLevelRuleRepository) {
        this.customerLevelRuleRepository = customerLevelRuleRepository;
    }

    public CustomerLevel evaluate(int totalScore){
        return customerLevelRuleRepository.findAll().stream()
                .filter(rule-> totalScore >= rule.getMinScore())
                .max(Comparator.comparing(CustomerLevelRule::getMinScore))
                .map(rule->CustomerLevel.valueOf(rule.getLevel()))
                .orElse(CustomerLevel.REGULAR);
    }
}
