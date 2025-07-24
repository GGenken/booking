package dev.genken.backend.service;

import dev.genken.backend.entity.Seat;
import dev.genken.backend.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatService {
    private final SeatRepository seatRepository;

    @Autowired
    public SeatService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    public List<Seat> getAllSeats() {
        return seatRepository.findAll();
    }

    public Optional<Seat> getSeatById(Long id) {
        return seatRepository.findById(id);
    }

    public boolean seatExists(int row, int column) {
        return seatRepository.findAll().stream().anyMatch(seat -> seat.getRow() == row && seat.getCol() == column);
    }
}
