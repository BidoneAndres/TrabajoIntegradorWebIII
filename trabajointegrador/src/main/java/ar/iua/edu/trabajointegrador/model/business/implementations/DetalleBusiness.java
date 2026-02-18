package ar.iua.edu.trabajointegrador.model.business.implementations;
import ar.iua.edu.trabajointegrador.model.Detalle;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDetalleBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.DetalleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ar.iua.edu.trabajointegrador.model.Orden;

@Service
@Slf4j
public class DetalleBusiness implements IDetalleBusiness {

    @Autowired
    private DetalleRepository detalleDAO;

    @Override
    public Detalle load(long id) throws NotFoundException, BusinessException {
        Optional<List<Detalle>> detalleFound;

        try {
            detalleFound = detalleDAO.findByOrdenId(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detalleFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el Detalle id= " + id).build();
        return detalleFound.get().get(0);
    }

    @Override
    public Detalle add(Detalle detalle) throws FoundException, BusinessException {
        try {
            load(detalle.getId());
            throw FoundException.builder().message("Ya existe el detalle id = " + detalle.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return detalleDAO.save(detalle);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo Detalle").build();
        }

    }
    @Override
    public Page<Detalle> listByOrden(Orden orden, Pageable pageable) {
        Optional<Page<Detalle>> detalle = detalleDAO.findAllByOrden(orden, pageable);

        return detalle.orElseGet(Page::empty);
    }

    @Override
    public List<Detalle> listByOrden(long idOrden) throws NotFoundException, BusinessException {
        Optional<List<Detalle>> detalleFound;

        try {
            detalleFound = detalleDAO.findByOrdenId(idOrden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (detalleFound.isEmpty())
            throw NotFoundException.builder().message("Orden sin detalles = " + idOrden).build();
        return detalleFound.get();
    }

    public Float calculateAverageTemperature(Long idOrden) {
        Double avgTemp = detalleDAO.findAverageTemperaturaByOrdenId(idOrden);
        return avgTemp != null ? avgTemp.floatValue() : 0.0f;
    }

    public Float calculateAverageDensity(Long idOrden) {
        Double avgDensidad = detalleDAO.findAverageDensidadByOrdenId(idOrden);
        return avgDensidad != null ? avgDensidad.floatValue() : 0.0f;
    }

    public Float calculateAverageFlowRate(Long idOrden) {
        Double avgCaudal = detalleDAO.findAverageCaudalByOrdenId(idOrden);
        return avgCaudal != null ? avgCaudal.floatValue() : 0.0f;
    }

}
