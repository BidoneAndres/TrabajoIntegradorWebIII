package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.DatoCarga;

public interface DatoCargaRepository extends JpaRepository<DatoCarga, Long> {
	
	@Query("SELECT dc.masaAcumulada FROM DatoCarga dc WHERE dc.orden.claveActivacion = :claveActivacion ORDER BY dc.timestamp DESC LIMIT 1")
	Optional<Double>findMasaAcumulada(Integer claveActivacion);

	@Query("SELECT avg(dc.densidadProducto) FROM DatoCarga dc WHERE dc.orden.claveActivacion = :claveActivacion GROUP BY dc.orden.claveActivacion")
	Optional<Double> calculateDensidadProductoAvg(Integer claveActivacion);
	
	
}
