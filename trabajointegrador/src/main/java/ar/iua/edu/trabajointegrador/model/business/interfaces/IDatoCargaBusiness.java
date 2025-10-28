package ar.iua.edu.trabajointegrador.model.business.interfaces;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;

public interface IDatoCargaBusiness {
	
	public DatoCarga add(DatoCarga datoCarga) throws  BusinessException, InvalidLoadException;
	
}
