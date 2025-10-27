package ar.iua.edu.trabajointegrador.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.iua.edu.trabajointegrador.integration.cli1.model.ProductoCli1;
import jakarta.transaction.Transactional;

public interface ProductoCli1Repository extends JpaRepository<ProductoCli1, Long>{
	
	Optional<ProductoCli1> findByIdCli1(String idCli1);
	
	Optional<ProductoCli1> findByProductoAndIdCli1NotAndCodCli1Temp(String producto, String idCli1, boolean codCli1Temp);

	Optional<ProductoCli1> findProductoCli1ByProducto(String producto);
	
	@Transactional
    @Modifying
    @Query(value = "INSERT INTO cli1_productos (id_producto, id_cli1, cod_cli1temp) VALUES (:idProducto, :idCli1, :codCli1Temp) " +
            "ON DUPLICATE KEY UPDATE id_producto = id_producto", nativeQuery = true)
    void insertProductoCli1(@Param("idProducto") Long idProducto, @Param("idCli1") String idCli1, @Param("codCli1Temp") Boolean codCli1Temp);


}
