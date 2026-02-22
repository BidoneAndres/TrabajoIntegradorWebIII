package ar.iua.edu.trabajointegrador.model.deserializers;

import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CAMION_PATENTE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CHOFER_DOCUMENTO_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CLIENTE_RAZON_SOCIAL_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_FECHA_ESTIMADA_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_NUMERO_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_PRESET_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.PRODUCTO_NOMBRE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.ORDEN_CODIGO_EXTERNO_ATTRIBUTES;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.TimeZone;



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
import java.text.ParseException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IChoferBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;
import ar.iua.edu.trabajointegrador.util.JsonUtils;
import lombok.SneakyThrows;

public class OrdenJsonDeserializer extends StdDeserializer<Orden> {

	private static final long serialVersionUID = -3881285352118964728L;
	private static final Logger log = LoggerFactory.getLogger(OrdenJsonDeserializer.class);

	protected OrdenJsonDeserializer(Class<?> vc) {
		super(vc);
	}

	private IChoferBusiness choferBusiness;

	private ICamionBusiness camionBusiness;

	private IClienteBusiness clienteBusiness;

	private IProductoBusiness productoBusiness;

	public OrdenJsonDeserializer(Class<?> vc, IChoferBusiness choferBusiness, ICamionBusiness camionBusiness,
			IClienteBusiness clienteBusiness, IProductoBusiness productoBusiness) {
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
		String codExt = "";
		Date fecha_estimada;
		int preset;
		int numeroOrden;

		SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    	formato.setTimeZone(TimeZone.getTimeZone("UTC"));	

		try {
			try {
				numeroOrden = JsonUtils.getInt(node, ORDEN_NUMERO_ATTRIBUTES, 0);
				if (numeroOrden == 0) {
					throw new BadRequestException("Número de orden inexistente o inválido");
				}
				String fechaEstimadaStr = JsonUtils.getString(node, ORDEN_FECHA_ESTIMADA_ATTRIBUTES, null);
				if (fechaEstimadaStr == null || fechaEstimadaStr.trim().isEmpty()) {
                    throw new BadRequestException("Fecha estimada inexistente o vacía");
                }
                try {
                    fecha_estimada = formato.parse(fechaEstimadaStr);
                } catch (ParseException e) {
                    // Si el parseo falla, el formato de entrada es incorrecto
                    log.error("Formato de fecha estimado inválido: " + fechaEstimadaStr, e);
                    throw new BadRequestException("El formato de la fecha estimada debe ser yyyy-MM-dd HH:mm");
                }
				preset = (int) JsonUtils.getValue(node, ORDEN_PRESET_ATTRIBUTES, 0);
				if (preset < 0) {
					throw new BadRequestException("Preset falta o no es válido");
				}
				codExt = JsonUtils.getString(node, ORDEN_CODIGO_EXTERNO_ATTRIBUTES, codExt);

			} catch (BadRequestException e) {
				log.error(e.getMessage(), e);
				throw new BusinessException(e.getMessage());
			}

			Chofer chofer = JsonUtils.getChofer(node, CHOFER_DOCUMENTO_ATTRIBUTES, choferBusiness);
			Camion camion = JsonUtils.getCamion(node, CAMION_PATENTE_ATTRIBUTES, camionBusiness);
			Cliente cliente = JsonUtils.getCliente(node, CLIENTE_RAZON_SOCIAL_ATTRIBUTES, clienteBusiness);
			Producto producto = JsonUtils.getProducto(node, PRODUCTO_NOMBRE_ATTRIBUTES, productoBusiness);

			r.setNumeroOrden(numeroOrden);
			;
			r.setFechaEstimada(fecha_estimada);
			r.setPreset(preset);
			r.setFechaRecepcionOrden(LocalDateTime.now());
			r.setCodExt(codExt);

			if (producto != null && cliente != null && camion != null && chofer != null) {
				r.setCamion(camion);
				r.setChofer(chofer);
				r.setCliente(cliente);
				r.setProducto(producto);
			}
			return r;
		} catch (FoundException e) {
			log.error(e.getMessage(), e);
			throw new FoundException(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException(e.getMessage());
		}
	}
}
