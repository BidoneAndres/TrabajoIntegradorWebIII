package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Orden.Estado;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


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
	public Optional<Orden> findByNumeroOrden(int numeroOrden);
	public Optional<Orden> findByNumeroOrdenAndClaveActivacion(Integer numeroOrden, Integer claveActivacion);
	@Query("SELECT o FROM Orden o WHERE (:estados IS NULL OR o.estado IN :estados)")
    Page<Orden> findByEstados(@Param("estados") List<String> estados, Pageable pageable);
}
