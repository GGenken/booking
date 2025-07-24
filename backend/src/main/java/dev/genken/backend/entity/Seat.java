package dev.genken.backend.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dev.genken.backend.serialization.SeatSerializer;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;


import java.util.List;

@Entity
@Table(name = "seats", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"row", "col"})
})
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Min(1)
    private int row;

    @Column(nullable = false)
    @Min(1)
    private int col;

    @OneToMany(mappedBy = "seat", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonSerialize(using = SeatSerializer.class)
    private List<Reservation> reservations;

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public int getRow() { return row; }

    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }

    public void setCol(int col) { this.col = col; }

    public List<Reservation> getReservations() { return reservations; }

    public void setReservations(List<Reservation> reservations) { this.reservations = reservations; }
}
