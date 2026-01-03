package com.restaurant.app.demo.model.entity.enums;

import com.restaurant.app.demo.excception.order.InvalidOrderStatusTransitionException;

public enum Status {

    CART{
        @Override
        public Status next(ActorRole role) {
            if(role.equals(ActorRole.CUSTOMER)){
                return CREATED;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(ActorRole role) {
            if(role.equals(ActorRole.CUSTOMER)){
                return CANCELLED;
            }
            throw invalid(role);
        }
    },

    CREATED{
        @Override
        public Status next(ActorRole role) {
            if(role.equals(ActorRole.CUSTOMER) ||role.equals(ActorRole.ADMIN)){
                return PAID;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(ActorRole role) {
            if(role.equals(ActorRole.CUSTOMER) ||role.equals(ActorRole.ADMIN)){
                return CANCELLED;
            }
            throw invalid(role);
        }
    },

    PAID{
        @Override
        public Status next(ActorRole role) {
            if(role.equals(ActorRole.ADMIN)){
                return PREPARING;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(ActorRole role) {
            if(role.equals(ActorRole.CUSTOMER) || role.equals(ActorRole.ADMIN)){
                return CANCELLED;
            }
            throw invalid(role);
        }
    },

    PREPARING{
        @Override
        public Status next(ActorRole role) {
            if(role.equals(ActorRole.ADMIN)){
                return READY;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(ActorRole role) {
            if(role.equals(ActorRole.ADMIN)){
                return CANCELLED;
            }
            throw invalid(role);
        }
    },

    READY{
        @Override
        public Status next(ActorRole role) {
            if(role.equals(ActorRole.ADMIN)){
                return DELIVERED;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(ActorRole role) {
            throw new InvalidOrderStatusTransitionException("Cannot cancel ready order");
        }
    },

    DELIVERED{
        @Override
        public Status next(ActorRole role) {
            throw new InvalidOrderStatusTransitionException("Order already delivered");
        }
        @Override
        public Status cancel(ActorRole role) {
            throw new InvalidOrderStatusTransitionException("Cannot cancel delivered order");
        }
    },

    CANCELLED;

    public  Status next(ActorRole role){
        throw new InvalidOrderStatusTransitionException();
    }

    public Status cancel(ActorRole role){
        throw new InvalidOrderStatusTransitionException();
    }

    protected RuntimeException invalid(ActorRole role) {
        return new InvalidOrderStatusTransitionException(this,role);
    }

}
