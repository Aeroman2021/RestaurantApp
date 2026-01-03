package com.restaurant.app.demo.service;

import java.math.BigDecimal;

public interface FeeStrategy {
    BigDecimal calculateFee(double distance);
}
