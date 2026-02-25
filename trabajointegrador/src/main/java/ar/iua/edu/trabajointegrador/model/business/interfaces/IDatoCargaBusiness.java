package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;
import java.util.Optional;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IDatoCargaBusiness {
	
	public DatoCarga add(String json) throws  BusinessException, InvalidLoadException, StateLoadException, NotFoundException;
	DatoCarga add(DatoCarga datoCarga) throws FoundException, BusinessException;
	//public List<DatoCarga> listByOrden(Long ordenId) throws BusinessException;



	public List<DatoCarga> list() throws BusinessException;
	public List<DatoCarga> listByNumeroOrden(int numeroOrden) throws BusinessException;
	Page<DatoCarga> listByOrden(Orden orden, Pageable pageable);
	public DatoCarga load(Integer id) throws NotFoundException, BusinessException;
	
	//utils
	public Optional<Double> loadLastMasaAcumulada(Integer claveActivacion) throws BusinessException;

	//usado en conciliacion
	public Optional<Double> calculateDensidadProductoAvg(Integer numeroOrden) throws BusinessException;
	public Optional<Double> calculateTemperaturaAvg(Integer numeroOrden) throws BusinessException;
	public Optional<Double> calculateCaudalAvg(Integer numeroOrden) throws BusinessException;


	
}
