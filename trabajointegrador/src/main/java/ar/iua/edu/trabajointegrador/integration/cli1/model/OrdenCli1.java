package ar.iua.edu.trabajointegrador.integration.cli1.model;

import ar.iua.edu.trabajointegrador.model.Orden;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_ordenes")
@PrimaryKeyJoinColumn(name = "id_orden")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrdenCli1 extends Orden{
	@Column(nullable = false, unique = true)
    private String ordenNumberCli1;

    private boolean codCli1Temp=false;
}
