package ar.iua.edu.trabajointegrador.controllers;

import java.io.IOException;
import java.io.StringWriter;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.UnProcessableException;
import ar.iua.edu.trabajointegrador.model.business.implementations.OrdenBusiness;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@RequestMapping(Constants.URL_ORDENES)
public class OrdenRestController extends BaseRestController{
    
    @Autowired
    private OrdenBusiness ordenBusiness;

    @Autowired
    private IStandartResponseBusiness response;

    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(ordenBusiness.list(), HttpStatus.OK);
    }

    @GetMapping(value = "/codExt/{codExt}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadByCodExt(@PathVariable(value = "codExt") String codExt) {
        try {
            return new ResponseEntity<>(ordenBusiness.loadByCodExt(codExt), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> load(@PathVariable(value="id") long id) {
    	try {
            return new ResponseEntity<>(ordenBusiness.load(id), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        try {
            Orden ordenCreada = ordenBusiness.cargaExterna(httpEntity.getBody());
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", Constants.URL_ORDENES + ordenCreada.getCodExt());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (FoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.FOUND, e, e.getMessage()), HttpStatus.FOUND);
        } catch (BadRequestException e) {
            return new ResponseEntity<>(response.build(HttpStatus.BAD_REQUEST, e, e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (UnProcessableException e) {
            return new ResponseEntity<>(response.build(HttpStatus.UNPROCESSABLE_ENTITY, e, e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/pesajeInicial")
        public ResponseEntity<?> registrarPesajeInicial(
            @RequestParam("patente") String patente,
            @RequestParam("pesoInicial") float pesoInicial) { 
    
            try {
            // La línea que lanza las excepciones:
            Orden orden = ordenBusiness.registrarPesajeInicial(patente, pesoInicial);
            
            // Si tiene éxito:
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("idOrden", String.valueOf(orden.getId()));
            return new ResponseEntity<>(orden.getClaveActivacion(), responseHeaders, HttpStatus.OK); 

        } catch (NotFoundException e) {
            // 404 Not Found: La orden no existe para la patente
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        
            } catch (UnProcessableException e) {
            // 422 Unprocessable Entity: La orden no está en el estado correcto
            return new ResponseEntity<>(response.build(HttpStatus.UNPROCESSABLE_ENTITY, e, e.getMessage()), HttpStatus.UNPROCESSABLE_ENTITY);
        
        } catch (BusinessException e) {
            // 500 Internal Server Error: Error genérico de la capa de negocio/DAO
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        
        } catch (Exception e) {
            // 500 Internal Server Error: Cualquier otra excepción no esperada
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
