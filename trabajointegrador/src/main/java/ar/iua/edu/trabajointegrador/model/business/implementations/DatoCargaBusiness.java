package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.time.Duration;
import java.time.LocalDateTime;
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
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.deserializers.DatoCargaJsonDeserializer;
import ar.iua.edu.trabajointegrador.model.persistence.DatoCargaRepository;
import ar.iua.edu.trabajointegrador.util.JsonUtiles;
import org.springframework.context.ApplicationEventPublisher;
import ar.iua.edu.trabajointegrador.events.Evento;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IAlarmaBusiness;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@Slf4j
public class DatoCargaBusiness implements IDatoCargaBusiness {

	@Autowired
	private DatoCargaRepository datoCargaDAO;

	@Autowired
	private DatoCargaHeaderBusiness datoCargaHeaderBusiness;

	@Autowired
	private IOrdenBusiness ordenBusiness;

	@Autowired
	private IAlarmaBusiness alarmaBusiness;

	@Autowired
    private ApplicationEventPublisher applicationEventPublisher;

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
			// Validación inicial

			if (datoCarga.getOrden() == null) {
				log.error("No se encontro la orden con ese numero de orden");
				throw NotFoundException.builder().message("No se encontro la orden con ese numero de orden").build();
			}

			Long ordenId = datoCarga.getOrden().getId();

			// Asignar timestamp si es null
			if (datoCarga.getTimestamp() == null) {
				datoCarga.setTimestamp(LocalDateTime.now());
			}
			
			log.error("hora " + datoCarga.getTimestamp());
			Double ultimaMasa = null;
			Optional<DatoCargaHeader> headerAnterior = datoCargaHeaderBusiness.findByOrdenId(ordenId);
			if (headerAnterior.isPresent()) {
				ultimaMasa = headerAnterior.get().getUltimaMasaAcumulada();
				int ultimaTemperatura = headerAnterior.get().getUltimaTemperatura();
				LocalDateTime ultimoTimestamp = headerAnterior.get().getTimestamp();
				LocalDateTime timestampActual = LocalDateTime.now();
				
				if (ultimoTimestamp != null && timestampActual != null) {
                    long milisegundosTranscurridos = Duration.between(ultimoTimestamp, timestampActual).toMillis();
                    
                    if (milisegundosTranscurridos < 5000) {
                        log.info("Se ignoró el dato de carga. Solo pasaron {} ms desde el último registro.", milisegundosTranscurridos);
                        
                        // Aquí decides qué hacer si no pasaron 5000ms. 
                        // Opción 1: Salir silenciosamente retornando null o el dato sin guardar.
                        //return null; 
                        
                        // Opción 2: Lanzar una excepción si tu cliente API necesita saber que fue rechazado.
                         throw InvalidLoadException.builder()
                               .message("ERROR: Deben pasar al menos 5000ms entre cargas")
                               .build();
                    }
                }
				
				

			}
			Integer preset = ordenBusiness.findPreset(ordenId);
			Orden.Estado estado = ordenBusiness.findEstado(ordenId);

			Double masaActual = datoCarga.getMasaAcumulada();
			Double caudalActual = datoCarga.getCaudal();
			int temperaturaActual = datoCarga.getTemperatura();

			 // Validación de preset
			if (preset == null) {
				log.error("No se encontró el preset para la orden id={}", ordenId);
				throw NotFoundException.builder().message("No se encontró el preset para la orden id=" + ordenId).build();
			}

			if (estado != Estado.ESTADO_2_EN_PROCESO_DE_CARGA) {
				log.error("La orden no está en el estado LISTO_PARA_CARGA");
				throw StateLoadException.builder()
						.message("ERROR: La orden no está en el estado LISTO_PARA_CARGA (actual: " + estado + ")")
						.build();
			}

			if (caudalActual == null || caudalActual <= 0) {
				log.error("Se recibió un caudal inválido: " + caudalActual);
				throw InvalidLoadException.builder().message("ERROR: Caudal inválido (" + caudalActual + ")").build();
			}

			if (masaActual == null || masaActual <= 0) {
				log.error("Se recibió una masa inválida: " + masaActual);
				throw InvalidLoadException.builder().message("ERROR: Masa acumulada inválida (" + masaActual + ")")
						.build();
			}
			try {
    			datoCargaHeaderBusiness.add(datoCarga);
			    datoCarga = datoCargaDAO.save(datoCarga); // Actualizamos la referencia con el objeto persistido
			} catch (Exception e) {
			    log.error("Error al guardar DatoCarga", e);
			    throw BusinessException.builder().message("Error interno al guardar el dato de carga").ex(e).build();
			}

						// Validación de consistencia con la masa anterior
			if (ultimaMasa != null && masaActual < ultimaMasa) {
				log.error("Masa acumulada menor a la anterior: actual=" + masaActual + ", anterior=" + ultimaMasa);
				throw InvalidLoadException.builder()
						.message("ERROR: Masa acumulada menor a la anterior (" + masaActual + " < " + ultimaMasa + ")")
						.build();
			}

			try {
			    datoCargaHeaderBusiness.add(datoCarga);
			    datoCarga = datoCargaDAO.save(datoCarga); // Actualizamos la referencia con el objeto persistido
			} catch (Exception e) {
			    log.error("Error al guardar DatoCarga", e);
			    throw BusinessException.builder().message("Error interno al guardar el dato de carga").ex(e).build();
			}

			// 2. DESPUÉS DISPARAMOS EL EVENTO (con el objeto ya guardado)
			if(temperaturaActual < -5 || temperaturaActual > 30) {
			    if (!alarmaBusiness.alarmaAceptada(ordenId)) {
			        applicationEventPublisher.publishEvent(new Evento(datoCarga, Evento.TipoEvento.TEMPERATURA_ALTA));
			    }
			}

			return datoCarga;
			

		} catch (NotFoundException | InvalidLoadException | StateLoadException | BusinessException e) {
			// Excepciones esperadas (controladas)
			throw e;
		} catch (Exception e) {
			// Cualquier otro error inesperado
			log.error("Error inesperado en el proceso de carga", e);
			throw BusinessException.builder().message("Error inesperado en el proceso de carga").ex(e).build();
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
	public List<DatoCarga> listByNumeroOrden(int numeroOrden) throws BusinessException {

		try {
			return datoCargaDAO.findAllByNumeroOrden(numeroOrden);
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

	@Override
    public DatoCarga load(Integer id) throws NotFoundException, BusinessException {
        List<DatoCarga> datoCargaFound;

        try {
            datoCargaFound = datoCargaDAO.findAllByNumeroOrden(id);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().ex(e).build();
        }
        if (datoCargaFound.isEmpty())
            throw NotFoundException.builder().message("No se encuentra el DatoCarga id= " + id).build();
        return datoCargaFound.get(0);
    }

	@Override
    public Page<DatoCarga> listByOrden(Orden orden, Pageable pageable) {
        Optional<Page<DatoCarga>> datoCarga = datoCargaDAO.findAllByOrden(orden, pageable);

        return datoCarga.orElseGet(Page::empty);
    }

	@Override
	public DatoCarga add(DatoCarga datoCarga) throws FoundException, BusinessException {
		try {
			load((int) datoCarga.getId());
            throw FoundException.builder().message("Ya existe el dato de carga id = " + datoCarga.getId()).build();
        } catch (NotFoundException e) {
            // log.trace(e.getMessage(), e);
        }

        try {
            return datoCargaDAO.save(datoCarga);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw BusinessException.builder().message("Error al Crear Nuevo DatoCarga").build();
        }

    }



}
