package dev.genken.backend.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.genken.backend.entity.Reservation;
import dev.genken.backend.entity.User;

import java.io.IOException;
import java.util.List;

public class UserSerializer extends JsonSerializer<User> {
    @Override
    public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeStringField("uuid", user.getUuid().toString());
        jsonGenerator.writeStringField("username", user.getUsername());

//        List<Long> reservationIds = user.getReservations().stream().map(Reservation::getId).toList();
//        jsonGenerator.writeFieldName("reservationIds");
//        jsonGenerator.writeStartArray();
//        for (Long reservationId : reservationIds) {
//            jsonGenerator.writeNumber(reservationId);
//        }
//        jsonGenerator.writeEndArray();

        jsonGenerator.writeEndObject();
    }
}
