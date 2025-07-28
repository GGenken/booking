package dev.genken.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import dev.genken.backend.dto.CoworkingLayoutDto;
import dev.genken.backend.dto.SeatCoordinateDto;
import dev.genken.backend.dto.TimeRangeDto;
import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Role;
import dev.genken.backend.entity.Seat;
import dev.genken.backend.serialization.AnonymousReservationSerializer;
import dev.genken.backend.service.SeatService;
import dev.genken.backend.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@Tag(name = "Seats")
@RequestMapping("/api/seats")
public class SeatController {
    private final SeatService seatService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SeatController(SeatService seatService, ObjectMapper objectMapper) {
        this.seatService = seatService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get all reservations for a seat",
        description = "Get a list of all reservations for a specific seat"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = """
                Successfully fetched the reservations;
                the response will be anonymized if the user is not an administrator
            """),
        @ApiResponse(responseCode = "404", description = "Seat was not found")
    })
    public ResponseEntity<String> getReservationsBySeat(@PathVariable Long id, @AuthenticationPrincipal User user)
        throws JsonProcessingException {
        Seat seat = seatService.getSeatById(id);
        List<Reservation> reservations = seat.getReservations();

        if (user.getRole() != Role.ADMIN) {
            SimpleModule module = new SimpleModule();
            module.addSerializer(Reservation.class, new AnonymousReservationSerializer());
            objectMapper.registerModule(module);
        }
        String response = objectMapper.writeValueAsString(reservations);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @GetMapping("/available")
    @Operation(
        summary = "Get all available seats for a specific time",
        description = """
            Fetch all seats that are available within the specified time range;
            the response contains seat IDs for the available seats
        """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Successfully fetched the available seats",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(type = "array", implementation = Long.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Invalid input, check time format"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public ResponseEntity<List<SeatCoordinateDto>> getAvailableSeats(@RequestParam(required = false) TimeRangeDto timeRange) {
        if  (timeRange == null) {
            var currentTime = LocalDateTime.now();
            timeRange = new TimeRangeDto(currentTime, currentTime);
        }
        List<Seat> availableSeats = seatService.getAvailableSeats(timeRange.getStartTime(), timeRange.getEndTime());
        List<SeatCoordinateDto> availableSeatsDto = availableSeats.stream().map(SeatCoordinateDto::new).toList();

        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(availableSeatsDto);
    }

    @GetMapping("/layout")
    @Operation(
        summary = "Get layout of the coworking space"
    )
    public ResponseEntity<List<SeatCoordinateDto>> getCoworkingLayout() {
        List<Seat> allSeats = seatService.getAllSeats();
        List<SeatCoordinateDto> seatCoordinates = new ArrayList<>();

        for (Seat seat : allSeats) {
            seatCoordinates.add(new SeatCoordinateDto(seat));
        }

        return ResponseEntity.ok(seatCoordinates);
    }
}
