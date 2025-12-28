package com.restaurant.app.demo.service.impl;

import com.restaurant.app.demo.model.entity.CustomerScoreHistory;
import com.restaurant.app.demo.model.entity.Order;
import com.restaurant.app.demo.model.entity.User;
import com.restaurant.app.demo.model.entity.enums.ScoreReason;
import com.restaurant.app.demo.repository.CustomerScoreHistoryRepository;
import com.restaurant.app.demo.repository.UserRepository;
import com.restaurant.app.demo.service.ScoreStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ScoreService {
    private final ScoreStrategy scoreStrategy;
    private final UserRepository userRepository;
    private final CustomerScoreHistoryRepository customerScoreHistoryRepository;
    private final CustomerLevelEvaluator customerLevelEvaluator;

    public ScoreService(ScoreStrategy scoreStrategy, UserRepository userRepository,
                        CustomerScoreHistoryRepository customerScoreHistoryRepository,
                        CustomerLevelEvaluator customerLevelEvaluator) {
        this.scoreStrategy = scoreStrategy;
        this.userRepository = userRepository;
        this.customerScoreHistoryRepository = customerScoreHistoryRepository;
        this.customerLevelEvaluator = customerLevelEvaluator;
    }

    @Transactional
    public void addScore(User user, Order order){
        int score = scoreStrategy.calculateScore(order);
        user.setTotalScore(user.getTotalScore() + score);
        user.setCustomerLevel(customerLevelEvaluator.evaluate(score));
        userRepository.save(user);
        customerScoreHistoryRepository.save(new CustomerScoreHistory(user,order,score, ScoreReason.ORDER_PURCHASE));
    }
}
