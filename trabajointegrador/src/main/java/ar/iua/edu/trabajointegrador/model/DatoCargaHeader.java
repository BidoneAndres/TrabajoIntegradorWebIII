package ar.iua.edu.trabajointegrador.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonFormat;

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
@Table(name = "datos_carga_header")
@EntityListeners(AuditingEntityListener.class) // para las timestamps
public class DatoCargaHeader  {
	@Schema(hidden = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	//asociamos al numero de orden
	
	@ManyToOne(fetch = FetchType.LAZY)  // ->importante, sino me va a traer en cada coNSULTA todas las veces la otden entera
	@JoinColumn(name="orden_id",nullable = false)//da mas detalles, clave foranea, 
	private Orden orden;
	
	private double ultimaMasaAcumulada;
	private double ultimaDensidadProducto;
	private int ultimaTemperatura;
	private double ultimoCaudal;
	
	// --- TIMESTAMPS AUTOMÁTICOS ---

    @UpdateTimestamp //  Anotación para la fecha de creación
    @Column(nullable = false) 
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")

    private LocalDateTime timestamp;
}