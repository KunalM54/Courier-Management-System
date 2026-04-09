package com.example.CourierManagement.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum UserRole {
    ADMIN,
    MANAGER,
    DELIVERY_AGENT,
    CUSTOMER;

    @JsonCreator
    public static UserRole from(String value) {
        return UserRole.valueOf(value.toUpperCase());
    }
}
