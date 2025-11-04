package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
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
	private OrdenRepository ordenDAO;

	@Autowired(required = false)
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

		    Integer claveActivacion = datoCarga.getOrden().getClaveActivacion();
		    
		    Optional<Double> ultimaMasa = this.loadLastMasaAcumulada(claveActivacion);
		    Integer preset = ordenDAO.findPreset(claveActivacion);
		    Orden.Estado estado = ordenDAO.findEstado(claveActivacion);

		    Double masaActual = datoCarga.getUltimaMasaAcumulada();
		    Double caudalActual = datoCarga.getUltimoCaudal();

		    if (estado != Estado.LISTO_PARA_CARGA) {
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
		    if (ultimaMasa.isPresent() && masaActual < ultimaMasa.get()) {
		        log.error("Masa acumulada menor a la anterior: actual=" + masaActual + ", anterior=" + ultimaMasa.get());
		        throw InvalidLoadException.builder()
		                .message("ERROR: Masa acumulada menor a la anterior (" + masaActual + " < " + ultimaMasa.get() + ")")
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
			return datoCargaDAO.findLastMasaAcumulada(claveActivacion);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}
}
