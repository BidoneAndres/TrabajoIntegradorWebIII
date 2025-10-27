package ar.iua.edu.trabajointegrador.integration.cli1.model.business.interfaces;

import ar.iua.edu.trabajointegrador.integration.cli1.model.ClienteCli1;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IClienteCli1Business {
	
	public ClienteCli1 cargaExterna(ClienteCli1 cliente) throws BusinessException, NotFoundException, FoundException;

}
