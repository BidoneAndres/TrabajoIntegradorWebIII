package ar.iua.edu.trabajointegrador.models.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;


//Esta exception es lo que se arroja cuando se hace una carga invalida, 

//Caudal <= 0
//Masa acumulada  <= 0 o menor que el valor anterior

@NoArgsConstructor
public class InvalidLoadException extends Exception {
	@Builder
	public InvalidLoadException(String message, Throwable ex) {
		super(message,ex);
	}
	
	@Builder
	public InvalidLoadException(String message) {
		super(message);
	}
	
	@Builder
	public InvalidLoadException(Throwable ex) {
		super(ex);
	}

}
