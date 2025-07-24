package dev.genken.backend.repository;

import dev.genken.backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Seat s WHERE s.row > :maxRow OR s.col > :maxCol")
    void deleteExcessiveSeats(int maxRow, int maxCol);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO seats (row, col) VALUES (?1, ?2) ON CONFLICT (row, col) DO NOTHING", nativeQuery = true)
    void insertSeatIfNotExists(int row, int col);
}
