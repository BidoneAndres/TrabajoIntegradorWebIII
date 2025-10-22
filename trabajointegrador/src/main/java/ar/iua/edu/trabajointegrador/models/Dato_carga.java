package ar.iua.edu.trabajointegrador.models;

import java.time.Instant;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "datos_carga")
@EntityListeners(AuditingEntityListener.class) // para las timestamps
public class Dato_carga {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	//asociamos al numero de orden
	
	@ManyToOne(fetch = FetchType.LAZY)  // ->importante, sino me va a traer en cada coNSULTA todas las veces la otden entera
	@JoinColumn(name="clave_activacion", nullable = false)//da mas detalles, clave foranea, 
	private Orden clave_orden;
	
	private double ultima_masa_acumulada;
	private double ultima_densidad_producto;
	private int ultima_temperatura;
	private double ultimo_caudal;
	
	// --- TIMESTAMPS AUTOMÁTICOS ---

    @CreatedDate //  Anotación para la fecha de creación
    @Column(nullable = false, updatable = false) // No se puede actualizar
    private Instant timestamp;
}
