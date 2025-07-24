package dev.genken.backend.controller;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Role;
import dev.genken.backend.service.ReservationService;
import dev.genken.backend.entity.User;
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
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@AuthenticationPrincipal User user, @RequestParam Long seatId, @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        if (user == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
        Reservation reservation = reservationService.createReservation(seatId, user, startTime, endTime);
        return ResponseEntity.created(URI.create("/api/reservations/" + reservation.getId())).body(reservation);
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations(@AuthenticationPrincipal User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user.getRole() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Reservation reservation = reservationService.getReservationById(id);
            boolean allowed = user.getRole() == Role.ADMIN ||
                user.getRole() == Role.USER && reservation.getUser() == user;
            if (allowed) { return ResponseEntity.ok(reservation); }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(@RequestBody Reservation reservation, @AuthenticationPrincipal User user, @PathVariable Long id) {
        if (user.getRole() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            Reservation reservationToUpdate = reservationService.getReservationById(id);
            if (user.getRole() != Role.ADMIN && reservation.getUser() != user) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            boolean allowed = user.getRole() == Role.ADMIN ||
                user.getRole() == Role.USER && reservationToUpdate.getUser() == user;
            if (!allowed) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }
            Reservation updatedReservation = reservationService.updateReservation(id, reservation);
            return ResponseEntity.ok(updatedReservation);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id, @AuthenticationPrincipal User user) {
        if (user.getRole() == null) { return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
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
