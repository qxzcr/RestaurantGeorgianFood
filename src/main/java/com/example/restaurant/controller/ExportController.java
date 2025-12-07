//// src/main/java/com/example/restaurant/controller/ExportController.java
//package com.example.restaurant.controller;
//
//import com.example.restaurant.model.Dish;
//import com.example.restaurant.model.Reservation;
//import com.example.restaurant.model.User;
//import com.example.restaurant.service.DishService;
//import com.example.restaurant.service.ReservationService;
//import com.example.restaurant.service.UserService;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.dataformat.xml.XmlMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/export")
//@RequiredArgsConstructor
//public class ExportController {
//
//    private final DishService dishService;
//    private final UserService userService;
//    private final ReservationService reservationService;
//
//    // --- MENU EXPORT (For everyone) ---
//
//    @GetMapping("/menu/json")
//    public ResponseEntity<String> exportMenuJson() throws JsonProcessingException {
//        List<Dish> dishes = dishService.findAllDishes();
//        return createJsonResponse(dishes, "menu.json");
//    }
//
//    @GetMapping("/menu/xml")
//    public ResponseEntity<String> exportMenuXml() throws JsonProcessingException {
//        List<Dish> dishes = dishService.findAllDishes();
//        return createXmlResponse(dishes, "menu.xml", "Menu");
//    }
//
//    // --- ADMIN EXPORTS (For Admin only - Secured in SecurityConfig) ---
//
//    @GetMapping("/users/json")
//    public ResponseEntity<String> exportUsersJson() throws JsonProcessingException {
//        List<User> users = userService.findAllUsers();
//        return createJsonResponse(users, "users.json");
//    }
//
//    @GetMapping("/users/xml")
//    public ResponseEntity<String> exportUsersXml() throws JsonProcessingException {
//        List<User> users = userService.findAllUsers();
//        return createXmlResponse(users, "users.xml", "Users");
//    }
//
//    @GetMapping("/reservations/json")
//    public ResponseEntity<String> exportReservationsJson() throws JsonProcessingException {
//        List<Reservation> reservations = reservationService.findAllReservations();
//        return createJsonResponse(reservations, "reservations.json");
//    }
//
//    @GetMapping("/reservations/xml")
//    public ResponseEntity<String> exportReservationsXml() throws JsonProcessingException {
//        List<Reservation> reservations = reservationService.findAllReservations();
//        return createXmlResponse(reservations, "reservations.xml", "Reservations");
//    }
//
//
//    // --- Helpers ---
//
//    private ResponseEntity<String> createJsonResponse(Object data, String filename) throws JsonProcessingException {
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JavaTimeModule()); // Handle Dates
//        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
//        return buildFileResponse(json, filename, MediaType.APPLICATION_JSON);
//    }
//
//    private ResponseEntity<String> createXmlResponse(Object data, String filename, String rootName) throws JsonProcessingException {
//        XmlMapper mapper = new XmlMapper();
//        mapper.registerModule(new JavaTimeModule());
//        String xml = mapper.writerWithDefaultPrettyPrinter().withRootName(rootName).writeValueAsString(data);
//        return buildFileResponse(xml, filename, MediaType.APPLICATION_XML);
//    }
//
//    private ResponseEntity<String> buildFileResponse(String content, String filename, MediaType mediaType) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(mediaType);
//        headers.setContentDispositionFormData("attachment", filename);
//        return ResponseEntity.ok().headers(headers).body(content);
//    }
//}
// src/main/java/com/example/restaurant/controller/ExportController.java
package com.example.restaurant.controller;

import com.example.restaurant.model.*;
import com.example.restaurant.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/export")
@RequiredArgsConstructor
public class ExportController {

    private final DishService dishService;
    private final UserService userService;
    private final ReservationService reservationService;

    @GetMapping("/menu/json")
    public ResponseEntity<String> exportMenuJson() throws JsonProcessingException {
        return createJsonResponse(dishService.findAllDishes(), "menu.json");
    }

    @GetMapping("/menu/xml")
    public ResponseEntity<String> exportMenuXml() throws JsonProcessingException {
        return createXmlResponse(dishService.findAllDishes(), "menu.xml", "Menu");
    }

    @GetMapping("/users/json")
    public ResponseEntity<String> exportUsersJson() throws JsonProcessingException {
        return createJsonResponse(userService.findAllUsers(), "users.json");
    }

    @GetMapping("/users/xml")
    public ResponseEntity<String> exportUsersXml() throws JsonProcessingException {
        return createXmlResponse(userService.findAllUsers(), "users.xml", "Users");
    }

    @GetMapping("/reservations/json")
    public ResponseEntity<String> exportReservationsJson() throws JsonProcessingException {
        return createJsonResponse(reservationService.findAllReservations(), "reservations.json");
    }

    @GetMapping("/reservations/xml")
    public ResponseEntity<String> exportReservationsXml() throws JsonProcessingException {
        return createXmlResponse(reservationService.findAllReservations(), "reservations.xml", "Reservations");
    }

    private ResponseEntity<String> createJsonResponse(Object data, String filename) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        return buildFileResponse(json, filename, MediaType.APPLICATION_JSON);
    }

    private ResponseEntity<String> createXmlResponse(Object data, String filename, String rootName) throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();
        mapper.registerModule(new JavaTimeModule());
        String xml = mapper.writerWithDefaultPrettyPrinter().withRootName(rootName).writeValueAsString(data);
        return buildFileResponse(xml, filename, MediaType.APPLICATION_XML);
    }

    private ResponseEntity<String> buildFileResponse(String content, String filename, MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setContentDispositionFormData("attachment", filename);
        return ResponseEntity.ok().headers(headers).body(content);
    }
}