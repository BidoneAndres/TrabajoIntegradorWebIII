package ar.iua.edu.trabajointegrador.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.model.Chofer;

public interface ChoferRepository extends JpaRepository<Chofer, Long>{
	public Optional<Chofer> findByDocumento(String documento);
	public Optional<Chofer> findByDocumentoAndIdNot(String documento, long id);
}
