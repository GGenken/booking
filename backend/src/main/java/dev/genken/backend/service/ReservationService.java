package dev.genken.backend.service;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.repository.ReservationRepository;
import dev.genken.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        if (LocalDateTime.now().isAfter(endTime)) {
            throw new IllegalArgumentException("Reservation's end must not be before current time");
        }
        if (startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("Reservations's start must not be before current time");
        }
        Seat seat = seatService.getSeatById(seatId);
        long durationMinutes = between(startTime, endTime).toMinutes();
        if (durationMinutes < 5) {
            throw new IllegalArgumentException("The reservation duration must be at least 5 minutes");
        } else if (durationMinutes > 24 * 60) {
            throw new IllegalArgumentException("The reservation duration cannot exceed 24 hours");
        }

        Reservation reservation = new Reservation(seat, user, startTime, endTime);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getAllReservations() { return reservationRepository.findAll(); }

    public List<Reservation> getReservationsByUser(User user) { return reservationRepository.findByUser(user); }

    public List<Reservation> getReservationsBySeat(Seat seat) { return reservationRepository.findBySeat(seat); }

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
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
        } else {
            throw new NoSuchElementException("Reservation not found");
        }
    }
}
