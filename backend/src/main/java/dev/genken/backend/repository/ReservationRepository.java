package dev.genken.backend.repository;

import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
}