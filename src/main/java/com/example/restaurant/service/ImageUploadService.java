// src/main/java/com/example/restaurant/service/ImageUploadService.java
package com.example.restaurant.service;

import org.springframework.stereotype.Service;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageUploadService {

    // Save to "uploads/dishes" in the project root
    private final String uploadDir = "uploads/dishes";

    public ImageUploadService() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String saveImage(String fileName, InputStream inputStream) throws IOException {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        Path destinationFile = Paths.get(uploadDir).resolve(uniqueFileName)
                .normalize().toAbsolutePath();

        try (FileOutputStream os = new FileOutputStream(destinationFile.toFile())) {
            inputStream.transferTo(os);
        }

        return "/images/dishes/" + uniqueFileName;
    }
}