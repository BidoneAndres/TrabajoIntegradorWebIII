package ar.iua.edu.trabajointegrador.model.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import ar.iua.edu.trabajointegrador.model.Detalle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import ar.iua.edu.trabajointegrador.model.Orden;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle, Long>{

    Optional<List<Detalle>> findByOrdenId(long id);

    Optional<Page<Detalle>> findAllByOrden(Orden orden, Pageable pageable);
    
    @Query("SELECT AVG(d.temperatura) FROM Detalle d WHERE d.orden.id = :ordenId")
    Double findAverageTemperaturaByOrdenId(Long ordenId);

    @Query("SELECT AVG(d.densidad) FROM Detalle d WHERE d.orden.id = :ordenId")
    Double findAverageDensidadByOrdenId(Long ordenId);

    @Query("SELECT AVG(d.caudal) FROM Detalle d WHERE d.orden.id = :ordenId")
    Double findAverageCaudalByOrdenId(Long ordenId);


}
