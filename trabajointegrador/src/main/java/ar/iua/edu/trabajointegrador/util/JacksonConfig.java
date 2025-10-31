package ar.iua.edu.trabajointegrador.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;




// -------------- Esto es para que no haya problemas con la devolucion en json de tipo lazy -----------------------------



@Configuration
public class JacksonConfig {

    @Bean
    public Hibernate6Module hibernate6Module() {
        Hibernate6Module module = new Hibernate6Module();
        
        // Esta es la configuración clave que reemplaza la propiedad
        module.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);
        
        return module;
    }
}
