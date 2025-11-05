package ar.iua.edu.trabajointegrador.model;

import java.util.Date;

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
@Table(name="ordenes")
@Inheritance(strategy = InheritanceType.JOINED)
public class Orden {
	
	public enum Estado{
		ESTADO_1_PENDIENTE_PESAJE_INICIAL,
		ESTADO_2_PESAJE_INICIAL_REGISTRADO,
		ESTADO_2_EN_PROCESO_DE_CARGA,
		ESTADO_3_CERRADA_PARA_CARGA,
		ESTADO_4_FINALIZADA,
				
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(nullable = false, unique = true)
	private String codExt;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Estado estado;
	
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
	
	//es de 4 digitos la clave de activacion
	@Column(length = 10)
    private Integer claveActivacion;
	
	@Column(unique = true)
	private Integer numeroOrden;
	
	private float pesoInicial;
	
	private float pesoFinal;
	
	private Date fechaEstimada;
	
	private Date fechaRecepcionOrden;

	private Date fechaPesajeInicial;
}
