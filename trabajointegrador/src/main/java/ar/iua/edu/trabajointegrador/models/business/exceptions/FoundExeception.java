package ar.iua.edu.trabajointegrador.models.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FoundExeception extends Exception{
	@Builder
	public FoundExeception(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public FoundExeception(String message) {
		super(message);
	}
	@Builder
	public FoundExeception(Throwable ex) {
		super(ex);
	}
}
