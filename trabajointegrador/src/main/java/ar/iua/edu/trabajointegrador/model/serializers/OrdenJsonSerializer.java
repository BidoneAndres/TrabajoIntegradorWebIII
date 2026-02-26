package ar.iua.edu.trabajointegrador.model.serializers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.Orden;




public class OrdenJsonSerializer extends StdSerializer<Orden>{
    
    public OrdenJsonSerializer(Class<Orden> t, boolean dummy) {
        super (t);
    }

    @Override
    public void serialize(Orden value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        gen.writeStartObject(); //{
        
        gen.writeNumberField("id", value.getId()); //"id" :

        gen.writeNumberField("peso inicial",value.getPesoFinal()); //"peso inicial" :

        gen.writeNumberField("peso final",value.getPesoFinal()); //"peso final" :

        if(value.getCliente() != null){
                /*
                * "cliente": {
                *  "id" :
                *  "razon social":
                *  "email" :
                * }
                */

            gen.writeObjectFieldStart("cliente"); // "camion" : {
            gen.writeNumberField("id", value.getCliente().getId()); //"id" :
            gen.writeStringField("razon social", value.getCliente().getRazonSocial()); //"razon social": 
            gen.writeStringField("email", value.getCliente().getEmail()); //"email": 
            gen.writeEndObject();   
        }else{
            //Aca tenemos que poner que tire una excepcion
        }

        if(value.getProducto() != null){
                /*
                * "producto": {
                *  "id" :
                *  "nombre":
                * }
                */
            gen.writeObjectFieldStart("producto"); // "camion" : {
            gen.writeNumberField("id", value.getProducto().getId()); //"id" :
            gen.writeStringField("nombre", value.getProducto().getProducto()); //"patente": 
            gen.writeEndObject();
            
        }else{
            //Aca tenemos que poner que tire una excepcion
        }

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

        if(value.getClaveActivacion() != null){
            gen.writeNumberField("clave", value.getClaveActivacion()); // "clave: "
        }else{
            gen.writeNullField("clave");
        }

        gen.writeEndObject(); //}

    }

}
