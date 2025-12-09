////// src/main/java/com/example/restaurant/model/Reservation.java
////package com.example.restaurant.model;
////
////import jakarta.persistence.*;
////import lombok.*; // <-- (ВОТ ИЗМЕНЕНИЕ!)
////
////import java.time.LocalDate;
////import java.time.LocalTime;
////
////@Entity
////@Table(name = "reservations")
////// (ВОТ ИСПРАВЛЕНИЕ!) Заменяем @Data
////@Getter
////@Setter
////@NoArgsConstructor
////@AllArgsConstructor
////@Builder
////public class Reservation {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    // (ВОТ ИСПРАВЛЕНИЕ!)
////    // Исключаем User из toString() и equals(), чтобы разорвать цикл
////    @ManyToOne
////    @JoinColumn(name = "user_id", nullable = false)
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    private User user;
////
////    private String fullName; // Full name used for the booking
////    private String phone;    // Phone used for the booking
////
////    private LocalDate reservationDate;
////    private LocalTime reservationTime;
////    private int guestCount;
////}
//// src/main/java/com/example/restaurant/model/Reservation.java
////package com.example.restaurant.model;
////
////import jakarta.persistence.*;
////import lombok.*; // <-- (FIX!)
////
////import java.time.LocalDate;
////import java.time.LocalTime;
////
////@Entity
////@Table(name = "reservations")
////// (FIX!) We replace @Data to prevent infinite loops
////@Getter
////@Setter
////@NoArgsConstructor
////@AllArgsConstructor
////@Builder
////public class Reservation {
////
////    @Id
////    @GeneratedValue(strategy = GenerationType.IDENTITY)
////    private Long id;
////
////    // (FIX!) Exclude this from toString() and equals()
////    @ManyToOne
////    @JoinColumn(name = "user_id", nullable = false)
////    @ToString.Exclude
////    @EqualsAndHashCode.Exclude
////    private User user;
////
////    private String fullName;
////    private String phone;
////    private LocalDate reservationDate;
////    private LocalTime reservationTime;
////    private int guestCount;
////}
//// src/main/java/com/example/restaurant/model/Reservation.java
//package com.example.restaurant.model;
//
//import jakarta.persistence.*;
//        import lombok.*; // <-- (ИЗМЕНЕНИЕ!)
//
//        import java.time.LocalDate;
//import java.time.LocalTime;
//
//@Entity
//@Table(name = "reservations")
//// (ВОТ ИСПРАВЛЕНИЕ!) Заменяем @Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Reservation {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    // (ВОТ ИСПРАВЛЕНИЕ!)
//    // Исключаем User из toString() и equals(), чтобы разорвать цикл
//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private User user;
//
//    private String fullName; // Full name used for the booking
//    private String phone;    // Phone used for the booking
//
//    private LocalDate reservationDate;
//    private LocalTime reservationTime;
//    private int guestCount;
//}
// src/main/java/com/example/restaurant/model/Reservation.java
package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    private String fullName;
    private String phone;
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private int guestCount;

    private Integer tableNumber;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Order order;
}