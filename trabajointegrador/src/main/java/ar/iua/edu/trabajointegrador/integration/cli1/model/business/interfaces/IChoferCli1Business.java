package ar.iua.edu.trabajointegrador.integration.cli1.model.business.interfaces;

import ar.iua.edu.trabajointegrador.integration.cli1.model.ChoferCli1;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface IChoferCli1Business {
	
	public ChoferCli1 cargaExterna(ChoferCli1 chofer) throws FoundException, BusinessException, NotFoundException;

}
