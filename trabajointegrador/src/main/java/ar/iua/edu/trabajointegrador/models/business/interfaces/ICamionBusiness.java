package ar.iua.edu.trabajointegrador.models.business.interfaces;

import java.util.List;

import ar.iua.edu.trabajointegrador.models.Camion;
import ar.iua.edu.trabajointegrador.models.business.exceptions.*;

public interface ICamionBusiness {
	
	public List<Camion> list() throws BusinessException;
	
	public Camion load(long id) throws NotFoundException, BusinessException;
	
	public Camion load(String patente) throws NotFoundException, BusinessException;

}
