package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;

public interface OrdenRepository extends JpaRepository<Orden, Long>{
	public Optional<Orden> findByClaveActivacion(Integer claveActivacion);
	public Optional<Orden> findByIdAndEstado(long id, Estado estado);
	public Optional<Orden> findOneByCodExt(String codExt);

	//esto se usa en dato carga
	@Query("SELECT dc.preset FROM Orden dc WHERE dc.id = :ordenId")
	public Integer findPreset(Long ordenId);

	@Query("SELECT dc.estado FROM Orden dc WHERE dc.id = :ordenId")
	public Orden.Estado findEstado(Long ordenId);
	
	//public Optional<Orden> findByCamion_IdAndEstado(Long camion_id, Orden.Estado estado);
	
	public Optional<Orden> findByCamion_PatenteAndEstado(String patente, Orden.Estado estado);
	public Optional<Orden> findByIdAndClaveActivacion(long id, int claveActivacion);
}
