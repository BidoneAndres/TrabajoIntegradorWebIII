package ar.iua.edu.trabajointegrador.events;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class DetalleEvento extends ApplicationEvent {
    public enum TipoEvento{
        DETALLE_GUARDADO
    }

    public DetalleEvento(Object source, TipoEvento tipo) {
        super(source);
        this.TipoEvento = tipo;
    }

    private TipoEvento TipoEvento;
    private Object data;
}
