package ar.iua.edu.trabajointegrador.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.iua.edu.trabajointegrador.websocket.chart.TemperaturaRequest;

@RestController
@RequestMapping("/temperaturas")
public class TemperaturaRestController {
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping
    public ResponseEntity<?> nuevaTemperatura(@RequestBody TemperaturaRequest req) {
     
        simpMessagingTemplate.convertAndSend("/topic/temperaturas", req.getValor());

        return ResponseEntity.ok("Temperatura enviada");
    }
}

