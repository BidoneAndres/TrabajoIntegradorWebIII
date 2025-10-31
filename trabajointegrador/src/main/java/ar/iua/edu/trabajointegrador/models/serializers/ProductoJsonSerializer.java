package ar.iua.edu.trabajointegrador.models.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.Producto;





public class ProductoJsonSerializer  extends StdSerializer<Producto>{

    //Comenzamos la generacion de Jsons....
    
    protected ProductoJsonSerializer(Class<Producto> t){
        super (t);
    }
    
    /*
     * {
     *  "id" : ;
     *  "producto" :
     * }
     */
    @Override
    public void serialize(Producto value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject(); //{
        
        gen.writeNumberField("id", value.getId()); //"id" :
        gen.writeStringField("nombre", value.getProducto()); //"producto" : 

        gen.writeEndObject(); //}

    }
    
    
}
