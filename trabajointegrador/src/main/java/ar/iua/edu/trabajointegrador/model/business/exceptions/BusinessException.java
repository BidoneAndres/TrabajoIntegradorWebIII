package ar.iua.edu.trabajointegrador.model.business.exceptions;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BusinessException extends Exception{
	@Builder
	public BusinessException(String message, Throwable ex) {
		super(message, ex);
	}
	@Builder
	public BusinessException(String message) {
		super(message);
	}
	@Builder
	public BusinessException(Throwable ex) {
		super(ex);
	}
}
