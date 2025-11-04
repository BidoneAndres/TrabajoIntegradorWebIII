package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.DatoCargaHeader;

public interface DatoCargaHeaderRepository extends JpaRepository<DatoCargaHeader, Long> {
	
	@Query("SELECT dc FROM DatoCargaHeader dc WHERE dc.orden.claveActivacion = :claveActivacion")
	DatoCargaHeader findOneByClaveActivacion(Integer claveActivacion);
	
	
}
