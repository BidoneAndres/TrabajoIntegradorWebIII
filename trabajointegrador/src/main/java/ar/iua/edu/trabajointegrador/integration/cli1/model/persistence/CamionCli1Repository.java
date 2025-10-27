package ar.iua.edu.trabajointegrador.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ar.iua.edu.trabajointegrador.integration.cli1.model.CamionCli1;

public interface CamionCli1Repository extends JpaRepository<CamionCli1 , Long>{

		Optional<CamionCli1> findOneByIdCli1(String idCli1);
		
		Optional<CamionCli1> findOneByPatenteAndIdCli1NotAndCodCli1Temp(String patente, String idCli1, boolean codCli1Temp);
		
		Optional<CamionCli1> findByPatente(String patente);
		
		@Modifying
	    @Query(value = "INSERT INTO cli1_camiones (id_camion, id_cli1, cod_cli1temp) VALUES (:idCamion, :idCli1, false)", nativeQuery = true)
	    void insertCamionCli1(@Param("idCamion") Long idCamion, @Param("idCli1") String idCli1);
}
