package ar.iua.edu.trabajointegrador.models.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.models.Camion;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.models.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.models.persistence.CamionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CamionBusiness implements ICamionBusiness {
	
	@Autowired
	private CamionRepository camionDAO;
	

	@Override
	public List<Camion> list() throws BusinessException {
		try {
			return camionDAO.findAll();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Camion load(long id) throws NotFoundException, BusinessException {
		Optional<Camion> camionFound;
		try {
			camionFound = camionDAO.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (camionFound.isEmpty())
			throw NotFoundException.builder().message("El camion "+ id + "no se encuentra").build();
		return camionFound.get();
	}

	@Override
	public Camion load(String patente) throws NotFoundException, BusinessException {
		Optional<Camion> camionFound;
		try {
			camionFound = camionDAO.findByPatente(patente);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (camionFound.isEmpty())
			throw NotFoundException.builder().message("El camion con la patente"+ patente + "no se encuentra").build();
		return camionFound.get();
	}

}
