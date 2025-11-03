package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.model.Chofer;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;

public interface IChoferBusiness {
	public List<Chofer> list() throws BusinessException;
	
	public Chofer load(long id) throws NotFoundException, BusinessException;
	
	public Chofer load(String documento) throws NotFoundException, BusinessException;
	
	public Chofer addChofer(Chofer chofer) throws FoundException, BusinessException, NotFoundException;
}
