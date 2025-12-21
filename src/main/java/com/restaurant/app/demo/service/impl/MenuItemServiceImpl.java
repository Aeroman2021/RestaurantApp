package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.dto.menuItem.MenuItemResponseDto;
import com.restaurant.app.demo.model.entity.MenuItem;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.service.MenuItemService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    @Cacheable(
            cacheNames = "menu",
            key = "'all:page=' + #pageable.pageNumber + ':size=' + #pageable.pageSize + ':sort=' + #pageable.sort",
            unless = "#result == null || #result.isEmpty()"
    )
    public List<MenuItemResponseDto> loadAll(Pageable pageable) {
        return menuItemRepository.findAll(pageable)
                .map(this::getMenuItemResponseDto)
                .getContent();
    }

    private  MenuItemResponseDto getMenuItemResponseDto(MenuItem menuItem) {
        return new MenuItemResponseDto(menuItem.getId(), menuItem.getName(), menuItem.getPrice(), menuItem.isActive());
    }

    @Override
    @Cacheable(cacheNames = "menu", key = "'count'")
    public Long countAll() {
        return menuItemRepository.count();
    }
}
