package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.DatoCargaHeader;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaHeaderBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.DatoCargaHeaderRepository;
import ar.iua.edu.trabajointegrador.model.persistence.OrdenRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DatoCargaHeaderBusiness implements IDatoCargaHeaderBusiness {

	@Autowired
	private DatoCargaHeaderRepository datoCargaHeaderDAO;

	@Autowired
	private OrdenRepository ordenDAO;

	@Override
	public DatoCargaHeader add(DatoCarga datoCarga) throws BusinessException {
		DatoCargaHeader datoCargaHeader = new DatoCargaHeader();
		// hacemos el traspaso al objeto datoCarga
		datoCargaHeader.setUltimaDensidadProducto(datoCarga.getDensidadProducto());
		datoCargaHeader.setUltimaMasaAcumulada(datoCarga.getMasaAcumulada());
		datoCargaHeader.setUltimaTemperatura(datoCarga.getTemperatura());
		datoCargaHeader.setUltimoCaudal(datoCarga.getCaudal());
		datoCargaHeader.setOrden(datoCarga.getOrden());

		try {
			Optional<DatoCargaHeader> datoCargaHeaderAnterior = datoCargaHeaderDAO.findByOrdenId(datoCarga.getOrden().getId());
			if (datoCargaHeaderAnterior.isPresent()) {
				//para que sobrescriba
				datoCargaHeader.setId(datoCargaHeaderAnterior.get().getId());
			}
			return datoCargaHeaderDAO.save(datoCargaHeader);
		} catch (Exception e) {
			log.error("Error al guardar DatoCargaHeader", e);
			throw BusinessException.builder()
					.message("Error interno al guardar el dato de carga en la cabecera").ex(e).build();
		}
		
	}

	@Override
	public Optional<DatoCargaHeader> findByOrdenId(long ordenId) throws BusinessException, NotFoundException {
		try {
			return datoCargaHeaderDAO.findByOrdenId(ordenId);
		}
		catch(Exception e) {
			throw NotFoundException.builder().message(e.getMessage()).build();
		}
	}
	
	@Override
	public List<DatoCargaHeader> listHeaders() throws BusinessException {

		try {
			return datoCargaHeaderDAO.findAll();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw BusinessException.builder().ex(e).message(e.getMessage()).build();
		}

	}
}

