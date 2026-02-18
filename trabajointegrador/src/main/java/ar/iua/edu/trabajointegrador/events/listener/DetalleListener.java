package ar.iua.edu.trabajointegrador.events.listener;

import org.springframework.context.ApplicationListener;

import ar.iua.edu.trabajointegrador.events.DetalleEvento;
import ar.iua.edu.trabajointegrador.model.Detalle;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDetalleBusiness;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DetalleListener implements ApplicationListener<DetalleEvento> {

     @Override
    public void onApplicationEvent(DetalleEvento event) {
        if (event.getTipoEvento().equals(DetalleEvento.TipoEvento.DETALLE_GUARDADO) && event.getSource() instanceof Detalle) {
            handleGuardarDetalle((Detalle) event.getSource());
        }
    }
    @Autowired
    IDetalleBusiness detailBusiness;

    private void handleGuardarDetalle(Detalle detalle) {
        try {
            detailBusiness.add(detalle);
        } catch (FoundException e) {
            log.error("El detalle con id={} ya existe", detalle.getId(), e);
        } catch (BusinessException e) {
            log.error("Error al guardar el detalle con id={}", detalle.getId(), e);
        }
    }
}
