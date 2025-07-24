package dev.genken.backend.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.Seat;

import java.io.IOException;

public class ReservationSerializer extends StdSerializer<Reservation> {
    public ReservationSerializer() { this(null); }

    public ReservationSerializer(Class<Reservation> t) { super(t); }

    @Override
    public void serialize(Reservation reservation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("id", reservation.getId());
        jsonGenerator.writeObjectField("startTime", reservation.getStartTime());
        jsonGenerator.writeObjectField("endTime", reservation.getEndTime());

        Seat seat = reservation.getSeat();
        if (seat != null) {
            jsonGenerator.writeObjectFieldStart("seat");
            jsonGenerator.writeNumberField("id", seat.getId());
            jsonGenerator.writeNumberField("row", seat.getRow());
            jsonGenerator.writeNumberField("col", seat.getCol());
            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndObject();
    }
}
