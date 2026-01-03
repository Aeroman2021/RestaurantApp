package com.restaurant.app.demo.service.impl.distanceFeeStartegy;

import com.restaurant.app.demo.service.FeeStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class DeliveryPricingService {
    private final DistanceCalculator distanceCalculator;
    private final FeeStrategy feeStrategy;

    public DeliveryPricingService(
            DistanceCalculator distanceCalculator,
            FeeStrategy feeStrategy) {
        this.distanceCalculator = distanceCalculator;
        this.feeStrategy = feeStrategy;
    }

    public DeliveryQuote quote(
            BigDecimal restLat, BigDecimal restLng,
            BigDecimal userLat, BigDecimal userLng) {

        BigDecimal distanceKm =
                distanceCalculator.calculateKm(restLat, restLng, userLat, userLng);

        BigDecimal fee =
                feeStrategy.calculateFee(distanceKm.doubleValue());

        return new DeliveryQuote(distanceKm, fee);
    }

    public record DeliveryQuote(BigDecimal distanceKm, BigDecimal fee) {}
}
