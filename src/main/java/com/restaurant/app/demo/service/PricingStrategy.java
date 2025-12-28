package com.restaurant.app.demo.service;

import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.enums.CustomerLevel;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculateFinalPrice(Order order);
    CustomerLevel supports();
}
