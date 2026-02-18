package ar.iua.edu.trabajointegrador.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;

@Schema(hidden = true)
@Entity
@Table(name = "detalles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Detalle {
        @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(hidden = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_orden", nullable = false)
    @JsonIgnoreProperties("detalles")
    private Orden orden;

    @Column(nullable = false)
    private Date fecha;

    @Column(nullable = false)
    private float masaAcumulada;

    @Column(nullable = false)
    private float densidad;

    @Column(nullable = false)
    private float temperatura;

    @Column(nullable = false)
    private float caudal;
}
