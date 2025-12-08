//////// src/main/java/com/example/restaurant/model/Order.java
//////package com.example.restaurant.model;
//////
//////import jakarta.persistence.*;
//////import lombok.*;
//////
//////import java.math.BigDecimal;
//////import java.time.LocalDateTime;
//////import java.util.ArrayList;
//////import java.util.List;
//////
//////@Entity
//////@Table(name = "customer_orders") // Using "customer_orders" as "order" is a reserved SQL keyword
//////@Getter
//////@Setter
//////@Builder
//////@NoArgsConstructor
//////@AllArgsConstructor
//////public class Order {
//////
//////    @Id
//////    @GeneratedValue(strategy = GenerationType.IDENTITY)
//////    private Long id;
//////
//////    // The waiter who created this order
//////    @ManyToOne
//////    @JoinColumn(name = "waiter_id", nullable = false)
//////    @ToString.Exclude
//////    @EqualsAndHashCode.Exclude
//////    private User waiter;
//////
//////    @Column(nullable = false)
//////    private Integer tableNumber;
//////
//////    @Enumerated(EnumType.STRING)
//////    @Column(nullable = false)
//////    private OrderStatus status;
//////
//////    @Column(nullable = false)
//////    private LocalDateTime createdAt;
//////
//////    // The list of all items in this order.
//////    // Eager fetch to always load items with the order.
//////    // CascadeType.ALL means saving/deleting an Order will also save/delete its items.
//////    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
//////    @Builder.Default
//////    private List<OrderItem> items = new ArrayList<>();
//////
//////    /**
//////     * Helper method to calculate the total price of the entire order.
//////     * @return Sum of all item subtotals.
//////     */
//////    public BigDecimal getTotalPrice() {
//////        return items.stream()
//////                .map(OrderItem::getSubtotal)
//////                .reduce(BigDecimal.ZERO, BigDecimal::add);
//////    }
//////}
////// src/main/java/com/example/restaurant/model/Order.java
////package com.example.restaurant.model;
////
////import jakarta.persistence.*;
////import lombok.*;
////
////import java.math.BigDecimal;
////import java.time.LocalDateTime;
////import java.util.ArrayList;
////import java.util.List;
////
////@Entity
////@Table(name = "customer_orders")
////@Getter
////@Setter
////@Builder
////@NoArgsConstructor
////@AllArgsConstructor
////public class Order {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    // Официант (может быть null, если заказ создан клиентом онлайн)
////    @ManyToOne
////    @JoinColumn(name = "waiter_id")
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    private User waiter;
////
////    // (НОВОЕ!) Клиент (для онлайн-заказов)
////    @ManyToOne
////    @JoinColumn(name = "customer_id")
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    private User customer;
////
////    @Column(nullable = false)
////    private Integer tableNumber;
////
////    @Enumerated(EnumType.STRING)
////    @Column(nullable = false)
////    private OrderStatus status;
////
////    @Column(nullable = false)
////    private LocalDateTime createdAt;
////
////    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
////    @Builder.Default
////    private List<OrderItem> items = new ArrayList<>();
////
////    // (НОВОЕ!) Связь с бронированием
////    @OneToOne
////    @JoinColumn(name = "reservation_id")
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    private Reservation reservation;
////
////    public BigDecimal getTotalPrice() {
////        return items.stream()
////                .map(OrderItem::getSubtotal)
////                .reduce(BigDecimal.ZERO, BigDecimal::add);
////    }
////}
//// src/main/java/com/example/restaurant/model/Order.java
//package com.example.restaurant.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "customer_orders")
//@Getter
//@Setter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor
//public class Order {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "waiter_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private User waiter;
//
//    @ManyToOne
//    @JoinColumn(name = "customer_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private User customer;
//
//    @Column(nullable = false)
//    private Integer tableNumber;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private OrderStatus status;
//
//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//
//    // (ВОТ ИЗМЕНЕНИЕ!) Добавлено orphanRemoval = true
//    // Это позволяет удалять старые пункты заказа при редактировании
//    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
//    @Builder.Default
//    private List<OrderItem> items = new ArrayList<>();
//
//    @OneToOne
//    @JoinColumn(name = "reservation_id")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Reservation reservation;
//
//    public BigDecimal getTotalPrice() {
//        return items.stream()
//                .map(OrderItem::getSubtotal)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//    }
//}
// src/main/java/com/example/restaurant/model/Order.java
package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "waiter_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User waiter;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User customer;

    @Column(nullable = false)
    private Integer tableNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "reservation_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Reservation reservation;

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // ...
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private java.util.List<Payment> payments = new java.util.ArrayList<>();

    // Хелпер для подсчета уже оплаченной суммы
    public java.math.BigDecimal getPaidAmount() {
        if (payments == null) return java.math.BigDecimal.ZERO;
        return payments.stream()
                .map(Payment::getAmount)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    }
}