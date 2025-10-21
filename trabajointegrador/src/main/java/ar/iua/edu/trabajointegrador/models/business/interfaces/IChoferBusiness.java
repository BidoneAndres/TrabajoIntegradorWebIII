package ar.iua.edu.trabajointegrador.models.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.models.Chofer;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IChoferBusiness {
	public List<Chofer> list() throws BusinessException;
	
	public Chofer load(long id) throws NotFoundException, BusinessException;
	
	public Chofer load(String documento) throws NotFoundException, BusinessException;
}
