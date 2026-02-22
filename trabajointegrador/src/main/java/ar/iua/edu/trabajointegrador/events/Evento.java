package ar.iua.edu.trabajointegrador.events;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Evento extends ApplicationEvent {
    public enum TipoEvento {
        TEMPERATURA_ALTA
    }

    public Evento(Object source, TipoEvento tipo) {
        super(source);
        this.TipoEvento = tipo;
    }

    private TipoEvento TipoEvento;
    private Object data;
}
