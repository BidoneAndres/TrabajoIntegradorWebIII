package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.Conciliacion;
import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.Orden;

public interface ConciliacionRepository extends JpaRepository<Conciliacion, Long> {
	@Query("SELECT dc FROM Conciliacion dc WHERE dc.orden.numeroOrden = :numeroOrden")
	Optional<Conciliacion> findByNumeroOrden(int numeroOrden);
}
