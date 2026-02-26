package ar.iua.edu.trabajointegrador.controllers;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaHeaderBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.util.FieldValidator;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import ar.iua.edu.trabajointegrador.util.Paginas;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ar.iua.edu.trabajointegrador.model.serializers.DatoCargaJsonSerializer;

import java.util.List;
import java.util.Map;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
@Tag(name = "3. Carga", description = "API para Gestionar Carga")
@RestController
@RequestMapping(Constants.URL_CARGA)
@CrossOrigin(origins = "http://localhost:5173")
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
	
	@Autowired
	private SimpMessagingTemplate messagingTemplate;

	@Operation(
    	operationId = "listar-datos-carga",
	    summary = "Lista todos los datos de carga",
    	description = "Devuelve una lista completa de datos de carga registrados en el sistema."
	)
	@ApiResponses(value = {
    	@ApiResponse(responseCode = "200", description = "Lista obtenida correctamente."),
	    @ApiResponse(responseCode = "404", description = "No se encontraron datos de carga."),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
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

	@Operation(
    	operationId = "listar-datos-carga-por-orden",
	    summary = "Lista los datos de carga por número de orden",
    	description = "Devuelve los datos de carga asociados a un número de orden específico."
	)
	@ApiResponses(value = {
    	@ApiResponse(responseCode = "200", description = "Datos obtenidos correctamente."),
	    @ApiResponse(responseCode = "404", description = "No se encontraron datos para la orden indicada."),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})	
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
	
	@Operation(
    	operationId = "listar-cabeceras-carga",
    	summary = "Lista todas las cabeceras de carga",
    	description = "Devuelve todas las cabeceras de carga registradas en el sistema."
	)
	@ApiResponses(value = {
    	@ApiResponse(responseCode = "200", description = "Cabeceras obtenidas correctamente."),
	    @ApiResponse(responseCode = "404", description = "No se encontraron cabeceras."),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
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
	
	@Operation(
    	operationId = "obtener-header-por-orden",
    	summary = "Obtiene una cabecera de carga por ID de orden",
    	description = "Devuelve la cabecera de carga asociada a una orden específica."
	)
	@ApiResponses(value = {
    	@ApiResponse(responseCode = "200", description = "Cabecera encontrada."),
    	@ApiResponse(responseCode = "404", description = "No se encontró la cabecera."),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
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
	
	@Operation(
    	operationId = "crear-dato-carga",
	    summary = "Registra un nuevo dato de carga",
    	description = "Permite crear un nuevo registro de carga a partir de un JSON enviado en el cuerpo de la petición."
	)
	@ApiResponses(value = {
    	@ApiResponse(responseCode = "201", description = "Dato de carga creado correctamente."),
    	@ApiResponse(responseCode = "404", description = "Orden asociada no encontrada."),
    	@ApiResponse(responseCode = "409", description = "Estado inválido para registrar la carga."),
    	@ApiResponse(responseCode = "422", description = "Datos de entrada inválidos."),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	@PostMapping(value = "")
	public ResponseEntity<?> add(HttpEntity<String> httpEntity) {
		try {
			DatoCarga response = datoCargaBusiness.add(httpEntity.getBody());
			
			messagingTemplate.convertAndSend("/topic/monitor/" + response.getOrden().getId() , response);
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


	@Operation(
    	operationId = "activar-carga",
    	summary = "Activa una orden de carga",
    	description = "Activa la orden de carga correspondiente al número y clave de activación provistos."
	)
	@ApiResponses(value = {
    	@ApiResponse(responseCode = "201", description = "Carga activada correctamente."),
    	@ApiResponse(responseCode = "404", description = "Orden no encontrada."),
    	@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})
	@PostMapping(value = "/activacion")
	public ResponseEntity<?> activate(@RequestParam Integer numeroOrden, @RequestParam Integer claveActivacion) {
		try {
			Orden response = ordenBusiness.activarCarga(numeroOrden,claveActivacion);
			messagingTemplate.convertAndSend("/topic/carga" + response.getNumeroOrden(), response);
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
	
	@Operation(
		operationId = "desactivar-carga",
		summary = "Desactiva una orden de carga",
		description = "Desactiva la orden de carga correspondiente al número de orden provisto."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "202", description = "Carga desactivada correctamente."),
		@ApiResponse(responseCode = "404", description = "Orden no encontrada."),
		@ApiResponse(responseCode = "500", description = "Error interno del servidor.")
	})	
	@PostMapping(value = "/desactivacion")
	public ResponseEntity<?> desactivate(@RequestParam int numeroOrden) {
		try {
			Orden response = ordenBusiness.desactivarCarga(numeroOrden);
			messagingTemplate.convertAndSend("/topic/carga" + response.getNumeroOrden(), response);
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

	@Operation(
        summary = "Listar detalles de carga por orden",
        description = "Retorna una lista paginada y ordenada de los datos de carga (DatoCarga) asociados a una orden. " +
                      "Útil para reconstruir la curva de carga y telemetría.",
        operationId = "getDetallesPorOrden"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de detalles obtenida correctamente.",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(example = "{\"details\": [...], \"pagination\": {...}}"))
        ),
        @ApiResponse(responseCode = "400", description = "Parámetro de ordenación (sort) no válido."),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada."),
        @ApiResponse(responseCode = "500", description = "Error interno al serializar los datos.")
    })
	@GetMapping(value = "/por-orden", produces = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(
		@Parameter(description = "ID de la orden a consultar", required = true, example = "1006")	
		@RequestParam Long idOrder,
		@Parameter(description = "Página a recuperar (0..N)", example = "0")
		@RequestParam(value = "page", defaultValue = "0") int page,
		@Parameter(description = "Cantidad de registros por página", example = "10")
		@RequestParam(value = "size", defaultValue = "10") int size) {

        Pageable pageable;
        /*if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(DatoCarga.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Alarm");
            }

            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }*/
		pageable = PageRequest.of(page, size);
        Orden orden = ordenBusiness.load(idOrder);
        Page<DatoCarga> details = datoCargaBusiness.listByOrden(orden, pageable);
        StdSerializer<DatoCarga> DatoCargaSerializer = new DatoCargaJsonSerializer(DatoCarga.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(DatoCarga.class, DatoCargaSerializer, null);

        // Convertir cada detalle a JSON y agregarla al resultado
        List<Object> serializedDetails = details.getContent().stream()
                .map(detail -> {
                    try {
                        return mapper.valueToTree(detail);  // Serializa a JsonNode directamente
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Detail", e);
                    }
                }).toList();

        // Crear un objeto de información de paginación
        Paginas paginationInfo = new Paginas(
                details.getPageable(),
                details.getTotalPages(),
                details.getTotalElements(),
                details.getNumber(),
                details.getSize(),
                details.getNumberOfElements()
        );

        // Crear la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("details", serializedDetails);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



	@Operation(
        summary = "Obtener todos los detalles de carga",
        description = "Retorna la lista completa de datos de carga (DatoCarga) para una orden específica, sin paginación. " + 
                      "Recomendado para la generación de gráficos de telemetría.",
        operationId = "getAllDetallesCarga"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista completa de detalles obtenida exitosamente.",
            content = @Content(mediaType = "application/json", 
            array = @ArraySchema(schema = @Schema(implementation = DatoCarga.class)))
        ),
        @ApiResponse(responseCode = "404", description = "Orden no encontrada."),
        @ApiResponse(responseCode = "500", description = "Error interno durante la serialización de los datos.")
    })
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @SneakyThrows
    public ResponseEntity<?> getAllAlarmas(
			@Parameter(description = "ID de la orden (base de datos)", required = true, example = "1006")
			@RequestParam Long idOrden) {
        Orden orden = ordenBusiness.load(idOrden);
        List<DatoCarga> detalles = datoCargaBusiness.listByNumeroOrden(orden.getNumeroOrden());
        StdSerializer<DatoCarga> DatoCargaSerializer = new DatoCargaJsonSerializer(DatoCarga.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(DatoCarga.class, DatoCargaSerializer, null);

        // Convertir cada detalle a JSON y agregarla al resultado
        List<Object> serializedDetalles = detalles.stream()
                .map(detail -> {
                    try {
                        return mapper.valueToTree(detail);  // Serializa a JsonNode directamente
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto DatoCarga", e);
                    }
                }).toList();

        return new ResponseEntity<>(serializedDetalles, HttpStatus.OK);
    }
}
