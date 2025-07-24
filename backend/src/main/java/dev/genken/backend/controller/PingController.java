package dev.genken.backend.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;


@RestController
@Tag(name = "Ping")
public class PingController {
    @GetMapping("/api/ping")
    public ResponseEntity<Void> ping() { return ResponseEntity.ok().build(); }
}
