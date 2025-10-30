package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.UnProcessableException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IChoferBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ISisternaBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;
import ar.iua.edu.trabajointegrador.model.deserializers.OrdenJsonDeserializer;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

	public OrdenRepository ordenDAO;

	@Autowired
	private IClienteBusiness clienteBusiness;

	@Autowired
	private ICamionBusiness camionBusiness;

	@Autowired
	private ISisternaBusiness sisternaBusiness;

	@Autowired
	private IChoferBusiness choferBusiness;

	@Autowired
	private IProductoBusiness productoBusiness;

	@Override
	public List<Orden> list() throws BusinessException {
		try {
			return ordenDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	@Override
	public Orden load(long id) throws NotFoundException, BusinessException {
		Optional<Orden> ordenFound;
		try {
			ordenFound = ordenDAO.findById(id);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (ordenFound.isEmpty())
			throw NotFoundException.builder().message("El orden " + id + "no se encuentra").build();
		return ordenFound.get();
	}

	@Override
	public Orden cargaExterna(String json)
			throws FoundException, BusinessException, BadRequestException, UnProcessableException {

		ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new OrdenJsonDeserializer(
				Orden.class, choferBusiness, camionBusiness, clienteBusiness, productoBusiness), null);
		Orden orden;

		try {
			orden = mapper.readValue(json, Orden.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw UnProcessableException.builder().message("El formato JSON es incorrecto").build();
		}

		if (orden.getCodExt() == null || orden.getCodExt().isBlank()) {
			String codExt = orden.getPreset() + System.currentTimeMillis() + "";
			orden.setCodExt(codExt);
		}

		return add(orden);
	}

	public Orden loadByCodExt(String codExt) throws NotFoundException, BusinessException {
		 Optional<Orden> r;
        try {
            r = ordenDAO.findOneByCodExt(codExt);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (r.isEmpty()) {
            throw NotFoundException.builder().message("No se encuentra la orden codExt=" + codExt).build();
        }
        return r.get();
		
	}

	public Orden add(Orden orden) throws FoundException, BusinessException {
		Optional<Orden> ordenFound;

        ordenFound = ordenDAO.findOneByCodExt(orden.getCodExt());
        if (ordenFound.isPresent()) {
            throw FoundException.builder().message("Ya existe una orden con el número " + orden.getCodExt()).build();
        }
        ordenFound = ordenDAO.findByCamion_IdAndEstado(orden.getCamion().getId(), Orden.Estado.RECIBIDA);
        if (ordenFound.isPresent()) {
            throw FoundException.builder().message("Ya existe una orden para el camion id=" + orden.getCamion().getId()).build();
        }

        try {
            return ordenDAO.save(orden);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
	}

}
