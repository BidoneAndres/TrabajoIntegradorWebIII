package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
<<<<<<< HEAD
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
=======
>>>>>>> 0b9625395f4782de8dd9587c852b054b81219d9f
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;

public interface IProductoBusiness {
	public List<Producto> list() throws BusinessException;
	
	public Producto load(long id) throws NotFoundException, BusinessException;
	
	public Producto load(String producto) throws NotFoundException, BusinessException;
<<<<<<< HEAD
	
	public Producto addProducto(Producto producto) throws NotFoundException, BusinessException, FoundException;
=======
>>>>>>> 0b9625395f4782de8dd9587c852b054b81219d9f
}
