package com.example.util;

import jakarta.inject.Singleton;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Singleton
public class FileLogger {
    private static final Path LOG_PATH = Path.of("received-messages.log");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public synchronized void log(String message) {
        try {
            if (Files.notExists(LOG_PATH)) {
                Files.createFile(LOG_PATH);
            }
            String line = String.format("[%s] %s%n",
                    LocalDateTime.now().format(FORMATTER), message);
            try (FileWriter fw = new FileWriter(LOG_PATH.toFile(), true)) {
                fw.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
