package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.Chofer;
import ar.iua.edu.trabajointegrador.model.Cliente;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.ClienteRepository;
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
	
	public Cliente addCliente(Cliente cliente) throws FoundException, BusinessException, NotFoundException{

	    //Verificar si el Chofer ya existe (Recomendación: Mover la búsqueda al inicio)
	    Optional<Cliente> foundCliente = clienteDAO.findByRazonSocial(cliente.getRazonSocial());

	    //Controlar la FoundException si ya existe un Chofer con ese documento
	    if (foundCliente.isPresent()) {
	        // Si ya existe, lanzamos la excepción específica FoundException
	        throw new FoundException("Ya existe un Cliente registrado como: " + cliente.getRazonSocial());
	    }

	    try {
	        //Persistir (guardar) el nuevo Chofer
	        return clienteDAO.save(cliente);
	    } catch (Exception e) {
	        //Manejo de errores de persistencia
	        log.error("Error al intentar guardar el Chofer con documento {}: {}", cliente.getRazonSocial(), e.getMessage(), e);

	        throw BusinessException.builder().ex(e).build();
	    }
	}

}
