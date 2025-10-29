package ar.iua.edu.trabajointegrador.models.persistence;


import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "usuarios_externos")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class UsuarioExterno {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;
}
