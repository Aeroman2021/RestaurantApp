package com.restaurant.app.demo.service.impl.pricingStrategyImpl;

import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.service.PricingStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class GoldPricingStrategy implements PricingStrategy {

    private final MenuItemRepository menuItemRepository;

    public GoldPricingStrategy(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public BigDecimal calculateFinalPrice(Order order) {
        return calculateBasePrice(order).multiply(new BigDecimal("0.8"));
    }

    @Override
    public CustomerLevel supports() {
        return CustomerLevel.GOLD;
    }

    BigDecimal calculateBasePrice(Order order) {
        return  order.getOrderItems()
                .stream()
                .map(req->{
                    MenuItem menuItem = menuItemRepository.findById(req.getMenuItem().getId()).orElseThrow(
                            ()->new RuntimeException("Menu Not Found"));
                    return menuItemRepository.findById(menuItem.getId()).get().getPrice().multiply(BigDecimal.valueOf(req.getQuantity()));
                }).reduce(BigDecimal.ZERO,BigDecimal::add);
    }

}
