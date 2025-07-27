package dev.genken.backend.dto;

import java.time.LocalDateTime;

public class TimeRangeDto {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public TimeRangeDto(LocalDateTime startTime, LocalDateTime endTime) {
        setStartTime(startTime);
        setEndTime(endTime);
    }

    public TimeRangeDto() {}

    public LocalDateTime getStartTime() { return startTime; }

    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }

    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
