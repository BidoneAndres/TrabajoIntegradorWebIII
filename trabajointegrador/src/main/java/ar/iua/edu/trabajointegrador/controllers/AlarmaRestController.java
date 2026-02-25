package ar.iua.edu.trabajointegrador.controllers;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IAlarmaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.serializers.AlarmaJsonSerializer;
import ar.iua.edu.trabajointegrador.util.FieldValidator;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import ar.iua.edu.trabajointegrador.util.Paginas;
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
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Map;


@Tag(description = "API Interna para Gestionar Alarmas", name = "Alarmas")
@RestController
@RequestMapping(Constants.URL_ALARMA)
public class AlarmaRestController extends BaseRestController{

    @Autowired
    private IAlarmaBusiness alarmaBusiness;

    @Autowired
    private IOrdenBusiness ordenBusiness;

    /* ENPOINT PARA OBTENER UNA LISTA DE ALARMAS CORRESPONDIENTES A UNA ORDEN (PAGINABLE) */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(@RequestParam("idOrden") Long idOrden,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
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
