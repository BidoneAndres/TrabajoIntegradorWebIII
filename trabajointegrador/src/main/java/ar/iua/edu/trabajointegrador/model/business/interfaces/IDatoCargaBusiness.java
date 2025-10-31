package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;
import java.util.Optional;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;

public interface IDatoCargaBusiness {
	
	public DatoCarga add(DatoCarga datoCarga) throws  BusinessException, InvalidLoadException;
	//public List<DatoCarga> listByOrden(Long ordenId) throws BusinessException;
	public List<DatoCarga> list() throws BusinessException;
	public Optional<Double> loadLastMasaAcumulada(Long orderId) throws BusinessException;


	
}
