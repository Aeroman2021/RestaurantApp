package com.restaurant.app.demo.service.impl.scoreStrategyImpl;

import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.service.OrderService;
import com.restaurant.app.demo.service.ScoreStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AmountBaseScoreStrategy implements ScoreStrategy {

    private final MenuItemRepository menuItemRepository;

    public AmountBaseScoreStrategy(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public int calculateScore(Order order) {
        return calculateBasePrice(order).divide(new BigDecimal("100000")).intValue();
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
