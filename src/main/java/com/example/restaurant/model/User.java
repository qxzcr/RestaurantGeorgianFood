//// src/main/java/com/example/restaurant/model/User.java
//package com.example.restaurant.model;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//
//import java.util.Collection;
//import java.util.List;
//
//@Entity
//@Table(name = "users")
//// Replaced @Data with specific annotations to prevent loops
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class User implements UserDetails {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false)
//    private String email;
//
//    @Column(nullable = false)
//    private String password;
//
//    @Column(nullable = false)
//    private String fullName;
//
//    private String phone;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    // This links the user to their reservations
//    // Excluded from toString/equals to prevent infinite loop
//    @OneToMany(mappedBy = "user")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<Reservation> reservations;
//
//    // (HERE IS THE FIX!)
//    // This links the User (as a WAITER) to the orders they created
//    // This was missing from your file.
//    @OneToMany(mappedBy = "waiter")
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private List<Order> createdOrders;
//
//
//    // --- UserDetails Methods ---
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}
// src/main/java/com/example/restaurant/model/User.java
package com.example.restaurant.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "users")
// (ВОТ ИСПРАВЛЕНИЕ!) Заменяем @Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    // (ВОТ ИСПРАВЛЕНИЕ!)
    // Исключаем User из toString() и equals(), чтобы разорвать цикл
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private List<Reservation> reservations;

    // (Добавляем связь для Панели Официанта)
    @OneToMany(mappedBy = "waiter")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore

    private List<Order> createdOrders;


    // --- UserDetails Methods ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public String getUsername() { return email; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}