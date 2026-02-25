package ar.iua.edu.trabajointegrador.config;



import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import ar.iua.edu.trabajointegrador.controllers.Constants;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {


        config.enableSimpleBroker("/topic"); // /topic/temperaturas

        config.setApplicationDestinationPrefixes("/app"); // /app/temperaturas

    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/temperaturas")
            .setAllowedOriginPatterns("*")
            .withSockJS();
      //Se agrega un endpoint denominado /chat y uno /graph, además de agrega la característica SockJS,
        //lo que permite disponer de vías alternativas en el caso de que el navegador no soporte websoket
        //o existan restricciones de proxy por ejemplo.
        //El endpoint es el punto en común "físico" de la comunicación
//        registry.addEndpoint("/alarms").setAllowedOrigins("http://localhost:3000");
//        registry.addEndpoint("/alarms").withSockJS();

        registry.addEndpoint("/notifier").setAllowedOrigins("http://localhost:3000");
        registry.addEndpoint("/notifier").withSockJS();

    }

}