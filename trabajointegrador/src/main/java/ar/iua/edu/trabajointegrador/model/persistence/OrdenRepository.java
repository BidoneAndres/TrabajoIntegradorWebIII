package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;

public interface OrdenRepository extends JpaRepository<Orden, Long>{
	public Optional<Orden> findByClaveActivacion(String claveActivacion);
	public Optional<Orden> findByIdAndEstado(long id, Estado estado);
	public Optional<Orden> findOneByCodExt(String codExt);
	
	@Query("SELECT dc.preset FROM Orden dc WHERE dc.id = :ordenId")
	Integer findPreset(Long ordenId);



	
}
