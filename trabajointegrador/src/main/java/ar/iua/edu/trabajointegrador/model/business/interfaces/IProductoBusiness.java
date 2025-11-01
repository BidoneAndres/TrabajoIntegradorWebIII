package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;

public interface IProductoBusiness {
	public List<Producto> list() throws BusinessException;
	
	public Producto load(long id) throws NotFoundException, BusinessException;
	
	public Producto load(String producto) throws NotFoundException, BusinessException;
	
	public Producto addProducto(Producto producto) throws NotFoundException, BusinessException, FoundException;
}
