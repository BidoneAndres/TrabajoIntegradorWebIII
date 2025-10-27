package ar.iua.edu.trabajointegrador.integration.cli1.model.business.interfaces;

import ar.iua.edu.trabajointegrador.integration.cli1.model.CamionCli1;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;

public interface ICamionCli1Business {

	public CamionCli1 load(String idCli1) throws NotFoundException, BusinessException;

    public CamionCli1 cargaExterna(CamionCli1 truck) throws FoundException, BusinessException, NotFoundException;
}
