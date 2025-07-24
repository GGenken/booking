package dev.genken.backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    public Reservation() {}

    public Reservation(Seat seat, User user, LocalDateTime startTime, LocalDateTime endTime) {
        setSeat(seat);
        setUser(user);
        setStartTime(startTime);
        setEndTime(endTime);
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public Seat getSeat() { return seat; }

    public void setSeat(Seat seat) { this.seat = seat; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public int getSeatRow() { return seat.getRow(); }

    public int getSeatCol() { return seat.getCol(); }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
