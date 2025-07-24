package dev.genken.backend.controller;

import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.service.SeatService;
import dev.genken.backend.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@Tag(name = "Seats")
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    @Autowired
    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get all reservations for a seat", description = "Get a list of all reservations for a specific seat")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Reservations fetched successfully"),
        @ApiResponse(responseCode = "404", description = "Seat not found")})
    public ResponseEntity<Seat> getReservationsBySeat(
        @PathVariable Long id,
        @AuthenticationPrincipal User user) {
        if (user == null || user.getRole() != Role.ADMIN) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
        try {
            Optional<Seat> seat = seatService.getSeatById(id);
            return seat.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
