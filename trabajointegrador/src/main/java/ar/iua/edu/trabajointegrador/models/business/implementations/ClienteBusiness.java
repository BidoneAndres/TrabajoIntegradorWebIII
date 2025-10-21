package ar.iua.edu.trabajointegrador.models.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.models.Cliente;
import ar.iua.edu.trabajointegrador.models.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.models.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.models.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.models.persistence.ClienteRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ClienteBusiness implements IClienteBusiness {
	
	public ClienteRepository clienteDAO;
	@Override
	public List<Cliente> list() throws BusinessException {
		try {
			return clienteDAO.findAll();
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Cliente load(long id) throws NotFoundException, BusinessException {
		Optional<Cliente> clienteFound;
		try {
			clienteFound = clienteDAO.findById(id);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (clienteFound.isEmpty())
			throw NotFoundException.builder().message("El cliente "+ id + "no se encuentra").build();
		return clienteFound.get();
	}

	@Override
	public Cliente load(String razonSocial) throws NotFoundException, BusinessException {
		Optional<Cliente> clienteFound;
		try {
			clienteFound = clienteDAO.findByRazonSocial(razonSocial);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (clienteFound.isEmpty())
			throw NotFoundException.builder().message("El cliente "+ razonSocial + " no se encuentra").build();
		return clienteFound.get();
	}

}
