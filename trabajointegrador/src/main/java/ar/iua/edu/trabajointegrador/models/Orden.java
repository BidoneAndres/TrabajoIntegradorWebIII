package ar.iua.edu.trabajointegrador.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Getter
@Setter
@Entity
@Table(name="ordenes")
public class Orden {
	
	public enum Estado{
		RECIBIDA,
		REGISTRADA_PESAJE_INICIAL,
		CERRADA,
		REGISTRADA_PESAJE_FINAL,
		CANCELADA		
	}
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Estado estado;
	
	@Column(length = 100, nullable = false)
	private int preset;
	
	private float peso_inicial;
	
	private float peso_final;
	
	@ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
	private Cliente cliente;
	
	@ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
	private Producto producto;
	
	@ManyToOne
    @JoinColumn(name = "id_camion", nullable = false)
	private Camion camion;
	
	@Column(length = 10)
    private String claveActivacion;
}
