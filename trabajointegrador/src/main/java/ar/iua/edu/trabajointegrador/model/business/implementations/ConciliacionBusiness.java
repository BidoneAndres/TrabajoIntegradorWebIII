package ar.iua.edu.trabajointegrador.model.business.implementations;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.iua.edu.trabajointegrador.model.Conciliacion;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.StateLoadException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IConciliacionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IDatoCargaHeaderBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.model.persistence.ConciliacionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ConciliacionBusiness implements IConciliacionBusiness {

	@Autowired
	private IOrdenBusiness ordenBusiness;

	@Autowired
	private IDatoCargaBusiness datoCargaBusiness;

	@Autowired
	private IDatoCargaHeaderBusiness datoCargaHeaderBusiness;

	@Autowired
	private ConciliacionRepository concilacionDAO;

	@Override
	public Conciliacion add(float pesajeFinal, int numeroOrden)
			throws NotFoundException, BusinessException, StateLoadException {
		// busqueda de la orden
		Optional<Orden> orden = ordenBusiness.findByNumeroOrden(numeroOrden);
		Double productoCargado = datoCargaHeaderBusiness.findByOrdenId(orden.get().getId()).get()
				.getUltimaMasaAcumulada();
		if (productoCargado == null || orden.isEmpty()) {
			throw NotFoundException.builder().message("No se encontro la orden asociada a esa clave de activacion")
					.build();
		} else {
			if (orden.get().getEstado() != Estado.ESTADO_3_CERRADA_PARA_CARGA) {
				throw StateLoadException.builder().message(
						"La orden no esta en ESTADO_3_CERRADA_PARA_CARGA, estado actual: " + orden.get().getEstado())
						.build();
			}
			// guardamos en la orden el pesaje final
			orden.get().setEstado(Orden.Estado.ESTADO_4_FINALIZADA);
			orden.get().setPesoFinal(pesajeFinal);

			Conciliacion conciliacion = new Conciliacion();
			conciliacion.setOrden(orden.get());
			conciliacion.setPesoFinal(pesajeFinal);
			conciliacion.setPesoInicial(orden.get().getPesoInicial());
			conciliacion.setProductoCargado(productoCargado);
			float neto = pesajeFinal - orden.get().getPesoInicial();
			conciliacion.setNetoPorBalanza(neto);
			conciliacion.setDiferenciaBalanzaCaudalimetro(neto - productoCargado);

			// promedios
			int numeroOrden1 = orden.get().getNumeroOrden();
			conciliacion.setPromedioDensidad(datoCargaBusiness.calculateDensidadProductoAvg(numeroOrden1).get());
			conciliacion.setPromedioCaudal(datoCargaBusiness.calculateCaudalAvg(numeroOrden1).get());
			conciliacion.setPromedioTemperatura(datoCargaBusiness.calculateTemperaturaAvg(numeroOrden1).get());

			return concilacionDAO.save(conciliacion);

		}
	}

	@Override
	public List<Conciliacion> list() throws BusinessException {
		try {
			return concilacionDAO.findAll();
		} catch (Exception e) {
			throw BusinessException.builder().message(e.getMessage()).build();
		}
	}

	@Override
	public Conciliacion loadByNumeroOrden(int numeroOrden) throws BusinessException, NotFoundException {
		try {
			Optional<Conciliacion> c = concilacionDAO.findByNumeroOrden(numeroOrden);
			if (c.isEmpty()) {
				throw NotFoundException.builder().message("No se encontro la conciliacion para la orden: " + numeroOrden)
				.build();
			}
			return c.get();
		}
		 catch (NotFoundException e) {
				throw NotFoundException.builder().message(e.getMessage()).build();
			}
		catch (Exception e) {
			throw BusinessException.builder().message(e.getMessage()).build();
		}

	}

}
