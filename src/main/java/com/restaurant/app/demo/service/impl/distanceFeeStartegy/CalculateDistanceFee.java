package com.restaurant.app.demo.service.impl.distanceFeeStartegy;

import com.restaurant.app.demo.service.FeeStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class CalculateDistanceFee implements FeeStrategy {
    private static final BigDecimal BASE_FEE = new BigDecimal("20000");
    private static final BigDecimal PER_KM_FEE = new BigDecimal("8000");
    private static final BigDecimal MIN_FEE = new BigDecimal("20000");
    private static final BigDecimal MAX_FEE = new BigDecimal("80000");

    @Override
    public BigDecimal calculateFee(double distance) {
        if (distance < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }

        BigDecimal distancekm = BigDecimal.valueOf(distance);

        BigDecimal fee = BASE_FEE.add(
                PER_KM_FEE.multiply(distancekm)
        );

        // enforce min / max
        if (fee.compareTo(MIN_FEE) < 0) {
            return MIN_FEE;
        }

        if (fee.compareTo(MAX_FEE) > 0) {
            return MAX_FEE;
        }

        return fee.setScale(0, RoundingMode.HALF_UP);

    }
}
