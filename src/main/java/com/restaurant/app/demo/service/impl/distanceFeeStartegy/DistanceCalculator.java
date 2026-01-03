package com.restaurant.app.demo.service.impl.distanceFeeStartegy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class DistanceCalculator {
    private static final double EARTH_RADIUS_KM = 6371.0088;

    public BigDecimal calculateKm(
            BigDecimal lat1, BigDecimal lon1,
            BigDecimal lat2, BigDecimal lon2) {

        double lat1Rad = Math.toRadians(lat1.doubleValue());
        double lat2Rad = Math.toRadians(lat2.doubleValue());
        double lon1Rad = Math.toRadians(lon1.doubleValue());
        double lon2Rad = Math.toRadians(lon2.doubleValue());

        double x = (lon2Rad - lon1Rad) * Math.cos((lat1Rad + lat2Rad) / 2);
        double y = (lat2Rad - lat1Rad);

        double km = Math.sqrt(x * x + y * y) * EARTH_RADIUS_KM;

        return BigDecimal.valueOf(km).setScale(2, RoundingMode.HALF_UP);
    }

}
