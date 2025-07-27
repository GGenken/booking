package dev.genken.backend.service;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAllSeats() { return seatRepository.findAll(); }

    public Seat getSeatById(Long id) {
        return seatRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Seat not found"));
    }

    public List<Seat> getAvailableSeats(LocalDateTime startTime, LocalDateTime endTime) {
        return seatRepository.findAvailableSeats(startTime, endTime);
    }

    private boolean isSeatAvailable(Seat seat, LocalDateTime startTime, LocalDateTime endTime) {
        return getAvailableSeats(startTime, endTime).contains(seat);
    }

    public List<Reservation> getReservations(Seat seat) {
        return seat.getReservations();
    }
}
