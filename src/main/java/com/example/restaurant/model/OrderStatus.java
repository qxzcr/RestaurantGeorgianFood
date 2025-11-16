// src/main/java/com/example/restaurant/model/OrderStatus.java
package com.example.restaurant.model;

/**
 * Represents the state of a customer's order.
 */
public enum OrderStatus {
    PREPARING, // Kitchen is working on it
    READY,     // Ready for pickup by the waiter
    SERVED,    // Delivered to the table
    PAID       // Bill is settled
}