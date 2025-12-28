package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import com.restaurant.app.demo.service.PricingStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PricingStrategyFactory {
    private final Map<CustomerLevel, PricingStrategy> strategies;

    public PricingStrategyFactory(List<PricingStrategy> strategyList){
        strategies = strategyList.stream()
                .collect(Collectors.toMap(
                        PricingStrategy::supports, Function.identity()
                ));
    }

    public PricingStrategy getStrategy(CustomerLevel customerType){
        return strategies.get(customerType);
    }

}
