package ar.iua.edu.trabajointegrador.model.business.interfaces;

import ar.iua.edu.trabajointegrador.model.Alarma;
import ar.iua.edu.trabajointegrador.model.Orden;

import java.util.List;

import ar.iua.edu.trabajointegrador.auth.User;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.ConflictException;

public interface IAlarmaBusiness {

    public List<Alarma> list() throws BusinessException;
    public Alarma load(Long id) throws BusinessException, NotFoundException;
    public Alarma add(Alarma alarma) throws BusinessException, FoundException;
    public Alarma update(Alarma alarma) throws BusinessException, NotFoundException;
    Boolean alarmaAceptada(Long idOrden) throws BusinessException;
    List<Alarma> revisionPendiente() throws NotFoundException;
    Orden setEstadoAlarma(Alarma alarma, User user, Alarma.alarmaEstado nuevoEstado) throws BusinessException, NotFoundException, ConflictException;
}
