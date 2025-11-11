package ar.iua.edu.trabajointegrador.util;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CAMION_NODE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CHOFER_NODE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.CLIENTE_NODE_ATTRIBUTES;
import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.PRODUCTO_NODE_ATTRIBUTES;

//import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;
import org.apache.coyote.BadRequestException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import ar.iua.edu.trabajointegrador.model.Camion;
import ar.iua.edu.trabajointegrador.model.Chofer;
import ar.iua.edu.trabajointegrador.model.Cliente;
import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.business.exceptions.BusinessException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.FoundException;
import ar.iua.edu.trabajointegrador.model.business.exceptions.NotFoundException;
import ar.iua.edu.trabajointegrador.model.business.interfaces.ICamionBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IChoferBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IClienteBusiness;
import ar.iua.edu.trabajointegrador.model.business.interfaces.IProductoBusiness;



/*Esta es una clase utilitaria, la cual tiene como objetivo simplificarnos la serealizacion de deserealizacion*/

public class JsonUtils {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String formatoFecha) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		
		
		//Basicamente definimos el formato de la fecha, en el caso de no estar definido utilizaremos el que creamos por defecto
		if(formatoFecha != null)
			defaultFormat = formatoFecha; //Basicamente definimos el formato de la fecha, en el caso de no estar definido utilizaremos el que creamos por defecto
		
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule md = new SimpleModule();
		
		
		
		if(ser != null)
			md.addSerializer(clazz, ser);
		
		mapper.setDateFormat(df);
		mapper.registerModule(md);
		return mapper;
		
		
	}
	
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer des, String formatoFecha) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";
		
		
		//Basicamente definimos el formato de la fecha, en el caso de no estar definido utilizaremos el que creamos por defecto
		if(formatoFecha != null)
			defaultFormat = formatoFecha; //Basicamente definimos el formato de la fecha, en el caso de no estar definido utilizaremos el que creamos por defecto
		
		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule md = new SimpleModule();
		
		
		
		if(des != null)
			md.addDeserializer(clazz, des);
		
		mapper.setDateFormat(df);
		mapper.registerModule(md);
		return mapper;
		
		
	}
	
	/*Estos metodos son todos iguales
	 * A partir de json, representado por JsonNode, nos permite cambiar un jeson por alguno de los siguientes tipos de datos 
	 * Buscamos los atributos definidos en el attrs y es el valor retornado, si no se devuelve el default*/
	
	public static String getString(JsonNode node, String[] attrs, String defaultValue) {
		String r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				r = node.get(attr).asText();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static double getDouble(JsonNode node, String[] attrs, double defaultValue) {
		Double r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isDouble()) {
				r = node.get(attr).asDouble();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}
	
	public static int getInt(JsonNode node, String[] attrs, int defaultValue) {
		Integer r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isInt()) {
				r = node.get(attr).asInt();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}

	public static boolean getBoolean(JsonNode node, String[] attrs, boolean defaultValue) {
		Boolean r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null && node.get(attr).isBoolean()) {
				r = node.get(attr).asBoolean();
				break;
			}
		}
		if (r == null)
			r = defaultValue;
		return r;
	}
	
	public static float getValue(JsonNode node, String[] attrs, float defaultValue) {
        Float r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                // Intentamos manejar el valor como float sin depender de si el tipo es específicamente un float
                if (node.get(attr).isFloat() || node.get(attr).isDouble() || node.get(attr).isInt()) {
                    r = node.get(attr).floatValue(); // Convertimos cualquiera de estos tipos a float
                    break;
                }
            }
        }
        if (r == null)
            r = defaultValue;
        return r;
    }
	
	public static JsonNode getJsonNode(JsonNode node, String[] attrs) {
        JsonNode r = null;
        for (String attr : attrs) {
            if (node.get(attr) != null) {
                r = node.get(attr);
                break;
            }
        }
        return r;
    }
	
	public static Chofer getChofer(JsonNode node, String[] attrs, IChoferBusiness ChoferBusiness) throws FoundException, BusinessException, NotFoundException, BadRequestException {
        JsonNode choferNode = getJsonNode(node, CHOFER_NODE_ATTRIBUTES); // Buscar el nodo padre "driver"
        if (choferNode != null) {
            String choferDocumento = null;
            // Recorremos los atributos dentro del nodo "chofer"
            for (String attr : attrs) {
                if (choferNode.get(attr) != null) {
                	choferDocumento = choferNode.get(attr).asText();
                    break;
                }
            }
            if (choferDocumento != null) {
                // Primero intentamos cargar el chofer existente por documento
                try {
                    return ChoferBusiness.load(choferDocumento);
                } catch (NotFoundException e) {
                    // Si no existe, creamos uno nuevo con los datos recibidos
                    Chofer chofer = BuildEntityUtils.buildChofer(choferNode);
                    return ChoferBusiness.addChofer(chofer);
                }
            } else {
                throw new BadRequestException("El campo documento del conductor no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo conductor no se recibió correctamente");
        }
    }

    public static Camion getCamion(JsonNode node, String[] attrs, ICamionBusiness camionBusiness) throws FoundException, BusinessException, NotFoundException, BadRequestException {
        JsonNode camionNode = getJsonNode(node, CAMION_NODE_ATTRIBUTES); // Buscar el nodo padre "truck"
        if (camionNode != null) {
            String patente = getString(camionNode, attrs, null);  // Obtener placa del camión desde los atributos
            if (patente != null) {
                JsonNode sisternaNode = camionNode.get("sisternas");
                // Intentamos usar el camion existente si ya está en la BD
                try {
                    return camionBusiness.load(patente);
                } catch (NotFoundException e) {
                    return camionBusiness.addCamion(BuildEntityUtils.buildCamion(camionNode, sisternaNode));
                }
            } else {
                throw new BadRequestException("El campo placa del camión no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo camión no se recibió correctamente");
        }
    }

    public static Cliente getCliente(JsonNode node, String[] attrs, IClienteBusiness clienteBusiness) throws FoundException, BusinessException, NotFoundException, BadRequestException {
        JsonNode clienteNode = getJsonNode(node, CLIENTE_NODE_ATTRIBUTES); // Buscar el nodo padre "customer"
        if (clienteNode != null) {
            String clienteName = null;
            // Recorremos los atributos dentro del nodo "customer"
            for (String attr : attrs) {
                if (clienteNode.get(attr) != null) {
                    clienteName = clienteNode.get(attr).asText();
                    break;
                }
            }
            if (clienteName != null) {
                // Intentamos cargar cliente existente por razon social
                try {
                    return clienteBusiness.load(clienteName);
                } catch (NotFoundException e) {
                    Cliente cliente = BuildEntityUtils.buildCliente(clienteNode);
                    return clienteBusiness.addCliente(cliente);
                }
            } else {
                throw new BadRequestException("El campo cliente no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo cliente no se recibió correctamente");
        }
        //return null;
    }

    public static Producto getProducto(JsonNode node, String[] attrs, IProductoBusiness productoBusiness) throws BusinessException, NotFoundException, FoundException, BadRequestException {
        JsonNode productoNode = getJsonNode(node, PRODUCTO_NODE_ATTRIBUTES); // Buscar el nodo padre "product"
        if (productoNode != null) {
            String productoName= null;
            // Recorremos los atributos dentro del nodo "producto"
            for (String attr : attrs) {
                if (productoNode.get(attr) != null) {
                    productoName = productoNode.get(attr).asText();
                    break;
                }
            }
            if (productoName != null) {
                // Intentamos cargar producto existente por nombre
                try {
                    return productoBusiness.load(productoName);
                } catch (NotFoundException e) {
                    return productoBusiness.addProducto(BuildEntityUtils.buildProducto(productoNode));
                }
            } else {
                throw new BadRequestException("El campo producto no se recibió correctamente");
            }
        } else {
            throw new BadRequestException("El nodo producto no se recibió correctamente");
        }
    }
    
    public static Date getFecha(JsonNode node, String[] attrs, String defaultValue) {
        Date parsedDate = null;

        SimpleDateFormat[] formats = {
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault()), // Con zona horaria
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()),// Sin zona horaria
                new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())   
        };

        for (String attr : attrs) {
            if (node.get(attr) != null) {
                String dateStr = node.get(attr).asText();
                for (SimpleDateFormat format : formats) {
                    try {
                        parsedDate = format.parse(dateStr);
                        if (parsedDate != null) {
                            return parsedDate;
                        }
                    } catch (ParseException e) {
                    }
                }
            }
        }

        if (defaultValue != null) {
            for (SimpleDateFormat format : formats) {
                try {
                    parsedDate = format.parse(defaultValue);
                    if (parsedDate != null) {
                        return parsedDate; 
                    }
                } catch (ParseException e) {
                   
                }
            }
        }
        return parsedDate;
    }
}

