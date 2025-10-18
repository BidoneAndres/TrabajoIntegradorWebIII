package ar.iua.edu.trabajointegrador.models.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.models.Producto;

public interface ProductoRepository extends JpaRepository <Producto, Long>{
	Optional<Producto> findByProducto(String producto);

    Optional<Producto> findByProductoAndIdNot(String producto, Long id);

}
