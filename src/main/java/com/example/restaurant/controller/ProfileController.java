// src/main/java/com/example/restaurant/controller/ProfileController.java
package com.example.restaurant.controller;

import com.example.restaurant.model.Reservation;
import com.example.restaurant.model.User;
import com.example.restaurant.service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Import for Java 8 dates
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ReservationService reservationService;

    @GetMapping("/export/json")
    public ResponseEntity<String> exportReservationsAsJson() throws JsonProcessingException {
        // 1. Get the currently logged-in user
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        // 2. Get their reservations
        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);

        // 3. Configure an ObjectMapper to handle Java 8 dates
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // 4. Convert the list to a JSON string
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservations);

        // 5. Create headers for the file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDispositionFormData("attachment", "reservations.json");

        return ResponseEntity.ok().headers(headers).body(json);
    }

    @GetMapping("/export/xml")
    public ResponseEntity<String> exportReservationsAsXml() throws JsonProcessingException {
        // 1. Get the currently logged-in user
        User currentUser = getAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        // 2. Get their reservations
        List<Reservation> reservations = reservationService.findReservationsByUser(currentUser);

        // 3. Configure an XmlMapper (from the new dependency)
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());

        // 4. Convert the list to an XML string
        // We wrap it in a <reservations> tag for proper XML structure
        String xml = xmlMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservations);
        xml = "<reservations>\n" + xml + "\n</reservations>";

        // 5. Create headers for the file download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDispositionFormData("attachment", "reservations.xml");

        return ResponseEntity.ok().headers(headers).body(xml);
    }

    /**
     * Helper method to get the User object from the security context.
     */
    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            return (User) authentication.getPrincipal();
        }
        return null;
    }
}