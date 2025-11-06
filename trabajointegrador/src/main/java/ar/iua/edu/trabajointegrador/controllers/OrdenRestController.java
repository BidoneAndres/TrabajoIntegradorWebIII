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
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    
    @Operation(
            operationId = "listar-ordenes",
            summary = "Lista ordenes de carga",
            description = "Lista las ordenes de carga.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Orden.class))),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list() {
        return new ResponseEntity<>(ordenBusiness.list(), HttpStatus.OK);
    }

    @Operation(
        operationId = "obtener-orden-por-codigo-externo",
        summary = "Obtiene una orden de carga por código externo",
        description = "Busca y devuelve una orden de carga según su código externo (codExt)."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    })
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
    
    @Operation(
        operationId = "obtener-orden-por-id",
        summary = "Obtiene una orden de carga por ID",
        description = "Busca y devuelve una orden de carga específica según su ID numérico."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orden encontrada correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Orden.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    }) 
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

    @Operation(
        operationId = "crear-orden-externa",
        summary = "Crea una nueva orden de carga externa",
        description = "Recibe una orden de carga externa en formato JSON y la registra en el sistema."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Orden creada exitosamente.",
            content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "302", description = "La orden ya existe.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud mal formada.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "422", description = "Entidad no procesable (datos inválidos).",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    })
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
    @Operation(
        operationId = "registrar-pesaje-inicial",
        summary = "Registra el pesaje inicial de una orden",
        description = "Registra el peso inicial de un camión asociado a una orden de carga, usando la patente y el peso inicial."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Pesaje inicial registrado correctamente.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada para la patente indicada.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "422", description = "La orden no está en el estado correcto para registrar pesaje.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    })
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
