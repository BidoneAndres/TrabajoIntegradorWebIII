package ar.iua.edu.trabajointegrador.models.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.models.Orden;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IOrdenBusiness {
	public List<Orden> list() throws BusinessException;
	
	public Orden load(long id) throws NotFoundException, BusinessException;
}
