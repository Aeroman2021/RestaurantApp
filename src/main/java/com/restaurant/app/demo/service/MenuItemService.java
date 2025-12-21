package com.restaurant.app.demo.service;

import com.restaurant.app.demo.model.dto.menuItem.MenuItemResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuItemService {
    List<MenuItemResponseDto> loadAll(Pageable pageable);
    Long countAll();

}
