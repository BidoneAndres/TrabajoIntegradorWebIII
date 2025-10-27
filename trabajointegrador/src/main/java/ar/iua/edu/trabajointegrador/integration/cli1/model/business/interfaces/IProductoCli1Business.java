package ar.iua.edu.trabajointegrador.integration.cli1.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.integration.cli1.model.ProductoCli1;
import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IProductoCli1Business {

	 public ProductoCli1 load(String idCli1) throws NotFoundException, BusinessException;

	 public List<ProductoCli1> list() throws BusinessException;

	 public Producto cargaExterna(ProductoCli1 producto) throws BusinessException, NotFoundException, FoundException;
}
