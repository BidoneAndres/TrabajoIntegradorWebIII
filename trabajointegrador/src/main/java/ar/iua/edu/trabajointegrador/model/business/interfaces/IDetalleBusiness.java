package ar.iua.edu.trabajointegrador.model.business.interfaces;

import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.Detalle;
import java.util.List;
import ar.iua.edu.trabajointegrador.model.Orden;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDetalleBusiness {
    public Detalle load(long id) throws NotFoundException, BusinessException;

    public List<Detalle> listByOrden(long idOrden) throws NotFoundException, BusinessException;

    Detalle add(Detalle detalle) throws FoundException, BusinessException;

    Page<Detalle> listByOrden(Orden orden, Pageable pageable);
}
