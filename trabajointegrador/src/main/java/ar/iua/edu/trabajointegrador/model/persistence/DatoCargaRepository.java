package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.Orden;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ar.iua.edu.trabajointegrador.model.DatoCarga;

public interface DatoCargaRepository extends JpaRepository<DatoCarga, Long> {



	Optional<Page<DatoCarga>> findAllByOrden(Orden order, Pageable pageable);
	
	@Query("SELECT dc.masaAcumulada FROM DatoCarga dc WHERE dc.orden.claveActivacion = :claveActivacion ORDER BY dc.timestamp DESC LIMIT 1")
	Optional<Double>findMasaAcumulada(Integer claveActivacion);

	@Query("SELECT avg(dc.densidadProducto) FROM DatoCarga dc WHERE dc.orden.numeroOrden = :numeroOrden GROUP BY dc.orden.numeroOrden")
	Optional<Double> calculateDensidadProductoAvg(Integer numeroOrden);
	
	@Query("SELECT avg(dc.temperatura) FROM DatoCarga dc WHERE dc.orden.numeroOrden = :numeroOrden GROUP BY dc.orden.numeroOrden")
	Optional<Double> calculateTemperaturaAvg(Integer numeroOrden);
	
	@Query("SELECT avg(dc.caudal) FROM DatoCarga dc WHERE dc.orden.numeroOrden = :numeroOrden GROUP BY dc.orden.numeroOrden")
	Optional<Double> calculateCaudalAvg(Integer numeroOrden);
	
	@Query("SELECT dc FROM DatoCarga dc WHERE dc.orden.numeroOrden = :numeroOrden ORDER BY dc.timestamp")
	List <DatoCarga> findAllByNumeroOrden(int numeroOrden);
	
}
