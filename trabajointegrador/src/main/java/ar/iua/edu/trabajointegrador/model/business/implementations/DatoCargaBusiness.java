package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.InvalidLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.DatoCargaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatoCargaBusiness implements IDatoCargaBusiness {

	@Autowired
	private DatoCargaRepository datoCargaDAO;

	@Override
	public DatoCarga add(DatoCarga datoCarga) throws InvalidLoadException, BusinessException {
		// Caudal <= 0
		// Masa acumulada <= 0 o menor que el valor anterior

		Optional<Long> lastValue = datoCargaDAO.findUltimaMasaAcumuladaFirstByOrdenIdOrderByTimestampDesc(datoCarga.getOrden().getId());

		if (datoCarga.getUltimo_caudal() <= 0) {
			log.error("Se recibio un dato de carga <=0");
			throw InvalidLoadException.builder()
					.message("Se ingreso un caudal de " + lastValue + ",  menor o igual a 0")
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

	@Override
	public List<DatoCarga> listByOrden(Long ordenId) throws BusinessException {

		try {
			return datoCargaDAO.findAllByOrdenIdSimple(ordenId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}

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
	public Optional<Long> loadLastMasaAcumulada(Long cargaId) throws BusinessException {

		try {
			return datoCargaDAO.findUltimaMasaAcumuladaFirstByOrdenIdOrderByTimestampDesc(cargaId);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}
}
