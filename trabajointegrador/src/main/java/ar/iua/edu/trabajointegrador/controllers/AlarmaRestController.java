package ar.iua.edu.trabajointegrador.controllers;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IAlarmaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.serializers.AlarmaJsonSerializer;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import ar.iua.edu.trabajointegrador.util.Paginas;
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import ar.iua.edu.trabajointegrador.model.Alarma;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.Parameter;


@Tag(description = "API Interna para Gestionar Alarmas", name = "Alarmas")
@RestController
@RequestMapping(Constants.URL_ALARMA)
public class AlarmaRestController extends BaseRestController{

    @Autowired
    private IAlarmaBusiness alarmaBusiness;

    @Autowired
    private IOrdenBusiness ordenBusiness;

@Operation(
        summary = "Obtener alarmas por Orden",
        description = "Retorna una lista paginada de alarmas asociadas a una orden específica. " +
                      "Requiere roles ROLE_ADMIN o ROLE_OPERATOR.",
        operationId = "getAllAlarms"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de alarmas obtenida exitosamente",
            content = @Content(mediaType = "application/json", 
            schema = @Schema(example = "{\"alarmas\": [], \"pagination\": {}}"))
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "No autorizado - Token faltante o inválido"
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Prohibido - No tiene los roles necesarios (ADMIN/OPERATOR)"
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "Orden no encontrada"
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = StandartResponse.class))
        )
    })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(
            @Parameter(description = "ID de la orden para filtrar alarmas", required = true, example = "1006")
            @RequestParam("idOrden") Long idOrden,
            
            @Parameter(description = "Número de página (0..N)", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            
            @Parameter(description = "Cantidad de elementos por página", example = "10")
            @RequestParam(value = "size", defaultValue = "10") int size){

        Pageable pageable;
        /*if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(Alarma.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Alarma");
            }

            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }*/
        pageable = PageRequest.of(page, size);
        Orden orden = ordenBusiness.load(idOrden);
        Page<Alarma> alarmas = alarmaBusiness.getAllAlarmasByOrden(orden, pageable);
        StdSerializer<Alarma> alarmSerializer = new AlarmaJsonSerializer(Alarma.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Alarma.class, alarmSerializer, null);

        // Convertir cada alarma a JSON y agregarla al resultado
        List<Object> serializedAlarms = alarmas.getContent().stream()
                .map(alarm -> {
                    try {
                        return mapper.valueToTree(alarm);  // Serializa a JsonNode directamente
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Alarm", e);
                    }
                }).toList();

        // Crear un objeto de información de paginación
        Paginas pagination = new Paginas(
                alarmas.getPageable(),
                alarmas.getTotalPages(),
                alarmas.getTotalElements(),
                alarmas.getNumber(),
                alarmas.getSize(),
                alarmas.getNumberOfElements()
        );

        // Crear la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("alarmas", serializedAlarms);
        response.put("pagination", pagination);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
