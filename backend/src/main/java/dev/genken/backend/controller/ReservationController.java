package dev.genken.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Role;
import dev.genken.backend.service.ReservationService;
import dev.genken.backend.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@Tag(name = "Reservations")
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    @Operation(summary = "Create a new reservation", description = "Create a new reservation for a user, specifying seat and times")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Reservation created successfully"), @ApiResponse(responseCode = "401", description = "Unauthorized - User not authenticated"), @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input")})
    public ResponseEntity<Reservation> createReservation(@AuthenticationPrincipal User user, @Parameter(description = "ID of the seat to be reserved") @RequestParam Long seatId, @Parameter(description = "Start time of the reservation") @RequestParam LocalDateTime startTime, @Parameter(description = "End time of the reservation") @RequestParam LocalDateTime endTime) {
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Reservation reservation = reservationService.createReservation(seatId, user, startTime, endTime);
        return ResponseEntity.created(URI.create("/api/reservations/" + reservation.getId())).body(reservation);
    }

    @GetMapping
    @Operation(summary = "Get all reservations", description = "Get a list of all reservations (administrator privileges required)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Reservations fetched successfully"), @ApiResponse(responseCode = "403", description = "Forbidden - administrator privileges required")})
    public ResponseEntity<List<Reservation>> getAllReservations(@AuthenticationPrincipal User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/my")
    @Operation(
        summary = "Get reservations for the authenticated user",
        description = "Fetches all reservations for the currently authenticated user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the reservations"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<Reservation>> getMyReservations(@AuthenticationPrincipal User user) {
        if (user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        List<Reservation> reservations = reservationService.getReservationsByUser(user);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID", description = "Fetch a reservation by its ID (available to administrators and owner)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Reservation fetched successfully"), @ApiResponse(responseCode = "404", description = "Reservation not found"), @ApiResponse(responseCode = "403", description = "Forbidden - available to administrators and owner")})
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id, @AuthenticationPrincipal User user) {

        try {
            Reservation reservation = reservationService.getReservationById(id);
            boolean allowed = user.getRole() == Role.ADMIN || user.getRole() == Role.USER && reservation.getUser() == user;

            if (allowed) {
                return ResponseEntity.ok(reservation);
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing reservation", description = "Update an existing reservation (available to administrators and owner)")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Reservation updated successfully"), @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to update this reservation."), @ApiResponse(responseCode = "404", description = "Reservation not found.")})
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation, @AuthenticationPrincipal User user, @PathVariable Long id) {
        try {
            Reservation reservationToUpdate = reservationService.getReservationById(id);
            if (user.getRole() != Role.ADMIN && reservation.getUser() != user) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            Reservation updatedReservation = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a reservation", description = "Delete an existing reservation (available to administrators and owner)")
    @ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Reservation deleted successfully"), @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized to delete this reservation."), @ApiResponse(responseCode = "404", description = "Reservation not found.")})
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, @AuthenticationPrincipal User user) {
        try {
            Reservation reservationToDelete = reservationService.getReservationById(id);
            boolean allowed = user.getRole() == Role.ADMIN || reservationToDelete.getUser() == user;

            if (allowed) {
                reservationService.deleteReservation(id);
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
