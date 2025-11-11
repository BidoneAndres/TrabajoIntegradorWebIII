package ar.iua.edu.trabajointegrador.model;

import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ordenes")
@Inheritance(strategy = InheritanceType.JOINED)
public class Orden {

	public enum Estado {
		ESTADO_1_PENDIENTE_PESAJE_INICIAL, ESTADO_2_PESAJE_INICIAL_REGISTRADO, ESTADO_2_EN_PROCESO_DE_CARGA,
		ESTADO_3_CERRADA_PARA_CARGA, ESTADO_4_FINALIZADA,

	}
	@Schema(hidden = true)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	private String codExt;
	
	@Schema(hidden = true)
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Estado estado = Estado.ESTADO_1_PENDIENTE_PESAJE_INICIAL;

	@Column(length = 100, nullable = false)
	private int preset;

	@ManyToOne
	@JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "id_producto", nullable = false)
	private Producto producto;

	@ManyToOne
	@JoinColumn(name = "id_camion", nullable = false)
	private Camion camion;

	@ManyToOne
	@JoinColumn(name = "id_chofer", nullable = false)
	private Chofer chofer;

	// es de 4 digitos la clave de activacion
	@Column(length = 10)
	private Integer claveActivacion;

	@Column(unique = true)
	private Integer numeroOrden;

	private float pesoInicial;

	private float pesoFinal;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private Date fechaEstimada;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime fechaRecepcionOrden;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime fechaPesajeInicial;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime fechaInicioCarga;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime fechaFinCarga;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm")
	private LocalDateTime fechaPesajeFinal;

	public void setEstado(Estado nuevoEstado) {
		 if (this.estado == Estado.ESTADO_4_FINALIZADA) {
		        throw new IllegalStateException("No se puede cambiar el estado de una orden finalizada, NUMERO DE ORDEN: " + this.numeroOrden);
		    }

		    if (this.estado == Estado.ESTADO_1_PENDIENTE_PESAJE_INICIAL
		            && nuevoEstado == Estado.ESTADO_2_PESAJE_INICIAL_REGISTRADO) {
		        this.estado = nuevoEstado;
		        this.fechaPesajeInicial = LocalDateTime.now(); // o convertir si fechaPesajeInicial es Date
		        return;
		    }

		    if (this.estado == Estado.ESTADO_2_PESAJE_INICIAL_REGISTRADO
		            && nuevoEstado == Estado.ESTADO_2_EN_PROCESO_DE_CARGA) {
		        this.estado = nuevoEstado;
		        this.fechaInicioCarga = LocalDateTime.now();
		        return;
		    }

		    if (this.estado == Estado.ESTADO_2_EN_PROCESO_DE_CARGA
		            && nuevoEstado == Estado.ESTADO_3_CERRADA_PARA_CARGA) {
		        this.estado = nuevoEstado;
		        this.fechaFinCarga = LocalDateTime.now();
		        return;
		    }

		    if (this.estado == Estado.ESTADO_3_CERRADA_PARA_CARGA
		            && nuevoEstado == Estado.ESTADO_4_FINALIZADA) {
		        this.estado = nuevoEstado;
		        this.fechaPesajeFinal = LocalDateTime.now();
		        return;
		    }

		    throw new IllegalStateException("Transicion de estados invalida: " + this.estado + " -> " + nuevoEstado);
}}
