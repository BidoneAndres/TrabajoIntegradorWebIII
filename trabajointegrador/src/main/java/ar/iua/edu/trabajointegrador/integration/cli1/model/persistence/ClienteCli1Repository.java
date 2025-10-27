package ar.iua.edu.trabajointegrador.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.iua.edu.trabajointegrador.integration.cli1.model.ClienteCli1;

public interface ClienteCli1Repository extends JpaRepository<ClienteCli1, Long>{
	
	Optional<ClienteCli1> findOneByIdCli1(String idCli1);
	
	Optional<ClienteCli1> findByRazonSocialAndIdCli1NotAndCodCli1Temp(String razonSocial, String idCli1, boolean codCli1Temp);
	
	Optional<ClienteCli1> findByRazonSocial(String razonSocial);
	
	@Modifying
    @Query(value = "INSERT INTO cli1_clientes (id_cliente, id_cli1, cod_cli1temp) VALUES (:idCliente, :idCli1, false)", nativeQuery = true)
    void insertClienteCli1(@Param("idCliente") Long idCliente, @Param("idCli1") String idCli1);
}
