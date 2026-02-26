package ar.iua.edu.trabajointegrador.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.util.StandartResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


import ar.iua.edu.trabajointegrador.auth.UserBusiness;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "User", description = "API para Gestion de usuarios")
@Slf4j
@RestController
@RequestMapping(Constants.URL_USER)
public class UserRestController {

    @Autowired UserBusiness userBusiness;

    @Operation(
            operationId = "listar-usuarios",
            summary = "Lista usuarios",
            description = "Lista los usuarios.")
    @ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))),
    @ApiResponse(responseCode = "500", description = "Error interno del servidor.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = StandartResponse.class)))
    })
    @SneakyThrows
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<User>> listar() {
        try {
            return new ResponseEntity<>(userBusiness.list(), HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error al listar usuarios: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
