package com.restaurant.app.demo.model.entity;

import jakarta.persistence.*;

import java.util.List;

@Table(name = "categories")
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    List<MenuItem> menuItems;

    private String name;

    public Category() {
    }

    public Category(Long id, List<MenuItem> menuItems, String name) {
        this.id = id;
        this.menuItems = menuItems;
        this.name = name;
    }

    public Category(List<MenuItem> menuItems, String name) {
        this.menuItems = menuItems;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
