package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.ProductoRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductoBusiness implements IProductoBusiness {

	@Autowired
	private ProductoRepository productoDAO;
	@Override
	public List<Producto> list() throws BusinessException {
		try {
			return productoDAO.findAll();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Producto load(long id) throws NotFoundException, BusinessException {
		Optional<Producto> productoFound;
		try {
			productoFound = productoDAO.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (productoFound.isEmpty())
			throw NotFoundException.builder().message("El producto "+ id + "no se encuentra").build();
		return productoFound.get();
	}

	@Override
	public Producto load(String producto) throws NotFoundException, BusinessException {
		Optional<Producto> productoFound;
		try {
			productoFound = productoDAO.findByProducto(producto);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (productoFound.isEmpty())
			throw NotFoundException.builder().message("El producto "+ producto + " no se encuentra").build();
		return productoFound.get();
	}
	
	@Override
	public Producto addProducto(Producto producto) throws FoundException, BusinessException, FoundException{

	    //Verificar si el Chofer ya existe (Recomendación: Mover la búsqueda al inicio)
	    Optional<Producto> foundProducto = productoDAO.findByProducto(producto.getProducto());

	    //Controlar la FoundException si ya existe un Chofer con ese documento
	    if (foundProducto.isPresent()) {
	        // Si ya existe, lanzamos la excepción específica FoundException
	        throw new FoundException("Ya existe un Chofer registrado con el documento: " + producto.getProducto());
	    }

	    try {
	        //Persistir (guardar) el nuevo Chofer
	        return productoDAO.save(producto);
	    } catch (Exception e) {
	        //Manejo de errores de persistencia
	        log.error("Error al intentar guardar el Chofer con documento {}: {}", producto.getProducto(), e.getMessage(), e);

	        throw BusinessException.builder().ex(e).build();
	    }
	}

}
