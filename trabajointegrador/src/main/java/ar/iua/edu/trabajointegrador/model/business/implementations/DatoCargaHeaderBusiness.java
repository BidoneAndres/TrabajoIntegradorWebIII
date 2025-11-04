package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.DatoCargaHeader;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
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
		// este metodo es medio bobo, las logicas enserio las maneja el dato carga
		// solamente el trata de guardar lo que le llega, sin importar si el dato es
		// valido o no

		// la unica validacion que hacemos es si tiene orden asociada
		if (datoCarga.getOrden() != null) {
			Integer claveActivacion = datoCarga.getOrden().getClaveActivacion();
			Orden.Estado estado = ordenDAO.findEstado(claveActivacion);

			// y la otra es que este en el estado correcto
			if (estado == Estado.ESTADO_2_EN_PROCESO_DE_CARGA) {

				DatoCargaHeader datoCargaHeader = new DatoCargaHeader();
				// hacemos el traspaso al objeto datoCarga
				datoCargaHeader.setUltimaDensidadProducto(datoCarga.getDensidadProducto());
				datoCargaHeader.setUltimaMasaAcumulada(datoCarga.getMasaAcumulada());
				datoCargaHeader.setUltimaTemperatura(datoCarga.getTemperatura());
				datoCargaHeader.setUltimoCaudal(datoCarga.getCaudal());
				datoCargaHeader.setOrden(datoCarga.getOrden());

				try {
					DatoCargaHeader datoCargaHeaderAnterior = datoCargaHeaderDAO
							.findOneByClaveActivacion(claveActivacion);
					if (datoCargaHeaderAnterior != null) {
						//para que sobrescriba
						datoCargaHeader.setId(datoCargaHeaderAnterior.getId());
					}
					return datoCargaHeaderDAO.save(datoCargaHeader);
				} catch (Exception e) {
					log.error("Error al guardar DatoCargaHeader", e);
					throw BusinessException.builder()
							.message("Error interno al guardar el dato de carga en la cabecera").ex(e).build();
				}
			}

		}

		// tiene que devolver algo, si llego aca no tiene una orden asociada, se ocupara
		// de este error dato carga business
		return null;
	}
}
