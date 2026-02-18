package ar.iua.edu.trabajointegrador.controllers;
import ar.iua.edu.trabajointegrador.model.Detalle;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDetalleBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.serializers.DetalleJsonSerializer;
import ar.iua.edu.trabajointegrador.util.FieldValidator;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import ar.iua.edu.trabajointegrador.util.Paginas;
import lombok.SneakyThrows;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/detalles")
public class DetailRestController extends BaseRestController {

    @Autowired
    private IDetalleBusiness detalleBusiness;

    @Autowired
    private IOrdenBusiness ordenBusiness;

    /* ENPOINT PARA OBTENER UNA LISTA DE DETALLES CORRESPONDIENTES A UNA ORDEN (PAGINABLE) */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarms(@RequestParam Long idOrder,
                                          @RequestParam(value = "page", defaultValue = "0") int page,
                                          @RequestParam(value = "size", defaultValue = "10") int size,
                                          @RequestParam(value = "sort", required = false, defaultValue = "timeStamp,desc") String sort) {

        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            String sortField = sortParams[0].trim();
            String sortDirection = (sortParams.length > 1 ? sortParams[1].trim().toLowerCase() : "desc"); // Dirección predeterminada
            Sort.Direction direction = sortDirection.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

            // Validar el campo de ordenación
            if (FieldValidator.isValidField(Detalle.class, sortField)) {
                throw new IllegalArgumentException("El campo de ordenación '" + sortField + "' no es válido para la entidad Alarm");
            }

            pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        } else {
            pageable = PageRequest.of(page, size);
        }

        Orden orden = ordenBusiness.load(idOrder);
        Page<Detalle> details = detalleBusiness.listByOrden(orden, pageable);
        StdSerializer<Detalle> detalleSerializer = new DetalleJsonSerializer(Detalle.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Detalle.class, detalleSerializer, null);

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

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_OPERATOR')")
    @SneakyThrows
    public ResponseEntity<?> getAllAlarmas(@RequestParam Long idOrden) {
        Orden orden = ordenBusiness.load(idOrden);
        List<Detalle> detalles = detalleBusiness.listByOrden(orden.getId());
        StdSerializer<Detalle> detalleSerializer = new DetalleJsonSerializer(Detalle.class, false);
        ObjectMapper mapper = JsonUtils.getObjectMapper(Detalle.class, detalleSerializer, null);

        // Convertir cada detalle a JSON y agregarla al resultado
        List<Object> serializedDetalles = detalles.stream()
                .map(detail -> {
                    try {
                        return mapper.valueToTree(detail);  // Serializa a JsonNode directamente
                    } catch (Exception e) {
                        throw new RuntimeException("Error al serializar el objeto Detalle", e);
                    }
                }).toList();

        return new ResponseEntity<>(serializedDetalles, HttpStatus.OK);
    }
}
