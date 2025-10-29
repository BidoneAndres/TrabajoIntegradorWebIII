package ar.iua.edu.trabajointegrador.models.persistence;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioExternoRepository extends JpaRepository<UsuarioExterno, Long> {
    Optional<UsuarioExterno> findByEmail(String email);
}
