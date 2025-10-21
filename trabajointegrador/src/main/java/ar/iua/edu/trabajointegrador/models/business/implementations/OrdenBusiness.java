package ar.iua.edu.trabajointegrador.models.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.models.Orden;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.models.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.models.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {
	
	public OrdenRepository ordenDAO;
	@Override
	public List<Orden> list() throws BusinessException {
		try {
			return ordenDAO.findAll();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Orden load(long id) throws NotFoundException, BusinessException {
		Optional<Orden> ordenFound;
		try {
			ordenFound = ordenDAO.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (ordenFound.isEmpty())
			throw NotFoundException.builder().message("El orden "+ id + "no se encuentra").build();
		return ordenFound.get();
	}

}
