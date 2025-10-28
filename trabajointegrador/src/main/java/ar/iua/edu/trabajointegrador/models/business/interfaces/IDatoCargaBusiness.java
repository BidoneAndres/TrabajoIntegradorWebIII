package ar.iua.edu.trabajointegrador.models.business.interfaces;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.InvalidLoadException;

public interface IDatoCargaBusiness {
	
	public DatoCarga add(DatoCarga datoCarga) throws  BusinessException, InvalidLoadException;
	
}
