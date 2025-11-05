package ar.iua.edu.trabajointegrador.integration.cli1.model;

import ar.iua.edu.trabajointegrador.model.Cliente;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "cli_clientes")
@PrimaryKeyJoinColumn(name = "id_cliente")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClienteCli1 extends Cliente{
	
	@Column(nullable = false, unique = true)
	private String id_cli1;
	
	private boolean codCli1Temp = false;

}
