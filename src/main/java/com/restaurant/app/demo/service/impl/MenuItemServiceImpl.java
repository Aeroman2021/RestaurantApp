package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.dto.menuItem.MenuItemResponseDto;
import com.restaurant.app.demo.repository.MenuItemRepository;
import com.restaurant.app.demo.service.MenuItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemServiceImpl(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Override
    public Page<MenuItemResponseDto> loadAll(Pageable pageable) {
        return menuItemRepository.findAll(pageable)
                .map(menuItem ->
                        new MenuItemResponseDto(menuItem.getId(), menuItem.getName(),menuItem.getPrice(), menuItem.isActive()));
    }
}
