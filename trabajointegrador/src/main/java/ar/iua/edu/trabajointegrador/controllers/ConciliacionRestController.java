package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.iua.edu.trabajointegrador.model.Conciliacion;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IConciliacionBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.OrdenRepository;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;

@RestController
@RequestMapping(Constants.URL_CONCILIACION)
public class ConciliacionRestController extends BaseRestController {

	@Autowired
	private IConciliacionBusiness conciliacionBusiness;
	
	@Autowired
	private OrdenRepository ordenDAO;
	
	
	// response http
	@Autowired
	private IStandartResponseBusiness response;
	
	@PostMapping(value = "")
	public ResponseEntity<?> add(@RequestParam Long ordenId, @RequestParam Float pesajeFinal ) {
		try {
			Conciliacion response = conciliacionBusiness.add(pesajeFinal, ordenId);

			// resposnse
			HttpHeaders responseHeaders = new HttpHeaders();

			return new ResponseEntity<>(response, responseHeaders, HttpStatus.CREATED);

		}
		catch (StateLoadException e) {
			// conflict es cuando distingen el estado del receptor
			return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
		}
		catch (NotFoundException e) {
			// conflict es cuando distingen el estado del receptor
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (BusinessException e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()),
					HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


}
