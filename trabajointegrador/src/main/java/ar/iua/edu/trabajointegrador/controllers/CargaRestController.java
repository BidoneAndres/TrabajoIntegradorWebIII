package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_CARGA)
public class CargaRestController {

	@Autowired
	private IDatoCargaBusiness datoCargaBusiness;

	// response http
	@Autowired
	private IStandartResponseBusiness response;

	@PostMapping(value = "")
	public ResponseEntity<?> add(@RequestBody DatoCarga datoCarga) {
		try {
			DatoCarga response = datoCargaBusiness.add(datoCarga);

			// resposnse
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

		}

		catch (InvalidLoadException e) {
			// datos incorrectos de entrada
			return new ResponseEntity<>(response.build(HttpStatus.UNPROCESSABLE_ENTITY, e, e.getMessage()),
					HttpStatus.UNPROCESSABLE_ENTITY);

		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

}
