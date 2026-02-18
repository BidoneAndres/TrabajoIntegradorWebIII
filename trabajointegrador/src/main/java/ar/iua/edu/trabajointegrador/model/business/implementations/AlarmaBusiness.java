package ar.iua.edu.trabajointegrador.model.business.implementations;

import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.auth.IUserBusiness;
import ar.iua.edu.trabajointegrador.model.Alarma;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.ConflictException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IAlarmaBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.AlarmaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class AlarmaBusiness implements IAlarmaBusiness {

    @Autowired
    private AlarmaRepository alarmaDAO;

    @Autowired
    private OrdenBusiness ordenBusiness;

    @Autowired
    private IUserBusiness userBusiness;

    @Override
    public List<Alarma> list() throws BusinessException {
        try {
            return alarmaDAO.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Alarma load(Long id) throws BusinessException, NotFoundException {
        Optional<Alarma> alarmaFound;
        try {
            alarmaFound = alarmaDAO.findById(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (alarmaFound.isEmpty()) {
            throw NotFoundException.builder().message("No se encontró la alarma con id: " + id).build();
        }
        return alarmaFound.get();
    }

    @Override
    public Alarma add(Alarma alarma) throws FoundException, BusinessException {

        try {
            load(alarma.getId());
            throw FoundException.builder().message("Ya existe la Alarma id = " + alarma.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }
        try {
            load(alarma.getId());
            throw FoundException.builder().message("Ya existe la Alarma = " + alarma.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }
        try {
            return alarmaDAO.save(alarma);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Alarma update(Alarma alarma) throws NotFoundException, BusinessException {
        load(alarma.getId());
        try {
            return alarmaDAO.save(alarma);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
    }

    @Override
    public Boolean alarmaAceptada(Long ordenId) {
        return alarmaDAO.findByEstadoAndOrdenId(Alarma.alarmaEstado.PENDIENTE_REVISION, ordenId).isPresent();
    }

    @Override
    public List<Alarma> revisionPendiente() throws NotFoundException {
        Optional<List<Alarma>> alarma = alarmaDAO.findByAlarmaEstadoAndEstado(Alarma.alarmaEstado.PENDIENTE_REVISION, Orden.Estado.ESTADO_2_PESAJE_INICIAL_REGISTRADO);
        if (alarma.isEmpty()) {
            throw new NotFoundException("No se encontraron alarmas pendientes de revisión");
        }
        return alarma.get();
    }

    public Orden setEstadoAlarma(Alarma alarma, User user, Alarma.alarmaEstado estadoNuevo) throws BusinessException, NotFoundException, ConflictException {
        Alarma alarmaFound = load(alarma.getId());
        Orden ordenFound = ordenBusiness.load(alarmaFound.getOrden().getId());

        User userFound = userBusiness.load(user.getUsername());

        if (alarmaFound.getEstado() != Alarma.alarmaEstado.PENDIENTE_REVISION) {
            throw ConflictException.builder().message("La alarma ya fue manejada").build();
        }
        if (ordenFound.getEstado() != Orden.Estado.ESTADO_2_PESAJE_INICIAL_REGISTRADO) {
            throw ConflictException.builder().message("La orden no se encuentra en estado de carga").build();
        }

        if (estadoNuevo != Alarma.alarmaEstado.ACEPTADA && estadoNuevo != Alarma.alarmaEstado.PROBLEMAS) {
            throw BusinessException.builder().message("El estado proporcionado no es válido").build();
        }

        if (!(alarma.getDescripcion() == null || alarma.getDescripcion().isEmpty())) {
            alarmaFound.setDescripcion(alarma.getDescripcion());
        }

        alarmaFound.setEstado(estadoNuevo);
        alarmaFound.setUser(userFound);
        update(alarmaFound);

        return ordenBusiness.update(ordenFound);
    }

    public Page<Alarma> getAllAlarmasByOrden(Orden orden, Pageable pageable) throws NotFoundException, BusinessException {
        Optional<Page<Alarma>> alarmas;
        try {
            alarmas = alarmaDAO.findAllByOrden(orden, pageable);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (alarmas.isEmpty()) {
            throw new NotFoundException("No alarms found for order id = " + orden.getId());
        }

        return alarmas.orElseGet(Page::empty);
    }
                
}
