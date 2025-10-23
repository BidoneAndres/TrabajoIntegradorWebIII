package ar.iua.edu.trabajointegrador.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.models.Sisterna;

public interface SisternaRepository extends JpaRepository<Sisterna, Long>{
	public Optional<Sisterna> findByLicencia(String licencia);
	public Optional<Sisterna> findByLicenciaAndIdNot(String licencia, long id);
}
