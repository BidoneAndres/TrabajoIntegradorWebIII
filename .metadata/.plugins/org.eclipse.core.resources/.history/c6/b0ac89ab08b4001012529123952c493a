package ar.iua.edu.trabajointegrador.integration.cli1.model.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.integration.cli1.model.OrdenCli1;
import ar.iua.edu.trabajointegrador.model.Orden;

public interface OrdenCli1Repository extends JpaRepository<OrdenCli1, Long>{

	 Optional<OrdenCli1> findOneByOrdenNumberCli1(String OrdenNumberCli1);
	 
	 Optional<OrdenCli1> findByCamion_idAndEstado(Long idTruck, Orden.Estado estado);
}
