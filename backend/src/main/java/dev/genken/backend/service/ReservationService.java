package dev.genken.backend.service;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.repository.ReservationRepository;
import dev.genken.backend.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

        Reservation reservation = new Reservation(seat, user, startTime, endTime);

        return reservationRepository.save(reservation);
    }
}
