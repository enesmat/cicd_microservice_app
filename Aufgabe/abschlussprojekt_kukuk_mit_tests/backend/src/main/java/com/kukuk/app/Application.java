package com.kukuk.app;

// Importiere benötigte Spring Boot-Klassen
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

// Markiert das Projekt als Spring Boot Anwendung + aktiviert automatische Konfiguration
@SpringBootApplication

// Einfacher REST-Controller direkt in der Main-Klasse
@RestController
public class Application {

    // Einstiegspunkt der Anwendung
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    // Einfacher HTTP-Endpunkt für Testzwecke (Jenkins)
    @GetMapping("/")
    public String start() {
        return "Kukuk App läuft";
    }
}
