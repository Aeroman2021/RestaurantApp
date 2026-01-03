package com.restaurant.app.demo.model.dto.order;

import java.math.BigDecimal;

public record CheckoutOrderRequestDto(
    String fulFillmentType,
    String deliveryAddressText,
    BigDecimal deliveryLat,
    BigDecimal deliveryLng
) {
}
