package com.restaurant.app.demo.controller;


import com.restaurant.app.demo.model.dto.menuItem.MenuItemResponseDto;
import com.restaurant.app.demo.service.MenuItemService;
import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menuItems")
public class MenuItemController {

    private final MenuItemService menuItemService;

    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ApiResponse<Page<MenuItemResponseDto>> loadAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "ASC") String direction,
            @RequestParam(defaultValue = "name") String property){

        Sort.Direction dir = Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC);
        List<String> allowedProperties = List.of("name","price");

        if(!allowedProperties.contains(property)){
            throw new IllegalArgumentException("Invalid sort property");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, property));
        List<MenuItemResponseDto> items = menuItemService.loadAll(pageable);

        Long total = menuItemService.countAll();

        Page<MenuItemResponseDto> result = new PageImpl<>(items,pageable,total);
        return ApiResponse.ok(result,"Menu retched successfully");
    }
}
