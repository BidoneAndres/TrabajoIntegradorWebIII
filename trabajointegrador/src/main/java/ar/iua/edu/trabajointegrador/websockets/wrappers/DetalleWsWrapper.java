package ar.iua.edu.trabajointegrador.websockets.wrappers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@Getter
@Setter
@NoArgsConstructor
public class DetalleWsWrapper {
    
    private long id;
    private Date fechaCreacion;
    private float masaAcumulada;
    private float densidad;
    private float temperatura;
    private float caudal;

}
