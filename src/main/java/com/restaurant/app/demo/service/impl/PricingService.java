package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.dto.order.OrderRequestDto;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.service.PricingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PricingService {
    private final PricingStrategyFactory strategyFactory;

    public PricingService(PricingStrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public BigDecimal calculateFinalPrice(Order order, User user){
        PricingStrategy strategy = strategyFactory.getStrategy(user.getCustomerLevel());
        return strategy.calculateFinalPrice(order);
    }
}
