package ar.iua.edu.trabajointegrador.model.persistence;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ar.iua.edu.trabajointegrador.model.Alarma;

import ar.iua.edu.trabajointegrador.model.Orden;

import java.util.List;

public interface AlarmaRepository extends JpaRepository<Alarma, Long>{
    
    Optional<Alarma> findByEstadoAndOrdenId(Alarma.alarmaEstado estadoAlarma,Long idOrden);

    Optional<List<Alarma>> findByAlarmaEstadoAndEstado(Alarma.alarmaEstado estadoAlarma, Orden.Estado estadoOrden);

    Optional<Alarma> findAllByOrden(Orden orden);

    Optional<Alarma> findByOrdenIdAndEstado(Long idOrden, Alarma.alarmaEstado estadoAlarma);

}
