package ar.iua.edu.trabajointegrador.models.business.implementations;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.models.Chofer;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.models.business.interfaces.IChoferBusiness;
import ar.iua.edu.trabajointegrador.models.persistence.ChoferRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChoferBusiness implements IChoferBusiness {
	public ChoferRepository choferDAO;
	
	@Override
	public List<Chofer> list() throws BusinessException {
		try {
			return choferDAO.findAll();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Chofer load(long id) throws NotFoundException, BusinessException {
		Optional<Chofer> choferFound;
		try {
			choferFound = choferDAO.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (choferFound.isEmpty())
			throw NotFoundException.builder().message("El chofer "+ id + "no se encuentra").build();
		return choferFound.get();
	}

	@Override
	public Chofer load(String documento) throws NotFoundException, BusinessException {
		Optional<Chofer> choferFound;
		try {
			choferFound = choferDAO.findByDocumento(documento);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (choferFound.isEmpty())
			throw NotFoundException.builder().message("El chofer de documento "+ documento + " no se encuentra").build();
		return choferFound.get();
	}
}

