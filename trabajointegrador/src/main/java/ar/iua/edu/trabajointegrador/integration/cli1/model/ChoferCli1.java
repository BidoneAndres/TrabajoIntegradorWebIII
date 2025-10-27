package ar.iua.edu.trabajointegrador.integration.cli1.model;

import ar.iua.edu.trabajointegrador.model.Chofer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_choferes")
@PrimaryKeyJoinColumn(name = "id_chofer")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChoferCli1 extends Chofer{
	@Column(nullable = false, unique = true)
	private String idCli1;
	
	private boolean codCli1Temp=false;
}
