package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.Camion;
import ar.iua.edu.trabajointegrador.model.Sisterna;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.CamionRepository;
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
	
	public Camion addCamion(Camion camion) throws FoundException, BusinessException, NotFoundException{
	    //Verificar si el Chofer ya existe (Recomendación: Mover la búsqueda al inicio)
	    Optional<Camion> foundCamion = camionDAO.findByPatente(camion.getPatente());

	    //Controlar la FoundException si ya existe un Chofer con ese documento
	    if (foundCamion.isPresent()) {
	        // Si ya existe, lanzamos la excepción específica FoundException
	        throw new FoundException("Ya existe un camion registrado con la patente: " + camion.getPatente());
	    }

	    try {
            for (Sisterna sisterna: camion.getSisternas()) {
                sisterna.setCamion(camion);
            }
            camionDAO.save(camion);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        return load(camion.getPatente());
	}
}
