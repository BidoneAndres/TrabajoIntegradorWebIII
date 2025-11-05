package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.DatoCargaHeader;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.deserializers.DatoCargaJsonDeserializer;
import ar.iua.edu.trabajointegrador.model.persistence.DatoCargaRepository;
import ar.iua.edu.trabajointegrador.model.persistence.OrdenRepository;
import ar.iua.edu.trabajointegrador.util.JsonUtiles;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatoCargaBusiness implements IDatoCargaBusiness {

	@Autowired
	private DatoCargaRepository datoCargaDAO;
	
	@Autowired
	private DatoCargaHeaderBusiness datoCargaHeaderBusiness;

	@Autowired
	private IOrdenBusiness ordenBusiness;

	@Override
	public DatoCarga add(String json)
			throws InvalidLoadException, BusinessException, StateLoadException, NotFoundException {

		// deserializacion
		ObjectMapper mapper = JsonUtiles.getObjectMapper(DatoCarga.class,
				new DatoCargaJsonDeserializer(DatoCarga.class, ordenBusiness), null);
		DatoCarga datoCarga = null;

		try {
			datoCarga = mapper.readValue(json, DatoCarga.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().message(e.getMessage()).build();
		}
		// Caudal <= 0
		// Masa acumulada <= 0 o menor que el valor anterior

		try {
		    //  Validación inicial
			
		    if (datoCarga.getOrden() == null) {
		        log.error("No se encontro la orden con esa clave de activacion");
		        throw NotFoundException.builder()
		                .message("No se encontro la orden con esa clave de activacion")
		                .build();
		    }

		    Long ordenId = datoCarga.getOrden().getId();
		    
		    Double ultimaMasa = null;
		    Optional<DatoCargaHeader> headerAnterior = datoCargaHeaderBusiness.findByOrdenId(ordenId);
		    if (headerAnterior.isPresent()) {
		    	ultimaMasa=headerAnterior.get().getUltimaMasaAcumulada();
		    	
		    }
		    Integer preset = ordenBusiness.findPreset(ordenId);
		    Orden.Estado estado = ordenBusiness.findEstado(ordenId);
		    log.error("sksosks" + preset + ultimaMasa + estado);

		    Double masaActual = datoCarga.getMasaAcumulada();
		    Double caudalActual = datoCarga.getCaudal();

		    if (estado != Estado.ESTADO_2_EN_PROCESO_DE_CARGA) {
		        log.error("La orden no está en el estado LISTO_PARA_CARGA");
		        throw StateLoadException.builder()
		                .message("ERROR: La orden no está en el estado LISTO_PARA_CARGA (actual: " + estado + ")")
		                .build();
		    }

		    if (caudalActual == null || caudalActual <= 0) {
		        log.error("Se recibió un caudal inválido: " + caudalActual);
		        throw InvalidLoadException.builder()
		                .message("ERROR: Caudal inválido (" + caudalActual + ")")
		                .build();
		    }

		    if (masaActual == null || masaActual <= 0) {
		        log.error("Se recibió una masa inválida: " + masaActual);
		        throw InvalidLoadException.builder()
		                .message("ERROR: Masa acumulada inválida (" + masaActual + ")")
		                .build();
		    }

		    //  Validación de consistencia con la masa anterior
		    if (ultimaMasa!= null && masaActual < ultimaMasa) {
		        log.error("Masa acumulada menor a la anterior: actual=" + masaActual + ", anterior=" + ultimaMasa);
		        throw InvalidLoadException.builder()
		                .message("ERROR: Masa acumulada menor a la anterior (" + masaActual + " < " + ultimaMasa + ")")
		                .build();
		    }

		    //  Verificación de preset
		    if (preset != null && masaActual > preset) {
		        log.error("Se quiso enviar una masa mayor al preset: " + masaActual + " > " + preset);
		        throw InvalidLoadException.builder()
		                .message("ERROR: Masa mayor al preset (" + masaActual + " > " + preset + ")")
		                .build();
		    }

		    //  Guardado final
		    try {
		    	datoCargaHeaderBusiness.add(datoCarga);
		        return datoCargaDAO.save(datoCarga);
		    } catch (Exception e) {
		        log.error("Error al guardar DatoCarga", e);
		        throw BusinessException.builder()
		                .message("Error interno al guardar el dato de carga")
		                .ex(e)
		                .build();
		    }

		} catch (NotFoundException | InvalidLoadException | StateLoadException | BusinessException e) {
		    // Excepciones esperadas (controladas)
		    throw e;
		} catch (Exception e) {
		    // Cualquier otro error inesperado
		    log.error("Error inesperado en el proceso de carga", e);
		    throw BusinessException.builder()
		            .message("Error inesperado en el proceso de carga")
		            .ex(e)
		            .build();
		}

	}

	/*
	 * @Override public List<DatoCarga> listByOrden(Long ordenId) throws
	 * BusinessException {
	 * 
	 * try { return datoCargaDAO.findAllByOrdenIdSimple(ordenId); } catch (Exception
	 * e) { log.error(e.getMessage(), e); throw
	 * BusinessException.builder().ex(e).message(e.getMessage()).build(); }
	 * 
	 * }
	 */

	@Override
	public List<DatoCarga> list() throws BusinessException {

		try {
			return datoCargaDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}

	@Override
	public Optional<Double> loadLastMasaAcumulada(Integer claveActivacion) throws BusinessException {

		try {
			return datoCargaDAO.findMasaAcumulada(claveActivacion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}
	
	@Override
	public Optional<Double> calculateDensidadProductoAvg(Integer numeroOrden) throws BusinessException {

		try {
			return datoCargaDAO.calculateDensidadProductoAvg(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}
	
	@Override
	public Optional<Double> calculateTemperaturaAvg(Integer numeroOrden) throws BusinessException {

		try {
			return datoCargaDAO.calculateTemperaturaAvg(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}
	
	@Override
	public Optional<Double> calculateCaudalAvg(Integer numeroOrden) throws BusinessException {

		try {
			return datoCargaDAO.calculateCaudalAvg(numeroOrden);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}

}
