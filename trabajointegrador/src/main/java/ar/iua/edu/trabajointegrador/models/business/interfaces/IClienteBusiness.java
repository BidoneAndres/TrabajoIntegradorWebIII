package ar.iua.edu.trabajointegrador.models.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.models.Cliente;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IClienteBusiness {
	public List<Cliente> list() throws BusinessException;
	
	public Cliente load(long id) throws NotFoundException, BusinessException;
	
	public Cliente load(String razonSocial) throws NotFoundException, BusinessException;
}
