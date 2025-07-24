package dev.genken.backend.service;

import dev.genken.backend.repository.SeatRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SeatInitializationService implements InitializingBean {
    private final SeatRepository seatRepository;

    @Value("${seat.rows}")
    private int numRows;

    @Value("${seat.columns}")
    private int numCols;

    public SeatInitializationService(SeatRepository seatRepository) {
        this.seatRepository = seatRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        seatRepository.deleteExcessiveSeats(numRows, numCols);
        for (int row = 1; row <= numRows; ++row) {
            for (int col = 1; col <= numCols; ++col) {
                seatRepository.insertSeatIfNotExists(row, col);
            }
        }

        System.out.println("Seats init and cleanup done. Total seats: " + (numRows * numCols));
    }
}
