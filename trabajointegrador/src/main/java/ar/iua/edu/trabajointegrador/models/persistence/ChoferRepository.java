package ar.iua.edu.trabajointegrador.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.models.Chofer;
import ar.iua.edu.trabajointegrador.models.Cliente;

public interface ChoferRepository extends JpaRepository<Chofer, Long>{
	public Optional<Chofer> findByDocumento(String documento);
	public Optional<Chofer> findByDocumentoAndIdNot(String documento, long id);
}
