package ar.iua.edu.trabajointegrador.integration.cli1.model.business.interfaces;

import java.util.List;

import org.apache.coyote.BadRequestException;

import ar.iua.edu.trabajointegrador.integration.cli1.model.OrdenCli1;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.UnProcessableException;

public interface IOrdenCli1Business {
	 public OrdenCli1 cargar(String idOrdenCli1) throws NotFoundException, BusinessException;

	 public List<OrdenCli1> list() throws BusinessException;

	 public OrdenCli1 add(OrdenCli1 orden) throws FoundException, BusinessException;

	 public OrdenCli1 cargaExterna(String json) throws FoundException, BusinessException, BadRequestException, UnProcessableException;

	 public OrdenCli1 cancelarExterna(String idOrdenCli1) throws BusinessException;
}
