package dev.genken.backend.service;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.repository.ReservationRepository;
import dev.genken.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static java.time.Duration.*;

@Service
public class ReservationService {

    private final SeatService seatService;
    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(SeatService seatService, ReservationRepository reservationRepository) {
        this.seatService = seatService;
        this.reservationRepository = reservationRepository;
    }

    public Reservation createReservation(Long seatId, User user, LocalDateTime startTime, LocalDateTime endTime) {
        Seat seat = seatService.getSeatById(seatId).orElseThrow(() -> new NoSuchElementException("Seat not found"));

        if (isOverlapping(seat, startTime, endTime)) {
            throw new IllegalArgumentException("The reservation overlaps with an existing reservation");
        }
        long durationMinutes = between(startTime, endTime).toMinutes();
        if (durationMinutes < 5) {
            throw new IllegalArgumentException("The reservation duration must be at least 5 minutes");
        } else if (durationMinutes > 24 * 60) {
            throw new IllegalArgumentException("The reservation duration cannot exceed 24 hours");
        }

        Reservation reservation = new Reservation(seat, user, startTime, endTime);

        return reservationRepository.save(reservation);
    }

    private boolean isOverlapping(Seat seat, LocalDateTime startTime, LocalDateTime endTime) {
        List<Reservation> overlappingReservations = reservationRepository.findBySeat(seat);
        for (Reservation existingReservation : overlappingReservations) {
            if (startTime.isBefore(existingReservation.getEndTime()) && endTime.isAfter(existingReservation.getStartTime())) {
                return true;
            }
        }
        return false;
    }

    public List<Reservation> getAllReservations() { return reservationRepository.findAll(); }

    public List<Reservation> getReservationsByUser(User user) {
        return reservationRepository.findByUser(user);
    }

    public Reservation getReservationById(Long id) {
        return reservationRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Reservation not found"));
    }

    public Reservation updateReservation(Long id, Reservation updatedReservation) {
        if (!reservationRepository.existsById(id)) {
            throw new NoSuchElementException("Reservation not found");
        }

        updatedReservation.setId(id);
        return reservationRepository.save(updatedReservation);
    }

    public void deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) { reservationRepository.deleteById(id); }
        throw new NoSuchElementException("Reservation not found");
    }
}
