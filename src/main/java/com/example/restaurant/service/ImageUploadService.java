// src/main/java/com/example/restaurant/service/ImageUploadService.java
package com.example.restaurant.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class ImageUploadService {

    // Путь к твоей папке static/images/dishes
    // (ВАЖНО: Убедись, что папки 'static/images/dishes' существуют)
    private final String uploadDir = "src/main/resources/static/images/dishes/";

    public ImageUploadService() {
        // Создаем папку, если ее нет
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    /**
     * Saves an uploaded file to the 'static/images/dishes' directory.
     *
     * @param fileName The original name of the file (e.g., "khinkali.jpg")
     * @param inputStream The data stream of the file
     * @return The public URL path (e.g., "/images/dishes/unique-name.jpg")
     * @throws IOException
     */
    public String saveImage(String fileName, InputStream inputStream) throws IOException {
        // 1. Создаем уникальное имя файла, чтобы избежать конфликтов
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;

        // 2. Определяем полный путь для сохранения
        Path destinationFile = Paths.get(uploadDir).resolve(uniqueFileName)
                .normalize().toAbsolutePath();

        // 3. Копируем данные из inputStream в файл
        try (FileOutputStream os = new FileOutputStream(destinationFile.toFile())) {
            inputStream.transferTo(os);
        }

        // 4. Возвращаем ПУБЛИЧНЫЙ URL, который будет храниться в базе
        return "/images/dishes/" + uniqueFileName;
    }
}