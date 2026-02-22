package ar.iua.edu.trabajointegrador.model.persistence;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ar.iua.edu.trabajointegrador.model.Alarma;

import ar.iua.edu.trabajointegrador.model.Orden;

import java.util.List;

public interface AlarmaRepository extends JpaRepository<Alarma, Long>{
    
    Optional<Alarma> findByEstadoAndOrdenId(Alarma.alarmaEstado estadoAlarma, Long idOrden);

    Optional<List<Alarma>> findByEstadoAndOrdenEstado(Alarma.alarmaEstado estadoAlarma, Orden.Estado estadoOrden);

    Optional<Alarma> findAllByOrden(Orden orden);

    Optional<Alarma> findByOrdenIdAndEstado(Long idOrden, Alarma.alarmaEstado estadoAlarma);

    Optional<Page<Alarma>> findAllByOrden(Orden orden, Pageable pageable);

}
