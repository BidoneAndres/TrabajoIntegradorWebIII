package ar.iua.edu.trabajointegrador.model.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class FechaJsonSerializer extends StdSerializer<Date> {

    // Define el formato de fecha y hora requerido
    private static final SimpleDateFormat FORMATTER = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm");

    // Constructor sin argumentos requerido por Jackson
    public FechaJsonSerializer() {
        this(null);
    }
    public FechaJsonSerializer(Class<Date> t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        
        String formattedDate = FORMATTER.format(value);
        
        gen.writeString(formattedDate);
    }
}