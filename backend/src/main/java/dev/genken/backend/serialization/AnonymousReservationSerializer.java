package dev.genken.backend.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.genken.backend.entity.Reservation;

import java.io.IOException;

public class AnonymousReservationSerializer extends StdSerializer<Reservation> {
    public AnonymousReservationSerializer() { this(null); }

    public AnonymousReservationSerializer(Class<Reservation> t) { super(t); }

    @Override
    public void serialize(Reservation reservation, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeNumberField("id", reservation.getId());
        jsonGenerator.writeObjectField("startTime", reservation.getStartTime());
        jsonGenerator.writeObjectField("endTime", reservation.getEndTime());

        jsonGenerator.writeEndObject();
    }
}
