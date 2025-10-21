package ar.iua.edu.trabajointegrador.models.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.models.Sisterna;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.models.business.interfaces.ISisternaBusiness;
import ar.iua.edu.trabajointegrador.models.persistence.SisternaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SisternaBusiness implements ISisternaBusiness {
	
	public SisternaRepository sisternaDAO;
	@Override
	public List<Sisterna> list() throws BusinessException {
		try {
			return sisternaDAO.findAll();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Sisterna load(long id) throws NotFoundException, BusinessException {
		Optional<Sisterna> sisternaFound;
		try {
			sisternaFound = sisternaDAO.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (sisternaFound.isEmpty())
			throw NotFoundException.builder().message("El sisterna "+ id + "no se encuentra").build();
		return sisternaFound.get();
	}

	@Override
	public Sisterna load(String licencia) throws NotFoundException, BusinessException {
		Optional<Sisterna> sisternaFound;
		try {
			sisternaFound = sisternaDAO.findByLicencia(licencia);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (sisternaFound.isEmpty())
			throw NotFoundException.builder().message("El sisterna "+ licencia + " no se encuentra").build();
		return sisternaFound.get();
	}

}
