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
import ar.iua.edu.trabajointegrador.model.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;
import ar.iua.edu.trabajointegrador.model.deserializers.OrdenJsonDeserializer;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;
import ar.iua.edu.trabajointegrador.util.generadorPassword;

@Service
@Slf4j
public class OrdenBusiness implements IOrdenBusiness {

	@Autowired
	private OrdenRepository ordenDAO;

	@Autowired
	private IClienteBusiness clienteBusiness;

	@Autowired
	private ICamionBusiness camionBusiness;

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

		ObjectMapper mapper = JsonUtils.getObjectMapper(Orden.class, new OrdenJsonDeserializer(Orden.class,
				choferBusiness, camionBusiness, clienteBusiness, productoBusiness), null);
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

		// chequeos para orden
		ordenFound = ordenDAO.findByNumeroOrden(orden.getNumeroOrden()); // ya hay NUMERO DE ORDEN
		if (ordenFound.isPresent()) {
			throw FoundException.builder().message("Ya existe una orden con el número " + orden.getNumeroOrden())
					.build();
		}
		ordenFound = ordenDAO.findByCamion_PatenteAndEstado(orden.getCamion().getPatente(),
				Orden.Estado.ESTADO_1_PENDIENTE_PESAJE_INICIAL); //Yya hay ese camion con ese estado
		if (ordenFound.isPresent()) {
			throw FoundException.builder().message("Ya existe una orden en estado: " + orden.getEstado()  + " para el camion con patente :" + orden.getCamion().getPatente())
					.build();
		}
		
		ordenFound = ordenDAO.findOneByCodExt(orden.getCodExt()); //Codigo externo 
		if (ordenFound.isPresent()) {
			throw FoundException.builder().message("Ya existe una orden con el codigo externo: " + orden.getCodExt())
					.build();
		}

		try {
			return ordenDAO.save(orden);
		} catch (Exception e) {
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}
	}

	public Orden registrarPesajeInicial(String patente, float pesoInicial)
			throws NotFoundException, BusinessException, UnProcessableException {
		Optional<Orden> ordenFound;

		ordenFound = ordenDAO.findByCamion_PatenteAndEstado(patente, Orden.Estado.ESTADO_1_PENDIENTE_PESAJE_INICIAL);
		if (ordenFound.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden para el camion de patente = " + patente
					+ " en el estado ESTADO_1_PENDIENTE_PESAJE_INICIAL").build();
		}

		Orden orden = ordenFound.get();
		int password = generadorPassword.generarPassword();
		orden.setClaveActivacion(password);
		orden.setPesoInicial(pesoInicial);
		orden.setEstado(Orden.Estado.ESTADO_2_PESAJE_INICIAL_REGISTRADO);
		try {
			return ordenDAO.save(orden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}

	}// esto se usa en carga estado 2

	public Orden activarCarga(Integer numeroOrden, Integer claveActivacion)
			throws NotFoundException, BusinessException {
		Optional<Orden> ordenFound;

		ordenFound = ordenDAO.findByNumeroOrdenAndClaveActivacion(numeroOrden, claveActivacion);
		if (ordenFound.isPresent()) {
			ordenFound.get().setEstado(Orden.Estado.ESTADO_2_EN_PROCESO_DE_CARGA);

			try {
				return ordenDAO.save(ordenFound.get());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw BusinessException.builder().ex(e).build();
			}
		}

		else {
			throw NotFoundException.builder().message(
					"No se encontro la orden con numero " + numeroOrden + " y clave de activacion " + claveActivacion)
					.build();

		}

	}

	public Orden desactivarCarga(Integer numeroOrden) throws NotFoundException, BusinessException {
		Optional<Orden> ordenFound;

		ordenFound = ordenDAO.findByNumeroOrden(numeroOrden);
		if (ordenFound.isPresent()) {
			ordenFound.get().setEstado(Orden.Estado.ESTADO_3_CERRADA_PARA_CARGA);

			try {
				return ordenDAO.save(ordenFound.get());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw BusinessException.builder().ex(e).message(e.getMessage()).build();
			}
		}

		else {
			throw NotFoundException.builder().message("No se encontro la orden con numero de orden " + numeroOrden)
					.build();

		}

	}

	public Orden loadByClaveActivacion(Integer claveActivacion) throws NotFoundException, BusinessException {
		Optional<Orden> r;
		try {
			r = ordenDAO.findByClaveActivacion(claveActivacion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con clave=" + claveActivacion).build();
		}
		return r.get();

	}

	public Optional<Orden> findById(long ordenId) throws NotFoundException, BusinessException {
		Optional<Orden> r;
		try {
			r = ordenDAO.findById(ordenId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con clave=" + ordenId).build();
		}
		return r;

	}

	public Optional<Orden> findByNumeroOrden(int numeroOrden) throws NotFoundException, BusinessException {
		Optional<Orden> r;
		try {
			r = ordenDAO.findByNumeroOrden(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con numero=" + numeroOrden).build();
		}
		return r;

	}

	public Integer findPreset(Long ordenId) throws NotFoundException, BusinessException {
		Integer r;
		try {
			r = ordenDAO.findPreset(ordenId);
			return r;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
	}

	public Orden.Estado findEstado(Long ordenId) throws NotFoundException, BusinessException {
		Orden.Estado r;
		try {
			r = ordenDAO.findEstado(ordenId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		return r;
	}

	public Optional<Orden> findByIdAndClaveActivacion(long ordenId, int claveActivacion)
			throws NotFoundException, BusinessException {
		Optional<Orden> r;
		try {
			r = ordenDAO.findByIdAndClaveActivacion(ordenId, claveActivacion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message(
					"No se encuentra la orden con clave=" + ordenId + " y clave de activacion " + claveActivacion)
					.build();
		}
		return r;

	}

	public Optional<Orden> findByNumeroOrdenAndClaveActivacion(int numeroOrden, int claveActivacion)
			throws NotFoundException, BusinessException {
		Optional<Orden> r;
		try {
			r = ordenDAO.findByNumeroOrdenAndClaveActivacion(numeroOrden, claveActivacion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		if (r.isEmpty()) {
			throw NotFoundException.builder().message("No se encuentra la orden con numero de orden=" + numeroOrden
					+ " y clave de activacion " + claveActivacion).build();
		}
		return r;

	}

}