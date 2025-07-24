package dev.genken.backend.controller;

import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.serialization.AnonymousReservationSerializer;
import dev.genken.backend.service.ReservationService;
import dev.genken.backend.service.SeatService;
import dev.genken.backend.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@Tag(name = "Seats")
@RequestMapping("/api/seats")
public class SeatController {
    private final SeatService seatService;
    private final ReservationService reservationService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SeatController(SeatService seatService, ReservationService reservationService, ObjectMapper objectMapper) {
        this.seatService = seatService;
        this.reservationService = reservationService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get all reservations for a seat", description = "Get a list of all reservations for a specific seat")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully fetched the reservations; the response will be anonymized if the user is not an administrator"), @ApiResponse(responseCode = "404", description = "Seat was not found")})
    public ResponseEntity<String> getReservationsBySeat(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Optional<Seat> seat = seatService.getSeatById(id);
        if (seat.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Reservation> reservations = reservationService.getReservationsBySeat(seat.get());
        try {
            if (user.getRole() != Role.ADMIN) {
                SimpleModule module = new SimpleModule();
                module.addSerializer(Reservation.class, new AnonymousReservationSerializer());
                objectMapper.registerModule(module);
            }
            String response = objectMapper.writeValueAsString(reservations);
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/available")
    @Operation(summary = "Get all available seats for a specific time", description = "Fetch all seats that are available within the specified time range. The response contains seat IDs for the available seats.")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully fetched the available seats", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "array", implementation = Long.class))), @ApiResponse(responseCode = "400", description = "Invalid input, check time format"), @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public ResponseEntity<long[]> getAvailableSeats(@RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime, @AuthenticationPrincipal User user) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Seat> availableSeats = seatService.getAvailableSeats(startTime, endTime);

        long[] availableSeatIds = availableSeats.stream().mapToLong(Seat::getId).toArray();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(availableSeatIds);
    }
}
