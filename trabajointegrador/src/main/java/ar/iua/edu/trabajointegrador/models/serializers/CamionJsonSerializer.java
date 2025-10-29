package ar.iua.edu.trabajointegrador.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.models.Camion;




public class CamionJsonSerializer  extends StdSerializer<Camion>{

    //Comenzamos la generacion de Jsons....
    
    protected CamionJsonSerializer(Class<Camion> t){
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
    public void serialize(Camion value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject(); //{
        
        gen.writeNumberField("id", value.getId()); //"id" :
        gen.writeStringField("patente", value.getPatente()); //"patente" : 
        gen.writeStringField("descripcion", value.getDescripcion()); //"descripcion" : 


        gen.writeEndObject(); //}

    }
    
    
}
