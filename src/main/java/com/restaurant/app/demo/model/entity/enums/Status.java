package com.restaurant.app.demo.model.entity.enums;

import com.restaurant.app.demo.model.entity.Role;

public enum Status {

    CART{
        @Override
        public Status next(Role role) {
            if(role.getName().equals("ROLE_CUSTOMER")){
                return CREATED;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(Role role) {
            return CANCELLED;
        }
    },


    CREATED{
        @Override
        public Status next(Role role) {
            if(role.getName().equals("ROLE_CUSTOMER") ||role.getName().equals("ROLE_ADMIN")){
                return PAID;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(Role role) {
            return CANCELLED;
        }
    },

    PAID{
        @Override
        public Status next(Role role) {
            if(role.getName().equals("ROLE_ADMIN")){
                return PREPARING;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(Role role) {
            return CANCELLED;
        }
    },

    PREPARING{
        @Override
        public Status next(Role role) {
            if(role.getName().equals("ROLE_ADMIN")){
                return READY;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(Role role) {
            if(role.getName().equals("ROLE_ADMIN")){
                return CANCELLED;
            }
            throw invalid(role);
        }
    },

    READY{
        @Override
        public Status next(Role role) {
            if(role.getName().equals("ROLE_ADMIN")){
                return DELIVERED;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(Role role) {
            throw new IllegalStateException("Cannot cancel ready order");
        }
    },

    DELIVERED{
        @Override
        public Status next(Role role) {
            throw new IllegalStateException("Order already delivered");
        }
        @Override
        public Status cancel(Role role) {
            throw new IllegalStateException("Cannot cancel delivered order");
        }
    },

    CANCELLED;

    public  Status next(Role role){
        throw new UnsupportedOperationException();
    }

    public Status cancel(Role role){
        throw new UnsupportedOperationException();
    }

    protected RuntimeException invalid(Role role) {
        return new IllegalStateException(
                "Role " + role + " cannot change status from " + this
        );
    }

}
