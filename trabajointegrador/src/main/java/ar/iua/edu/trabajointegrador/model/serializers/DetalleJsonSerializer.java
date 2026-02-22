package ar.iua.edu.trabajointegrador.model.serializers;

import ar.iua.edu.trabajointegrador.model.Detalle;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class DetalleJsonSerializer extends StdSerializer<Detalle> {
    public DetalleJsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    // serealizer: id, acummulated mass, density, flow, temperature, timestamp
    @Override
    public void serialize(Detalle detalle, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        // Campo ID
        jsonGenerator.writeNumberField("id", detalle.getId());

        // Campo acummulated mass
        jsonGenerator.writeNumberField("accumulatedMass", detalle.getMasaAcumulada());

        // Campo density
        jsonGenerator.writeNumberField("density", detalle.getDensidad());

        // Campo flow
        jsonGenerator.writeNumberField("flowRate", detalle.getCaudal());

        // Campo temperature
        jsonGenerator.writeNumberField("temperature", detalle.getTemperatura());

        // Campo timestamp
        jsonGenerator.writeStringField("timeStamp", detalle.getFecha().toString());

        jsonGenerator.writeEndObject();
    }
}
