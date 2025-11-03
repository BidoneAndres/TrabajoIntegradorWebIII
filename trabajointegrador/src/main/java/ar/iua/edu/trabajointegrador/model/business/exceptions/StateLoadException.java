package ar.iua.edu.trabajointegrador.model.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;

//exception por si se quiere realizar un cambio en la carga si esta abierta o cerrada

@NoArgsConstructor
public class StateLoadException extends Exception {
	@Builder
	public StateLoadException(String message, Throwable ex) {
		super(message,ex);
	}
	
	@Builder
	public  StateLoadException(String message) {
		super(message);
	}
	
	@Builder
	public StateLoadException(Throwable ex) {
		super(ex);
	}
}
