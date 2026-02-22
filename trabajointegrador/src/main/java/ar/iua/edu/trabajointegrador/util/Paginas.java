package ar.iua.edu.trabajointegrador.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Pageable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Paginas {

    private Pageable pageable;
    private int totalPages;
    private long totalElements;
    private int number;
    private int size;
    private int numberOfElements;
    
}
