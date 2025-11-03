package ar.iua.edu.trabajointegrador.model.business.implementations;

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
	public DatoCarga add(String json) throws InvalidLoadException, BusinessException, StateLoadException {
		
		
		//deserializacion
		ObjectMapper mapper = JsonUtiles.getObjectMapper(DatoCarga.class,
				new DatoCargaJsonDeserializer(DatoCarga.class, ordenBusiness),null);
		DatoCarga datoCarga = null;
		
		try {
			datoCarga = mapper.readValue(json, DatoCarga.class);
		} catch (JsonProcessingException e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).build();
		}
		// Caudal <= 0
		// Masa acumulada <= 0 o menor que el valor anterior

		Integer claveActivacion = datoCarga.getOrden().getClaveActivacion();

		// busqueda de orden
		Optional<Double> ultimaMasa = this.loadLastMasaAcumulada(claveActivacion);
		Integer preset = ordenDAO.findPreset(claveActivacion);
		Orden.Estado estado = ordenDAO.findEstado(claveActivacion);

		// getters de la que me llego recien
		Double masaActual = datoCarga.getUltimaMasaAcumulada();
		Double caudalActual = datoCarga.getUltimoCaudal();

		// check de que la orden este habilitada
		if (estado == Estado.LISTO_PARA_CARGA) {

			// check de valores invalidos
			if (caudalActual <= 0) {
				log.error("Se recibio un dato de carga <=0");
				throw InvalidLoadException.builder()
						.message("ERROR: Se ingreso un caudal de " + caudalActual + ",  menor o igual a 0").build();
			}
			if (masaActual <= 0) {
				log.error("Se recibio un dato de masa acumulada <=0");
				throw InvalidLoadException.builder()
						.message("ERROR: Se ingreso una masa de " + masaActual + ",  menor o igual a 0").build();
			}

			// chcek de validez en cuanto a valores anteriores
			if (ultimaMasa.isPresent() && masaActual < ultimaMasa.get()) {
				log.error("Se recibio una masa acumulada menor a la anterior");
				throw InvalidLoadException.builder()
						.message("ERROR: se recibio una masa acumulada menor a la anterior, " + masaActual
								+ ", menor a " + ultimaMasa.get())
						.build();
			} else {

				// pesaje llega al preset, cierre de carga
				if (masaActual > preset) {
					log.error("Se quiso enviar una masa mayor al preset: " + masaActual + " es mayor a " + preset);
					throw InvalidLoadException.builder().message(
							"ERROR: Se quiso enviar una masa mayor al preset " + masaActual + " es mayor a " + preset)
							.build();
				} else {
					try {

						return datoCargaDAO.save(datoCarga);

					} catch (Exception e) {

						log.error(e.getMessage(), e);
						throw BusinessException.builder().ex(e).build();

					}
				}

			}
		}
		else {
			log.error("La orden no esta en el estado LISTO_PARA_CARGA");
			throw StateLoadException.builder()
					.message("ERROR: La orden no esta en el estado LISTO_PARA_CARGA, esta en estado " + estado).build();

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
