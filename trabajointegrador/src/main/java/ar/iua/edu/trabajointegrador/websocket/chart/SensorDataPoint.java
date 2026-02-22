package ar.iua.edu.trabajointegrador.websocket.chart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@AllArgsConstructor
public class SensorDataPoint {
	    private long id; 
	    private double temperatura;
	    private double caudal;
	    private double masaAcumulada;
}

