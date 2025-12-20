package com.restaurant.app.demo.model.entity.enums;

import com.restaurant.app.demo.model.entity.Role;

public enum Status {
    CREATED{
        @Override
        public Status next(Role role) {
            if(role.equals("CUSTOMER") ||role.equals("ADMIN")){
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
            if(role.equals("ADMIN")){
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
            if(role.equals("ADMIN")){
                return READY;
            }
            throw invalid(role);
        }

        @Override
        public Status cancel(Role role) {
            if(role.equals("ADMIN")){
                return CANCELLED;
            }
            throw invalid(role);
        }
    },

    READY{
        @Override
        public Status next(Role role) {
            if(role.equals("ADMIN")){
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
