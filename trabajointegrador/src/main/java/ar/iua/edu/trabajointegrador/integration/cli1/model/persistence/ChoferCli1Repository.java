package ar.iua.edu.trabajointegrador.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.iua.edu.trabajointegrador.integration.cli1.model.ChoferCli1;

public interface ChoferCli1Repository extends JpaRepository<ChoferCli1, Long>{
	
	Optional<ChoferCli1> findOneByIdCli1(String idCli1);
	
	Optional<ChoferCli1> findByDocumentoAndIdCli1NotAndCodCli1Temp(String documento, String idCli1, boolean codCli1Temp);
	
	Optional<ChoferCli1> findByDocumento(String documento);
	
/*	@Modifying
    @Query(value = "INSERT INTO cli1_choferes (id_chofer, id_cli1, cod_cli1temp) VALUES (:idChofer, :idCli1, false)", nativeQuery = true)
    void insertChoferCli1(@Param("idChofer") Long idChofer, @Param("idCli1") String idCli1);*/
}
