package dev.genken.backend.service;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.repository.ReservationRepository;
import dev.genken.backend.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SeatService {
    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository, ReservationRepository reservationRepository) {
        this.seatRepository = seatRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Seat> getAllSeats() { return seatRepository.findAll(); }

    public Optional<Seat> getSeatById(Long id) { return seatRepository.findById(id); }

    public boolean seatExists(int row, int column) {
        return seatRepository.findAll().stream().anyMatch(seat -> seat.getRow() == row && seat.getCol() == column);
    }

    public List<Seat> getAvailableSeats(LocalDateTime startTime, LocalDateTime endTime) {
        List<Seat> allSeats = seatRepository.findAll();
        return allSeats.stream().filter(seat -> isSeatAvailable(seat, startTime, endTime)).collect(Collectors.toList());
    }

    // have to re-implement this, since isOverlapping() is in reservation service
    // 'll move this to repository eventually
    private boolean isSeatAvailable(Seat seat, LocalDateTime startTime, LocalDateTime endTime) {
        List<Reservation> reservations = reservationRepository.findBySeat(seat);
        for (Reservation reservation : reservations) {
            if (startTime.isBefore(reservation.getEndTime()) && endTime.isAfter(reservation.getStartTime())) {
                return false;
            }
        }
        return true;
    }

}
