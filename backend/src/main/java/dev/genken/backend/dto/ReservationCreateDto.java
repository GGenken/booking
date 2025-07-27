package dev.genken.backend.dto;

import java.time.LocalDateTime;

public class ReservationCreateDto {
    private Long seatId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Long getSeatId() { return seatId; }

    public void setSeatId(Long seatId) { this.seatId = seatId; }

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
