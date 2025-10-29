package ar.iua.edu.trabajointegrador.models;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;




public class ProductoJsonSerializer  extends StdSerializer<Producto>{

    
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
        gen.writeStringField("producto", value.getProducto()); //"producto" : 
        gen.writeEndObject(); //}

    }
    
    
}
