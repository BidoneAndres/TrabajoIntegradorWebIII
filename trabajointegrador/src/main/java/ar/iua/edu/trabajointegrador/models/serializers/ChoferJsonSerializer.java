package ar.iua.edu.trabajointegrador.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.models.Chofer;




public class ChoferJsonSerializer  extends StdSerializer<Chofer>{

    //Comenzamos la generacion de Jsons....
    
    protected ChoferJsonSerializer(Class<Chofer> t){
        super (t);
    }
    
    /*
     * {
     *  "id" : ;
     *  "nombre" :
     *  "apellido" :
     *  "documento" :
     * }
     */
    @Override
    public void serialize(Chofer value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject(); //{
        
        gen.writeNumberField("id", value.getId()); //"id" :
        gen.writeStringField("nombre", value.getNombre()); //"nombre" : 
        gen.writeStringField("apellido", value.getApellido()); //"apellido" : 
        gen.writeStringField("documento", value.getDocumento());

        gen.writeEndObject(); //}

    }
    
    
}
