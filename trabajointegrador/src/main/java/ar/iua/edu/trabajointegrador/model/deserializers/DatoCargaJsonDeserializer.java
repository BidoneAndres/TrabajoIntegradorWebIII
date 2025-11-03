package ar.iua.edu.trabajointegrador.model.deserializers;

import java.io.IOException;
import java.util.Optional;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.iua.edu.trabajointegrador.model.DatoCarga;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IOrdenBusiness;
import ar.iua.edu.trabajointegrador.util.JsonUtiles;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatoCargaJsonDeserializer extends StdDeserializer<DatoCarga> {

	protected DatoCargaJsonDeserializer(Class<?> vc) {
		super(vc);
		// TODO Auto-generated constructor stub
	}
	
	private IOrdenBusiness ordenBusiness;
	public DatoCargaJsonDeserializer(Class<?> vc, IOrdenBusiness ordenBusiness) {
		super(vc);
		this.ordenBusiness = ordenBusiness;
	}

	@Override
	public DatoCarga deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JacksonException {
		// TODO Auto-generated method stub
		JsonNode node = jp.getCodec().readTree(jp);
		DatoCarga datoCarga = new DatoCarga();
		
		Double masa = JsonUtiles.getDouble(node, "masa,ultima_masa_acumulada,ultimaMasaAcumulada".split(","), 0);

		Double caudal = JsonUtiles.getDouble(node, "caudal,ultimo_caudal,ultimoCaudal".split(","), 0);

		Double densidad = JsonUtiles.getDouble(node, "densidad,ultima_densidad_acumulada,ultimaDensidadProducto".split(","), 0);

		Integer temperatura =  JsonUtils.getInt(node, "temperatura,ultima_temperatura,ultimaTemperatura".split(","), 0);
		
		Integer claveActivacion = JsonUtils.getInt(node, "clave_activacion,claveActivacion".split(","), 0); 
		
		datoCarga.setUltimaDensidadProducto(densidad);
		datoCarga.setUltimaTemperatura(temperatura);
		datoCarga.setUltimaMasaAcumulada(masa);
		datoCarga.setUltimoCaudal(caudal);
		
		try {
			Orden ordenEncontrada = ordenBusiness.loadByClaveActivacion(claveActivacion);
			
			datoCarga.setOrden(ordenEncontrada);
		}
		catch(NotFoundException | BusinessException e) {
			
		}
		
		return datoCarga;
	}

}
