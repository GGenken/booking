package dev.genken.backend.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.genken.backend.entity.Seat;

import java.io.IOException;

public class SeatSerializer extends StdSerializer<Seat> {
    public SeatSerializer() { this(null); }

    public SeatSerializer(Class<Seat> t) { super(t); }

    @Override
    public void serialize(Seat seat, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("id", seat.getId());
        jsonGenerator.writeNumberField("row", seat.getRow());
        jsonGenerator.writeNumberField("col", seat.getCol());
        jsonGenerator.writeEndObject();
    }
}
