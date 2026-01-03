package com.restaurant.app.demo.excception.order;

import com.restaurant.app.demo.model.entity.enums.ActorRole;
import com.restaurant.app.demo.model.entity.enums.Status;

public class InvalidOrderStatusTransitionException extends RuntimeException{

    public InvalidOrderStatusTransitionException() {
    }

    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }

    public InvalidOrderStatusTransitionException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidOrderStatusTransitionException(Status from, ActorRole actor){
        super("Actor " + actor + " cannot move order from status " + from);
    }
}
