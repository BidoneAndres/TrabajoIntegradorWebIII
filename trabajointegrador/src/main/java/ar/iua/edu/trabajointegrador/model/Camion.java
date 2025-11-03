package ar.iua.edu.trabajointegrador.model;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name="camiones")
@Inheritance(strategy = InheritanceType.JOINED)
public class Camion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(length = 50, unique = true, nullable = false)
	private String patente;
	
	@OneToMany(mappedBy = "camion",cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private Set<Sisterna> sisternas = new HashSet<>();
	
	private String descripcion;
}
