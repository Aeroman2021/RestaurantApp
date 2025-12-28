package com.restaurant.app.demo.service;

import com.restaurant.app.demo.model.entity.Order;

public interface ScoreStrategy {
    int calculateScore(Order order);
}
