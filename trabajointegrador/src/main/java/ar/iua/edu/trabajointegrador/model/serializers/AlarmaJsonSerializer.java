package ar.iua.edu.trabajointegrador.model.serializers;


import ar.iua.edu.trabajointegrador.model.Alarma;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class AlarmaJsonSerializer extends StdSerializer<Alarma>{
    public AlarmaJsonSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }
    @Override
    public void serialize(Alarma value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getId());
        gen.writeStringField("estado", value.getEstado().toString());
        gen.writeStringField("tiempo", value.getTiempo().toString());
        gen.writeEndObject();
        String user = value.getUser() != null ? value.getUser().getUsername() : null;
        gen.writeStringField("user", user);
        String descripcion = value.getDescripcion() != null ? value.getDescripcion() : null;
        gen.writeStringField("descripcion", descripcion);
        gen.writeNumberField("temperatura", value.getTemperatura() != null ? Double.parseDouble(value.getTemperatura()) : 0);
        gen.writeEndObject();
    }
}
