package dev.genken.backend.dto;

import java.time.LocalDateTime;

public class ReservationUpdateDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
