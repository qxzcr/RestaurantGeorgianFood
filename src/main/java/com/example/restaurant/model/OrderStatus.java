package com.example.restaurant.model;

public enum OrderStatus {
    CREATED,
    PREPARING, // <--- You were missing this
    READY,
    SERVED,
    PAID,      // <--- Needed for Payment logic
    CLOSED,    // <--- Needed for Bill logic
    CANCELLED
}