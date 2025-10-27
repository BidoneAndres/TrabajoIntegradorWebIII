package ar.iua.edu.trabajointegrador.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.iua.edu.trabajointegrador.integration.cli1.model.SisternaCli1;

public interface SisternaCli1Repository extends JpaRepository<SisternaCli1, Long>{

	Optional<SisternaCli1> findOneByIdCli1(String idCli1);
	
	 @Modifying
	    @Query(value = "INSERT INTO cli1_sisternas (id_sisterna, id_cli1, cod_cli1temp) VALUES (:idSisterna, :idCli1, false)", nativeQuery = true)
	    void insertTankerCli1(@Param("idSisterna") Long idSisterna, @Param("idCli1") String idCli1);
}
