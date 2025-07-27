package dev.genken.backend.dto;

import dev.genken.backend.entity.Seat;

public class SeatCoordinateDto {
    private long id;
    private int row;
    private int col;

    public SeatCoordinateDto(long id, int row, int col) {
        this.id = id;
        this.row = row;
        this.col = col;
    }

    public SeatCoordinateDto(Seat seat) {
        setId(Math.toIntExact(seat.getId()));
        setRow(seat.getRow());
        setCol(seat.getCol());
    }

    public long getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
