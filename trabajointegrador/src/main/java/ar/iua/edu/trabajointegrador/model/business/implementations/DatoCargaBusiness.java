package ar.iua.edu.trabajointegrador.model.business.implementations;

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

		if (datoCarga.getUltimo_caudal() <= 0) {
			log.error("Se recibio un dato de carga <=0");
			throw InvalidLoadException.builder()
					.message("Se ingreso un caudal de " + datoCarga.getUltimo_caudal() + ",  menor o igual a 0")
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
