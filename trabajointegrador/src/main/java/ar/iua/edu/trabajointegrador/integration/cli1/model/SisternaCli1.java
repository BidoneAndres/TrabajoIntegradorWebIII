package ar.iua.edu.trabajointegrador.integration.cli1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cli1_sisternas")
@PrimaryKeyJoinColumn(name = "id_sisterna")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SisternaCli1 {
	@Column(nullable = false, unique = true)
    private String idCli1;

    private boolean codCli1Temp=false;
}
