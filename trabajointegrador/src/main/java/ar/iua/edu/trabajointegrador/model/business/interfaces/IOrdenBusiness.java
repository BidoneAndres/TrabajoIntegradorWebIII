package ar.iua.edu.trabajointegrador.model.business.interfaces;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.UnProcessableException;

public interface IOrdenBusiness {
	public List<Orden> list() throws BusinessException;
	
	public Orden load(long id) throws NotFoundException, BusinessException;
	
	public Orden loadByCodExt(String codExt) throws NotFoundException, BusinessException;
	
	public Orden loadByClaveActivacion(Integer claveActivacion) throws NotFoundException, BusinessException;

	
	public Orden cargaExterna(String json) throws FoundException, BusinessException, BadRequestException, UnProcessableException;

	public Orden registrarPesajeInicial(String patente, float pesoInicial) throws NotFoundException, BusinessException, UnProcessableException;
	public Orden activarCarga(Integer claveActivacion) throws NotFoundException, BusinessException;
	
	public Orden desactivarCarga(Integer claveActivacion) throws NotFoundException, BusinessException;
	
	public Integer findPreset(Long ordenId) throws NotFoundException, BusinessException;
	
	public Orden.Estado findEstado(Long ordenId) throws NotFoundException, BusinessException ;
	
	public Optional<Orden> findById(long ordenId) throws NotFoundException, BusinessException;

}
