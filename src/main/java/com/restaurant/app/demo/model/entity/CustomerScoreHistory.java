package com.restaurant.app.demo.model.entity;

import com.restaurant.app.demo.model.entity.enums.ScoreReason;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_score_histories")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @NotNull
    @Column(name = "score", nullable = false)
    private Integer score;


    @Enumerated(EnumType.STRING)
    private ScoreReason reason;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public CustomerScoreHistory(User user, Order order, Integer score, ScoreReason reason) {
        this.user = user;
        this.order = order;
        this.score = score;
        this.reason = reason;
    }
}
