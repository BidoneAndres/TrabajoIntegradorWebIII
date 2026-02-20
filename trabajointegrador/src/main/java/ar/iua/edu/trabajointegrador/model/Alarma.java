package ar.iua.edu.trabajointegrador.model;

import ar.iua.edu.trabajointegrador.auth.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Schema(hidden = true)
@Entity
@Table(name = "alarms")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Alarma {
    public enum alarmaEstado{
        PENDIENTE_REVISION,
        ACEPTADA,
        PROBLEMAS,
    }

    @Schema(hidden = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(hidden = true)
    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;

    @Enumerated(EnumType.STRING)
    @Column()
    private alarmaEstado estado;
    
    @Column(nullable = false)
    private Date tiempo;

    @Column(nullable = false)
    private float temperatura;

    @ManyToOne
    @JoinColumn(name = "idUser", nullable = false)
    private User user;

    private String descripcion;
}
