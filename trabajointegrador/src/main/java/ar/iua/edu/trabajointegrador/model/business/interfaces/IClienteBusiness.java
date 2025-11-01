package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.model.Cliente;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;

public interface IClienteBusiness {
	public List<Cliente> list() throws BusinessException;
	
	public Cliente load(long id) throws NotFoundException, BusinessException;
	
	public Cliente load(String razonSocial) throws NotFoundException, BusinessException;
	
	public Cliente addCliente(Cliente cliente) throws NotFoundException, BusinessException, FoundException;
}
