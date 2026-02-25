package ar.iua.edu.trabajointegrador.front;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import ar.iua.edu.trabajointegrador.model.Orden.Estado;

@Data
@AllArgsConstructor
public class OrdenMonitorDTO {
    public OrdenMonitorDTO(Integer numeroOrden2, Estado estado2, LocalDateTime fechaInicioCarga,
			LocalDateTime fechaFinCarga, LocalDateTime fechaPesajeFinal2) {
	
	}
	public OrdenMonitorDTO(Integer numeroOrden2, Estado estado2, LocalDateTime fechaInicioCarga,
			LocalDateTime fechaFinCarga, LocalDateTime fechaPesajeFinal2, float pesoInicial2) {
		// TODO Auto-generated constructor stub
	}
	private Long numeroOrden;
    private String estado;
    private LocalDateTime inicioCarga;
    private LocalDateTime finCarga;
    private LocalDateTime fechaPesajeFinal;
    private float pesoInicial;

}