package ar.iua.edu.trabajointegrador.events.listener;

import ar.iua.edu.trabajointegrador.events.Evento;
import ar.iua.edu.trabajointegrador.model.Alarma;
import ar.iua.edu.trabajointegrador.model.Detalle;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IAlarmaBusiness;
import ar.iua.edu.trabajointegrador.util.services.EmailBusiness;
import ar.iua.edu.trabajointegrador.websockets.wrappers.AlarmaWsWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class EventoListener implements ApplicationListener<Evento>{

    @Override
    public void onApplicationEvent(Evento evento) {
        if (evento.getTipoEvento().equals(Evento.TipoEvento.TEMPERATURA_ALTA) && evento.getSource() instanceof Detalle) {
            manejoTemperaturaAlta((Detalle) evento.getSource());
        }
    }

    @Autowired
    private EmailBusiness emailBusiness;

    @Autowired
    private IAlarmaBusiness alarmaBusiness;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${mail.temperature.exceeded.send.to}")
    private String to;
    private void manejoTemperaturaAlta(Detalle detalle) {
        Date now = new Date(System.currentTimeMillis());

        // Guardado de alerta en db
        Alarma alarma = new Alarma();
        alarma.setOrden(detalle.getOrden());
        alarma.setTiempo(now);
        alarma.setTemperatura(detalle.getTemperatura());
        alarma.setEstado(Alarma.alarmaEstado.PENDIENTE_REVISION);

        try {
            alarma = alarmaBusiness.add(alarma);
        } catch (BusinessException | FoundException e) {
            log.error(e.getMessage(), e);
        }

        // Envío de alerta a clientes (WebSocket)
        AlarmaWsWrapper alarmaWsWrapper = new AlarmaWsWrapper();
        alarmaWsWrapper.setId(alarma.getId());
        alarmaWsWrapper.setOrdenId(alarma.getOrden().getId());
        alarmaWsWrapper.setEstado(alarma.getEstado());
        alarmaWsWrapper.setTemperatura(alarma.getTemperatura());
        alarmaWsWrapper.setFechaCreacion(alarma.getTiempo());
        //alarmWsWrapper.setThresholdTemperatura(alarm.getOrden().getProduct().getThresholdTemperatura()); //todo tira null pointer ver que onda
        alarmaWsWrapper.setDescripcion(alarma.getDescripcion() != null ? alarma.getDescripcion() : null);
        alarmaWsWrapper.setUser(
                alarma.getUser() != null && alarma.getUser().getUsername() != null
                        ? alarma.getUser().getUsername()
                        : null
        );

        String topic = "/topic/alarmas/Orden/" + detalle.getOrden().getId();
        try {
            messagingTemplate.convertAndSend(topic, alarmaWsWrapper);
        } catch (Exception e) {
            log.error("Failed to send alert notification", e);
        }

        // Armado de mail de alerta
        String subject = "Temperatura Excedida Orden Nro " + detalle.getOrden().getId();
        String mensaje = String.format(
                """
                        ALERTA: Temperatura Excedida en la Orden Nro %s

                        Detalles de la Alerta:
                        ---------------------------------
                        Orden ID: %s
                        Fecha/Hora del Evento: %s
                        Temperatura Registrada: %.2f °C
                        Masa Acumulada: %.2f kg
                        Densidad: %.2f kg/m³
                        Caudal: %.2f Kg/h
                        ---------------------------------

                        Descripción: La temperatura del combustible ha superado el umbral establecido. \
                        Por favor, revise esta alerta lo antes posible para evitar inconvenientes.

                        Atentamente,
                        Sistema de Monitoreo de Carga de Combustible""",
                detalle.getOrden().getId(),
                detalle.getOrden().getId(),
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now),
                detalle.getTemperatura(),
                detalle.getMasaAcumulada(),
                detalle.getDensidad(),
                detalle.getCaudal()
        );

        try {
            emailBusiness.sendSimpleMessage(to, subject, mensaje);
            log.info("Enviando mensaje '{}'", mensaje);
        } catch (BusinessException e) {
            log.error(e.getMessage(), e);
        }
    }

}
