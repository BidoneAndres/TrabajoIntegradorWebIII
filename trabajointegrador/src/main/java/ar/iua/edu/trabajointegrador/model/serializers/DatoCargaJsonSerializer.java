package ar.iua.edu.trabajointegrador.model.serializers;

import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.DatoCarga;

public class DatoCargaJsonSerializer extends StdSerializer<DatoCarga> {
    public DatoCargaJsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    @Override
    public void serialize(DatoCarga datoCarga, com.fasterxml.jackson.core.JsonGenerator jsonGenerator, com.fasterxml.jackson.databind.SerializerProvider serializerProvider) throws java.io.IOException {
        jsonGenerator.writeStartObject();

        // Campo ID
        jsonGenerator.writeNumberField("id", datoCarga.getId());

        // Campo acummulated mass
        jsonGenerator.writeNumberField("accumulatedMass", datoCarga.getMasaAcumulada());

        // Campo density
        jsonGenerator.writeNumberField("density", datoCarga.getDensidadProducto());

        // Campo flow
        jsonGenerator.writeNumberField("flowRate", datoCarga.getCaudal());

        // Campo temperature
        jsonGenerator.writeNumberField("temperature", datoCarga.getTemperatura());

        // Campo timestamp
        jsonGenerator.writeStringField("timeStamp", datoCarga.getTimestamp().toString());

        jsonGenerator.writeEndObject();
    }
    
}
