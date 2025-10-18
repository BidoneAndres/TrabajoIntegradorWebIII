package ar.iua.edu.trabajointegrador.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.models.Camion;

public interface CamionRepository extends JpaRepository<Camion, Long>{
	public Optional<Camion> findByPatente(String patente);
	public Optional<Camion> findByPatenteAndIdNot(String patente, long id);

}
