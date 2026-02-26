package ar.iua.edu.trabajointegrador.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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


import ar.iua.edu.trabajointegrador.front.OrdenMonitorDTO;
import org.springframework.web.bind.annotation.RequestBody;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Alarma;
import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.model.serializers.OrdenJsonSerializer;
import ar.iua.edu.trabajointegrador.model.business.implementations.AlarmaBusiness;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.ConflictException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.UnProcessableException;
import ar.iua.edu.trabajointegrador.model.business.implementations.OrdenBusiness;
import ar.iua.edu.trabajointegrador.util.IStandartResponseBusiness;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import ar.iua.edu.trabajointegrador.util.Paginas;
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
@Tag(name = "Orden", description = "API para Gestionar Ordenes")
@Slf4j
@RestController
@RequestMapping(Constants.URL_ORDENES)
public class OrdenRestController extends BaseRestController{
    
    @Autowired
    private OrdenBusiness ordenBusiness;

    @Autowired 
    private AlarmaBusiness alarmaBusiness;

    @Autowired
    private IStandartResponseBusiness response;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
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
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            description = "Objeto JSON que representa los datos de la orden de carga",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(
                            example = """
            {
                "numero": int,
                "codExt" : String,
                "fecha_estimada": Date,
                "preset" : 0,
                "cliente": {
                    "razon_social": String,
                    "email": String
                },
                "producto": {
                    "producto_nombre": String
                },
                "camion": {
                    "patente": String,
                    "descripcion": String,
                    "sisternas": [
                        {
                            "capacidad": int,
                            "licencia": String
                        }
                    ]},
                    "chofer": {
                        "nombre": String
                        "apellido": String
                        "documento": String
                        }
                }
                    """
                    )
            )
    )
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addExternal(HttpEntity<String> httpEntity) {
        try {
            Orden ordenCreada = ordenBusiness.cargaExterna(httpEntity.getBody());

            

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", Constants.URL_ORDENES + ordenCreada.getCodExt());
            
 
            messagingTemplate.convertAndSend("/topic/orden", ordenCreada.getNumeroOrden());
            return new ResponseEntity<>(ordenCreada,responseHeaders, HttpStatus.CREATED);
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
            OrdenMonitorDTO dto = new OrdenMonitorDTO(
            	    orden.getNumeroOrden(),
            	    orden.getEstado(),
            	    orden.getFechaInicioCarga(),
            	    orden.getFechaFinCarga(),
            	    orden.getFechaPesajeFinal(),
            	    pesoInicial
            	    
            	);
            // Si tiene éxito:
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("idOrden", String.valueOf(orden.getId()));
            messagingTemplate.convertAndSend("/topic/monitor/" + orden.getId(), dto);
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

    @Operation(
        summary = "Cambiar el estado de una alarma",
        description = "Permite a un administrador modificar el estado de una alarma específica. " +
                      "El sistema registra qué usuario realizó el cambio y retorna la ubicación de la orden afectada.",
        operationId = "setEstadoAlarma"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Estado actualizado correctamente. Se incluye la URL de la orden en el header 'Location'.",
            content = @Content
        ),
        @ApiResponse(responseCode = "401", description = "No autorizado - El usuario no está autenticado."),
        @ApiResponse(responseCode = "403", description = "Prohibido - Se requiere el rol ROLE_ADMIN."),
        @ApiResponse(responseCode = "404", description = "No encontrado - La alarma o el recurso solicitado no existe."),
        @ApiResponse(responseCode = "409", description = "Conflicto - El cambio de estado no es válido para el estado actual."),
        @ApiResponse(responseCode = "500", description = "Error interno - Error en la lógica de negocio o del servidor.")
    })
    @PostMapping(value = "/set-estado-alarma")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setEstadoAlarma(
        @RequestBody 
        @Parameter(description = "Objeto Alarma completo o con ID necesario para identificarla") 
        Alarma alarma, 
        @RequestParam 
        @Parameter(description = "Nuevo estado de la alarma (Enum)", example = "ACEPTADA")
        Alarma.alarmaEstado estado) {
        try {
            User user = getUserLogged();
            if (user == null) {
                return new ResponseEntity<>(response.build(HttpStatus.UNAUTHORIZED, null, "Usuario no autenticado"), HttpStatus.UNAUTHORIZED);
            }
            
            Orden orden = alarmaBusiness.setEstadoAlarma(alarma, user, estado);
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set("Location", Constants.URL_ORDENES + "/orden/set-estado-alarma/" + orden.getId());
            return new ResponseEntity<>(responseHeaders, HttpStatus.CREATED);
        } catch (ConflictException e) {
            return new ResponseEntity<>(response.build(HttpStatus.CONFLICT, e, e.getMessage()), HttpStatus.CONFLICT);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(response.build(HttpStatus.NOT_FOUND, e, e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (BusinessException e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            return new ResponseEntity<>(response.build(HttpStatus.INTERNAL_SERVER_ERROR, e, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(
        summary = "Listar órdenes paginadas",
        description = "Obtiene una lista de órdenes con soporte para paginación y filtrado por estado. " +
                      "Requiere roles ROLE_ADMIN o ROLE_OPERATOR.",
        operationId = "getAllOrders"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Listado de órdenes y metadatos de paginación obtenidos.",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(example = "{\"orders\": [...], \"pagination\": {...}}"))
        ),
        @ApiResponse(responseCode = "401", description = "No autenticado."),
        @ApiResponse(responseCode = "403", description = "No tiene permisos suficientes."),
        @ApiResponse(responseCode = "500", description = "Error interno al procesar la solicitud.")
    })
    @GetMapping(value = "/pages", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAll(
            @Parameter(description = "Número de página (inicia en 0)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @Parameter(description = "Cantidad de registros por página", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size,
            @Parameter(description = "Filtro de estados separados por coma (ej: 'PENDIENTE,EN_PROCESO')", example = "PENDIENTE")
            @RequestParam(value = "filter", required = false) String filter) {

        Pageable pageable;
        /*if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(Orden.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Order");
            }
            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }*/
        pageable = PageRequest.of(page, size);
        final List<String> statusFilters = (filter != null && !filter.isEmpty())
                ? List.of(filter.split(","))
                : null;

        Page<Orden> orders = ordenBusiness.listPage(pageable, statusFilters);

        StdSerializer<Orden> orderSerializer = new OrdenJsonSerializer(Orden.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, orderSerializer, null);

        List<Object> serializedOrders = orders.getContent().stream()
                .map(orden -> {
                    try {
                        return mapper.valueToTree(orden);
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Orden", e);
                    }
                }).toList();

        Paginas paginationInfo = new Paginas(
                orders.getPageable(),
                orders.getTotalPages(),
                orders.getTotalElements(),
                orders.getNumber(),
                orders.getSize(),
                orders.getNumberOfElements()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("orders", serializedOrders);
        response.put("pagination", paginationInfo);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
