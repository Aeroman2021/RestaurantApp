package com.restaurant.app.demo.controller.dto;

import java.util.Map;

public record ErrorResponseDto(
       String code,
       String message,
       Map<String,Object> data

) {
}
