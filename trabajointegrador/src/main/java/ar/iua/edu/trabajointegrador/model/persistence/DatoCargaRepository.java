package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.DatoCarga;

public interface DatoCargaRepository extends JpaRepository<DatoCarga, Long> {
	
	@Query("SELECT dc.ultimaMasaAcumulada FROM DatoCarga dc WHERE dc.orden.id = :ordenId ORDER BY dc.timestamp DESC LIMIT 1")
	Optional<Double>findLastMasaAcumulada(Long ordenId);

}
