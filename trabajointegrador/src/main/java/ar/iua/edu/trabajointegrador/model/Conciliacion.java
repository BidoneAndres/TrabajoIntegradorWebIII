package ar.iua.edu.trabajointegrador.model;

import java.time.LocalDateTime;
import java.util.Date;

import ar.iua.edu.trabajointegrador.model.Orden.Estado;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "conciliaciones")
public class Conciliacion {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	private double productoCargado; //ultimo valor de masa acumulada
	
	private float pesoInicial; //tara
	
	private float pesoFinal;
	
	private float netoPorBalanza; //pesaje final - pesaje inicial
	
	private double diferenciaBalanzaCaudalimetro; //Neto por balanza - producto cargado
	
	//-- promedios --
	private double promedioTemperatura;
	
	private double promedioDensidad;
	
	private double promedioCaudal;
	

	@ManyToOne(fetch = FetchType.LAZY)  // ->importante, sino me va a traer en cada coNSULTA todas las veces la otden entera
	@JoinColumn(name="orden_id",nullable = false)//da mas detalles, clave foranea, 
	private Orden orden;
	

}
