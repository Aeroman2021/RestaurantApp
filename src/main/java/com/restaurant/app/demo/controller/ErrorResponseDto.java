package com.restaurant.app.demo.controller;

import java.util.Map;

public record ErrorResponseDto(
       String code,
       String message,
       Map<String,Object> data

) {
}
