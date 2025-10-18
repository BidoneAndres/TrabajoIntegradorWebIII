package ar.iua.edu.trabajointegrador.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.models.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{
	public Optional<Cliente> findByRazonSocial(String razonSocial);
	public Optional<Cliente> findByRazonSocialAndIdNot(String razonSocial, long id);
}
