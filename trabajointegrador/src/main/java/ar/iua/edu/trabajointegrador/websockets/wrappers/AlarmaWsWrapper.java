package ar.iua.edu.trabajointegrador.websockets.wrappers;

import ar.iua.edu.trabajointegrador.model.Alarma;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class AlarmaWsWrapper {

    private long id;
    private long ordenId;
    private Alarma.alarmaEstado estado;
    private float temperatura;
    private String descripcion;
    private Date fechaCreacion;
    private String user;
    private float umbralTemperatura;
    
}
