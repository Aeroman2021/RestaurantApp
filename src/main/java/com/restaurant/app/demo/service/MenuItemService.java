package com.restaurant.app.demo.service;

import com.restaurant.app.demo.model.dto.menuItem.MenuItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuItemService {
    Page<MenuItemResponseDto> loadAll(Pageable pageable);

}
