package ar.iua.edu.trabajointegrador.util;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StandartResponse {
	private String message;

	@JsonIgnore
	private Throwable ex;

	@JsonIgnore
	private HttpStatus httpStatus;

	public String getReasonPhrase() {
		return httpStatus.toString();
	}
	
	public int getCode() {
		return httpStatus.value();
	}
	

	public String getMessage() {
		if(message!=null) {
			return message;
		}
		if (ex!=null) {
			return ex.getMessage();
		}
		return null;
	}
}
