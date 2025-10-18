package ar.iua.edu.trabajointegrador.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.models.Orden;
import ar.iua.edu.trabajointegrador.models.Orden.Estado;

public interface OrdenRepository extends JpaRepository<Orden, Long>{
	public Optional<Orden> findByClaveActivacion(String claveActivacion);
	public Optional<Orden> findByIdAndEstado(long id, Estado estado);
}
