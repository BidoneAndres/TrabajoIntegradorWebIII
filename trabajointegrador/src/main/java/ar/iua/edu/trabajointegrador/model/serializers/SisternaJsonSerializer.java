
package ar.iua.edu.trabajointegrador.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.Sisterna;






public class SisternaJsonSerializer  extends StdSerializer<Sisterna>{

    //Comenzamos la generacion de Jsons....
    
    protected SisternaJsonSerializer(Class<Sisterna> t){
        super (t);
    }
    
    /*
     * {
     *  "id" :
     *  "camion" :{
     *              ....
     *              } ;
     *  "capacidad" : 
     *  "licencia"  :
     * }
     */
    @Override
    public void serialize(Sisterna value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject(); //{
        
        gen.writeNumberField("id", value.getId()); //"id" :
        
        if(value.getCamion() != null){
                /*
                * "camion": {
                *  "id" :
                *  "patente":
                * }
                */
            gen.writeObjectFieldStart("camion"); // "camion" : {
            gen.writeNumberField("id", value.getCamion().getId()); //"id" :
            gen.writeStringField("patente", value.getCamion().getPatente()); //"patente": 
            gen.writeEndObject();
            
        }else{
            //Aca tenemos que poner que tire una excepcion
        }
            
        gen.writeNumberField("capacidad", value.getId()); //"id" :
        gen.writeNumberField("licencia", value.getId()); // "licencia"  :
        gen.writeEndObject(); //}

    }
    
    
}
