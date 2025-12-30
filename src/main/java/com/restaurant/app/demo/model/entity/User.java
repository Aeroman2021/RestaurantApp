package com.restaurant.app.demo.model.entity;

import com.restaurant.app.demo.model.entity.enums.CustomerLevel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table(name = "users")
@Entity
@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="username")
    private String userName;

    @Column(name="password_hash")
    private String password;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    private String phone;

    private String email;

    @Column(name = "customer_level")
    private CustomerLevel customerLevel;

    @Column(name = "total_score")
    private int totalScore;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name="users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user",fetch = FetchType.LAZY)
    private List<Order> orders;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name="created_at")
    private LocalDateTime createdAt;


}
