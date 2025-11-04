package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.Optional;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.DatoCargaHeader;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;

public interface IDatoCargaHeaderBusiness {
	public DatoCargaHeader add(DatoCarga datoCarga) throws  BusinessException,StateLoadException, NotFoundException;
	
	public Optional<DatoCargaHeader> findByOrdenId(long ordenId) throws BusinessException, NotFoundException;

}
