package ar.iua.edu.trabajointegrador.util;

import java.util.List;
import java.util.concurrent.TimeUnit;

import ar.iua.edu.trabajointegrador.model.Alarma;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IAlarmaBusiness;
import ar.iua.edu.trabajointegrador.websockets.wrappers.AlarmaWsWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableScheduling
@EnableAsync
@Slf4j
public class Scheduler {

    @Autowired
    private IAlarmaBusiness alarmaBusiness;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Scheduled(fixedDelay = 60, initialDelay = 60, timeUnit = TimeUnit.SECONDS)
    public void redordatorioAlarma() {

        try {
            List<Alarma> alarmas = alarmaBusiness.revisionPendiente();
            for (Alarma alarma : alarmas) {

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

                try {
                    log.info("Sending reminder for alarma id=" + alarma.getId());
                    messagingTemplate.convertAndSend("/topic/alarmas/reminders", alarmaWsWrapper);
                } catch (Exception e) {
                    log.error("Failed to send alert notification for alarma id=" + alarma.getId(), e);
                }
            }
        } catch (Exception e) {
            log.error("Error processing alarmas", e);
        }

    }

}
