package ar.iua.edu.trabajointegrador.models.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.models.Producto;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IProductoBusiness {
	public List<Producto> list() throws BusinessException;
	
	public Producto load(long id) throws NotFoundException, BusinessException;
	
	public Producto load(String producto) throws NotFoundException, BusinessException;
}
