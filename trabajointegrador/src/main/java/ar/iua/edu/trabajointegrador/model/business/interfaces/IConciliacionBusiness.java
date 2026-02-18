package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.model.Conciliacion;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;

public interface IConciliacionBusiness {
	public Conciliacion add(float pesajeFinal, int numeroOrden) throws NotFoundException, BusinessException,StateLoadException;
	public List<Conciliacion> list() throws BusinessException;
	public Conciliacion loadByNumeroOrden(int numeroOrden)throws BusinessException, NotFoundException;
}
