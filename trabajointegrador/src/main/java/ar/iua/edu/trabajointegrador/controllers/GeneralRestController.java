package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IChoferBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IConciliacionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;



//EL proposito de este controller es obtener consultas de las otras entidades: clientes, choferes productos


@RestController
@RequestMapping(Constants.URL_GENERAL)
public class GeneralRestController  extends BaseRestController {
	
	@Autowired
	private IClienteBusiness clienteBusiness;
	
	@Autowired
	private IProductoBusiness productoBusiness;
	
	@Autowired
	private IChoferBusiness choferBusiness;

	@Autowired
	private ICamionBusiness camionBusiness;
	
	// response http
	@Autowired
	private IStandartResponseBusiness response;
	
	
	@GetMapping(value = "/cliente", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadCliente() {
    	try {
            return new ResponseEntity<>(clienteBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
	}
	
	@GetMapping(value = "/producto", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadProducto() {
    	try {
            return new ResponseEntity<>(productoBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
	}
	
	@GetMapping(value = "/chofer", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadChofer() {
    	try {
            return new ResponseEntity<>(choferBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
	}

	@GetMapping(value = "/camion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadCamion() {
    	try {
            return new ResponseEntity<>(camionBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
	}


}
