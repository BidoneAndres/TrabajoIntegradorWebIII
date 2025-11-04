package ar.iua.edu.trabajointegrador.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.Cliente;





public class ClienteJsonSerializer  extends StdSerializer<Cliente>{

    //Comenzamos la generacion de Jsons....
    
    protected ClienteJsonSerializer(Class<Cliente> t){
        super (t);
    }
    
    /*
     * {
     *  "id" : ;
     *  "producto" :
     *  "descripcion" :
     * }
     */
    @Override
    public void serialize(Cliente value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject(); //{
        
        gen.writeNumberField("id", value.getId()); //"id" :
        gen.writeStringField("razonSocial", value.getRazonSocial()); //"razon social" : 
        gen.writeStringField("email", value.getEmail()); //"email" : 


        gen.writeEndObject(); //}

    }
    
    
}
