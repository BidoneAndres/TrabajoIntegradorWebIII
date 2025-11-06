package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaHeaderBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_CARGA)
public class CargaRestController {

	@Autowired
	private IDatoCargaBusiness datoCargaBusiness;
	

	@Autowired
	private IDatoCargaHeaderBusiness datoCargaHeaderBusiness;

	@Autowired
	private IOrdenBusiness ordenBusiness;

	// response http
	@Autowired
	private IStandartResponseBusiness response;

	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> list() {
		try {
			return new ResponseEntity<>(datoCargaBusiness.list(), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} /*
			 * catch (NotFoundException e) { return new
			 * ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
			 * HttpStatus.FOUND); }
			 */
	}
	
	@GetMapping(value = "/{numeroOrden}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listNumeroOrden(@PathVariable(value="numeroOrden") int numeroOrden) {
		try {
			return new ResponseEntity<>(datoCargaBusiness.listByNumeroOrden(numeroOrden), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} /*
			 * catch (NotFoundException e) { return new
			 * ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
			 * HttpStatus.FOUND); }
			 */
	}
	
	@GetMapping(value = "/carga-header", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> listHeaders() {
		try {
			return new ResponseEntity<>(datoCargaHeaderBusiness.listHeaders(), HttpStatus.OK);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} /*
			 * catch (NotFoundException e) { return new
			 * ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
			 * HttpStatus.FOUND); }
			 */
	}
	
	 @GetMapping(value = "/carga-header/orden/{ordenId}", produces = MediaType.APPLICATION_JSON_VALUE)
	    public ResponseEntity<?> loadHeader(@PathVariable(value="ordenId") long ordenId) {
	    	try {
	            return new ResponseEntity<>(datoCargaHeaderBusiness.findByOrdenId(ordenId), HttpStatus.OK);
	        } catch (NotFoundException e) {
	            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
	        } catch (BusinessException e) {
	            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	        } catch (Exception e) {
	            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
	 

	@PostMapping(value = "")
	public ResponseEntity<?> add(HttpEntity<String> httpEntity) {
		try {
			DatoCarga response = datoCargaBusiness.add(httpEntity.getBody());

			// resposnse
			HttpHeaders responseHeaders = new HttpHeaders();
			return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);

		}

		catch (InvalidLoadException e) {
			// datos incorrectos de entrada
			return new ResponseEntity<>(response.build(HttpStatus.UNPROCESSABLE_ENTITY, e, e.getMessage()),
					HttpStatus.UNPROCESSABLE_ENTITY);

		} catch (StateLoadException e) {
			// conflict es cuando distingen el estado del receptor
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);

		}

	}

	@PostMapping(value = "/activacion")
	public ResponseEntity<?> activate(@RequestParam Integer numeroOrden, @RequestParam Integer claveActivacion) {
		try {
			Orden response = ordenBusiness.activarCarga(numeroOrden,claveActivacion);

			// resposnse
			HttpHeaders responseHeaders = new HttpHeaders();

			return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);

		}

		catch (NotFoundException e) {
			// conflict es cuando distingen el estado del receptor
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
	
	@PostMapping(value = "/desactivacion")
	public ResponseEntity<?> desactivate(@RequestParam int numeroOrden) {
		try {
			Orden response = ordenBusiness.desactivarCarga(numeroOrden);

			// resposnse
			HttpHeaders responseHeaders = new HttpHeaders();

			return new ResponseEntity<>(response, responseHeaders, HttpStatus.ACCEPTED);

		}

		catch (NotFoundException e) {
			// conflict es cuando distingen el estado del receptor
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


	/*
	 * @GetMapping(value = "/orden/{ordenId}") public ResponseEntity<?>
	 * listByOrden(@PathVariable Long ordenId) { try { return new
	 * ResponseEntity<>(datoCargaBusiness.listByOrden(ordenId), HttpStatus.OK); }
	 * catch (BusinessException e) { return new
	 * ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()),
	 * HttpStatus.NOT_FOUND); } /*catch (NotFoundException e) { return new
	 * ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()),
	 * HttpStatus.FOUND); } }
	 */

}
