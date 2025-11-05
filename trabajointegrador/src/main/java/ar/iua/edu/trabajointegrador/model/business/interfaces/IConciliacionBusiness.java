package ar.iua.edu.trabajointegrador.model.business.interfaces;

import ar.iua.edu.trabajointegrador.model.Conciliacion;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;

public interface IConciliacionBusiness {
	public Conciliacion add(float pesajeFinal, long ordenId) throws NotFoundException, BusinessException,StateLoadException;

}
