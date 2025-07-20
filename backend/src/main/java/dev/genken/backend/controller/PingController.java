package dev.genken.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


@RestController
public class PingController {
    @GetMapping("/api/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.ok().build();
    }
}
