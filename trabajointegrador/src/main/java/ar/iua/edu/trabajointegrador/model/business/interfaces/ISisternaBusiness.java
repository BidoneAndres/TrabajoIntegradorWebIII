package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.model.Sisterna;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;

public interface ISisternaBusiness {
	public List<Sisterna> list() throws BusinessException;
	
	public Sisterna load(long id) throws NotFoundException, BusinessException;
	
	public Sisterna load(String licencia) throws NotFoundException, BusinessException;
}
