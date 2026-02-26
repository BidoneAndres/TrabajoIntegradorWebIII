package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;

import ar.iua.edu.trabajointegrador.websocket.chart.TemperaturaRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
@Tag(name = "Temperaturas", description = "API para Gestionar Temperaturas")
@RestController
@RequestMapping("/temperaturas")
public class TemperaturaRestController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;


    @Operation(
        summary = "Recibir y difundir temperatura",
        description = "Recibe un valor de temperatura por HTTP y lo publica instantáneamente en el broker de mensajería (WebSocket) en el canal '/topic/temperaturas'."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Temperatura procesada y enviada al tópico de WebSockets",
            content = @Content(mediaType = "text/plain", schema = @Schema(example = "Temperatura enviada"))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Datos de temperatura inválidos o mal formados"
        )
    })
    @PostMapping
    public ResponseEntity<?> nuevaTemperatura(@RequestBody TemperaturaRequest req) {
     
        simpMessagingTemplate.convertAndSend("/topic/temperaturas", req.getValor());

        return ResponseEntity.ok("Temperatura enviada");
    }
}

