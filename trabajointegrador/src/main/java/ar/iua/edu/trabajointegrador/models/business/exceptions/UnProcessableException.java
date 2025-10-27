package ar.iua.edu.trabajointegrador.models.business.exceptions;

import java.io.Serial;

import lombok.Builder;

public class UnProcessableException extends Exception{
	@Serial
    private static final long serialVersionUID = 1L;

    @Builder
    public UnProcessableException(String message, Throwable ex) {
        super(message, ex);
    }
    @Builder
    public UnProcessableException(String message) {
        super(message);
    }
    @Builder
    public UnProcessableException(Throwable ex) {
        super(ex.getMessage(), ex);
    }
 
}
