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
	public ProductoRepository productoDAO;
	
	
	
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
	public Producto add(Producto producto) throws FoundException, BusinessException {

		try {
			load(producto.getId());
			throw FoundException.builder().message("Se encuentró el Producto id=" + producto.getId()).build();
		} catch (NotFoundException e) {
		}
		try {
			load(producto.getProducto());
			throw FoundException.builder().message("Se encuentró el Producto '" + producto.getProducto() +"'").build();
		} catch (NotFoundException e) {
		}

		try {
			return productoDAO.save(producto);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

}
