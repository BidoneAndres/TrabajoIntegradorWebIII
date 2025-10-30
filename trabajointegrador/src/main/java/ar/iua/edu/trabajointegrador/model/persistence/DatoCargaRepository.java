package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.DatoCarga;

public interface DatoCargaRepository extends JpaRepository<DatoCarga, Long> {
	
	@Query("SELECT d FROM DatoCarga d WHERE d.orden.id = :ordenId")
	List<DatoCarga> findAllByOrdenIdSimple(Long ordenId);
	
	Optional<Long> findUltimaMasaAcumuladaFirstByOrdenIdOrderByTimestampDesc(Long ordenId);

}
