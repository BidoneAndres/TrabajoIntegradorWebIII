package ar.iua.edu.trabajointegrador.models.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.models.Sisterna;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface ISisternaBusiness {
	public List<Sisterna> list() throws BusinessException;
	
	public Sisterna load(long id) throws NotFoundException, BusinessException;
	
	public Sisterna load(String licencia) throws NotFoundException, BusinessException;
}
