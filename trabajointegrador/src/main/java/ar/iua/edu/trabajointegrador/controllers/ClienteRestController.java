package ar.iua.edu.trabajointegrador.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ar.iua.edu.trabajointegrador.model.Cliente;
import ar.iua.edu.trabajointegrador.model.business.implementations.ClienteBusiness;
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Clientes", description = "API para Gestion de Clientes")
@Slf4j
@RestController
@RequestMapping(Constants.URL_CLIENTES)
public class ClienteRestController {

    @Autowired ClienteBusiness clienteBusiness;

    @Operation(
            operationId = "listar-clientees",
            summary = "Lista clientees",
            description = "Lista los clientees.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cliente.class))),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Cliente>> listar() {
        try {
            return new ResponseEntity<>(clienteBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al listar usuarios: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
