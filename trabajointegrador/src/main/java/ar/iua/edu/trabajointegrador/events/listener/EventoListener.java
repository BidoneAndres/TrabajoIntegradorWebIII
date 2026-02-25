package ar.iua.edu.trabajointegrador.events.listener;

import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.auth.IUserBusiness;
import ar.iua.edu.trabajointegrador.events.Evento;
import ar.iua.edu.trabajointegrador.model.Alarma;
import ar.iua.edu.trabajointegrador.model.DatoCarga;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Component
public class EventoListener implements ApplicationListener<Evento>{

    @Override
    public void onApplicationEvent(Evento evento) {
        if (evento.getTipoEvento().equals(Evento.TipoEvento.TEMPERATURA_ALTA) && evento.getSource() instanceof DatoCarga) {
            manejoTemperaturaAlta((DatoCarga) evento.getSource());
        }
    }

    @Autowired
    private EmailBusiness emailBusiness;

    @Autowired
    private IAlarmaBusiness alarmaBusiness;

    @Autowired
    private IUserBusiness userBusiness;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Value("${mail.temperature.exceeded.send.to}")
    private String to;
    private void manejoTemperaturaAlta(DatoCarga detalle) {
        Date now = new Date(System.currentTimeMillis());

        // Guardado de alerta en db
        Alarma alarma = new Alarma();
        alarma.setOrden(detalle.getOrden());
        alarma.setTiempo(now);
        alarma.setTemperatura(detalle.getTemperatura());
        alarma.setEstado(Alarma.alarmaEstado.PENDIENTE_REVISION);
        
        // Obtener usuario logueado
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            User usuarioLogueado = userBusiness.load(username);
            alarma.setUser(usuarioLogueado);
        } catch (Exception e) {
            log.warn("No se pudo obtener el usuario logueado: " + e.getMessage());
        }
        try {
            alarma = alarmaBusiness.add(alarma);
        } catch (BusinessException | FoundException e) {
            log.error("Error al guardar alarma", e);
            return;  // ← Detiene aquí si falló
        }

        // Envío de alerta a clientes (WebSocket)
        AlarmaWsWrapper alarmaWsWrapper = new AlarmaWsWrapper();
        alarmaWsWrapper.setId(alarma.getId());
        alarmaWsWrapper.setOrdenId(alarma.getOrden().getId());
        alarmaWsWrapper.setEstado(alarma.getEstado());
        alarmaWsWrapper.setTemperatura(alarma.getTemperatura());
        alarmaWsWrapper.setFechaCreacion(alarma.getTiempo());
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
                        Temperatura Registrada: %d °C
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
                detalle.getDensidadProducto(),
                detalle.getCaudal()
        );

        log.info("Preparando envío de email a: {} con asunto: {}", to, subject);
        try {
            emailBusiness.sendSimpleMessage(to, subject, mensaje);
            log.info("Email enviado exitosamente a: {}", to);
        } catch (BusinessException e) {
            log.error("Error BusinessException al enviar email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado al enviar email: " + e.getMessage(), e);
        }
    }

}
