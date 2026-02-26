package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import io.swagger.v3.oas.annotations.Parameter;

import ar.iua.edu.trabajointegrador.model.Conciliacion;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IConciliacionBusiness;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "Conciliacion", description = "API para Gestionar Conciliaciones")
@RestController
@RequestMapping(Constants.URL_CONCILIACION)
public class ConciliacionRestController extends BaseRestController {

	@Autowired
	private IConciliacionBusiness conciliacionBusiness;
	
	// response http
	@Autowired
	private IStandartResponseBusiness response;
	
	@Operation(
    	operationId = "registrar-conciliacion",
    	summary = "Registra una conciliación final",
    	description = "Registra el pesaje final asociado a una orden existente y genera la conciliación correspondiente."
	)
	@ApiResponses(value = {
    	@ApiResponse(
        	responseCode = "201",
	        description = "Conciliación registrada correctamente.",
    	    content = @Content(mediaType = "application/json",
        	        schema = @Schema(implementation = Conciliacion.class))
	    ),
    	@ApiResponse(
        	responseCode = "404",
	        description = "No se encontró la orden indicada.",
    	    content = @Content(mediaType = "application/json",
        	        schema = @Schema(implementation = StandartResponse.class))
    	),
    	@ApiResponse(
        	responseCode = "409",
	        description = "Estado inválido para registrar la conciliación (conflicto de estado).",
    	    content = @Content(mediaType = "application/json",
                schema = @Schema(implementation = StandartResponse.class))
	    ),
	    @ApiResponse(
    	    responseCode = "500",
        	description = "Error interno del servidor.",
	        content = @Content(mediaType = "application/json",
    	            schema = @Schema(implementation = StandartResponse.class))
	    )
	})
	@PostMapping(value = "")
	public ResponseEntity<?> add(@RequestParam Integer numeroOrden, @RequestParam Float pesajeFinal ) {
		try {
			Conciliacion response = conciliacionBusiness.add(pesajeFinal, numeroOrden);

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
	
	@Operation(
        summary = "Obtener conciliación de una orden",
        description = "Recupera los datos de conciliación final para una orden específica utilizando su número de orden único.",
        operationId = "getConciliacion"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Datos de conciliación recuperados exitosamente.",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Conciliacion.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró una conciliación para el número de orden proporcionado."
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno al procesar la conciliación."
        )
    })
	@GetMapping(value = "/{numeroOrden}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadConciliacion(
		@Parameter(description = "Número identificador de la orden (business key)", required = true, example = "1006")
		@PathVariable(value="numeroOrden") int numeroOrden) {
    	try {
            return new ResponseEntity<>(conciliacionBusiness.loadByNumeroOrden(numeroOrden), HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
	}


	@Operation(
        summary = "Obtener conciliación por ID de Orden",
        description = "Recupera los datos de conciliación final utilizando el ID interno (Primary Key) de la orden en la base de datos.",
        operationId = "getConciliacionById"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Datos de conciliación recuperados exitosamente.",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(implementation = Conciliacion.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "No se encontró una orden o conciliación para el ID proporcionado."
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor."
        )
    })
	@GetMapping(value = "/orden/{idOrden}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> loadConciliacionByIdOrden(
			@Parameter(description = "ID interno de la orden (PK)", required = true, example = "1")
			@PathVariable(value="idOrden") int idOrden) {
		try {
			return new ResponseEntity<>(conciliacionBusiness.loadByIdOrden(idOrden), HttpStatus.OK);
		} catch (NotFoundException e) {
			return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		} 
	}
	

	@Operation(
        summary = "Listar todas las conciliaciones",
        description = "Retorna una lista completa de todos los registros de conciliación almacenados en el sistema. " +
                      "Útil para reportes generales y auditoría de cargas finalizadas.",
        operationId = "listAllConciliaciones"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de conciliaciones obtenida exitosamente.",
            content = @Content(mediaType = "application/json", 
            array = @ArraySchema(schema = @Schema(implementation = Conciliacion.class)))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno al recuperar el listado.",
            content = @Content(schema = @Schema(implementation = StandartResponse.class))
        )
    })
	@GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loadConciliacion() {
    	try {
            return new ResponseEntity<>(conciliacionBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } 
	}


}
