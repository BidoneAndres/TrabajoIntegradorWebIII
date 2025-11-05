package ar.iua.edu.trabajointegrador.model.deserializers;

import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CAMION_PATENTE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CHOFER_DOCUMENTO_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_FECHA_ESTIMADA_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_NUMERO_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_PESO_INICIAL_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.PRODUCTO_NOMBRE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CLIENTE_RAZON_SOCIAL_ATTRIBUTES;

import java.util.Date;

import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import ar.iua.edu.trabajointegrador.model.Camion;
import ar.iua.edu.trabajointegrador.model.Chofer;
import ar.iua.edu.trabajointegrador.model.Cliente;
import ar.iua.edu.trabajointegrador.model.Orden;
import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IChoferBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import lombok.SneakyThrows;

public class OrdenJsonDeserializer extends StdDeserializer<Orden>{
	
	private static final long serialVersionUID = -3881285352118964728L;
	private static final Logger log = LoggerFactory.getLogger(OrdenJsonDeserializer.class);
	
	protected OrdenJsonDeserializer(Class<?> vc) {
		super(vc);
	}
	
	private IChoferBusiness choferBusiness;
	
	private ICamionBusiness camionBusiness;
	
	private IClienteBusiness clienteBusiness;
	
	private IProductoBusiness productoBusiness;
	
	public OrdenJsonDeserializer(Class<?> vc, IChoferBusiness choferBusiness, ICamionBusiness camionBusiness, IClienteBusiness clienteBusiness, IProductoBusiness productoBusiness) {
		 super(vc);
	        this.choferBusiness = choferBusiness;
	        this.camionBusiness = camionBusiness;
	        this.clienteBusiness = clienteBusiness;
	        this.productoBusiness = productoBusiness;
	}
	@SneakyThrows
    @Override
    public Orden deserialize(JsonParser jp, DeserializationContext context) {
	Orden r = new Orden();
	JsonNode node = jp.getCodec().readTree(jp);
	String codExt;
	Date fecha_estimada;
	int preset;
	int numeroOrden;
	try {
			try {
				numeroOrden = JsonUtils.getInt(node, ORDEN_NUMERO_ATTRIBUTES, 0);
				if (numeroOrden == 0) {
                	throw new BadRequestException("Número de orden inexistente o inválido");
            	}
				fecha_estimada = JsonUtils.getFecha(node, ORDEN_FECHA_ESTIMADA_ATTRIBUTES, String.valueOf(new Date()));
				if (fecha_estimada == null) {
                	throw new BadRequestException("Fecha estimada inexistente");
            	}
				preset = (int) JsonUtils.getValue(node, ORDEN_PESO_INICIAL_ATTRIBUTES, 0);
				if (preset < 0) {
                	throw new BadRequestException("Preset falta o no es válido");
            	}
			} catch(BadRequestException e) {
			 	log.error(e.getMessage(), e);
	        	 throw new BusinessException(e.getMessage());
			}
		
			Chofer chofer = JsonUtils.getChofer(node, CHOFER_DOCUMENTO_ATTRIBUTES, choferBusiness);
			Camion camion = JsonUtils.getCamion(node, CAMION_PATENTE_ATTRIBUTES, camionBusiness);
			Cliente cliente = JsonUtils.getCliente(node, CLIENTE_RAZON_SOCIAL_ATTRIBUTES, clienteBusiness);
			Producto producto = JsonUtils.getProducto(node, PRODUCTO_NOMBRE_ATTRIBUTES, productoBusiness);
		
			r.setNumeroOrden(numeroOrden);;
			r.setFechaEstimada(fecha_estimada);
			r.setPreset(preset);
			r.setFechaRecepcionOrden(new Date(System.currentTimeMillis()));
		
			if (producto != null && cliente != null && camion != null && chofer != null) {
				r.setCamion(camion);
				r.setChofer(chofer);
				r.setCliente(cliente);
				r.setProducto(producto);
			}
			r.setEstado(Orden.Estado.ESTADO_1_PENDIENTE_PESAJE_INICIAL);
			return r;
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}
}
