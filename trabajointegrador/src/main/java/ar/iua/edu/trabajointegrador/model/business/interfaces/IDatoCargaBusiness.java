package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;
import java.util.Optional;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;

public interface IDatoCargaBusiness {
	
	public DatoCarga add(String json) throws  BusinessException, InvalidLoadException, StateLoadException, NotFoundException;
	//public List<DatoCarga> listByOrden(Long ordenId) throws BusinessException;

	public List<DatoCarga> list() throws BusinessException;
	
	//utils
	public Optional<Double> loadLastMasaAcumulada(Integer claveActivacion) throws BusinessException;

	public Optional<Double> calculateDensidadProducto(Integer claveActivacion) throws BusinessException;

	
}
