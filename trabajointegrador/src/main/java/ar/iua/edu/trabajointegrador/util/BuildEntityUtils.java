package ar.iua.edu.trabajointegrador.util;

import static ar.iua.edu.trabajointegrador.util.JsonAttributeConstants.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.coyote.BadRequestException;

import com.fasterxml.jackson.databind.JsonNode;

import ar.iua.edu.trabajointegrador.model.Camion;
import ar.iua.edu.trabajointegrador.model.Chofer;
import ar.iua.edu.trabajointegrador.model.Cliente;
import ar.iua.edu.trabajointegrador.model.Producto;
import ar.iua.edu.trabajointegrador.model.Sisterna;

public class BuildEntityUtils {
	public static Chofer buildChofer(JsonNode choferNode) throws BadRequestException {
        Chofer newChofer = new Chofer();

        String nombre = JsonUtils.getString(choferNode, CHOFER_NOMBRE_ATTRIBUTES, "");
        if (nombre != null && !nombre.isEmpty()) {
            newChofer.setNombre(nombre);
        } else {
            throw new BadRequestException("El nombre del chofer no puede ser nulo o vacío");
        }

        String apellido = JsonUtils.getString(choferNode, CHOFER_APELLIDO_ATTRIBUTES, "");
        if (apellido != null && !apellido.isEmpty()) {
            newChofer.setApellido(apellido);
        } else {
            throw new BadRequestException("El apellido del chofer no puede ser nulo o vacío");
        }

        String documento = JsonUtils.getString(choferNode, CHOFER_DOCUMENTO_ATTRIBUTES, "");
        if (documento != null && !documento.isEmpty()) {
            newChofer.setDocumento(documento);
        } else {
            throw new BadRequestException("El documento del conductor no puede ser nulo o vacío");
        }

        return newChofer;
    }


    public static Camion buildCamion(JsonNode camionNode, JsonNode sisternasNode) throws BadRequestException {
        Camion newCamion = new Camion();

        String patente = JsonUtils.getString(camionNode, CAMION_PATENTE_ATTRIBUTES, "");
        if (patente != null && !patente.isEmpty()) {
            newCamion.setPatente(patente);
        } else {
            throw new BadRequestException("La placa del camión no puede ser nula o vacía");
        }

        String descripcion = JsonUtils.getString(camionNode, CAMION_DESCRIPCION_ATTRIBUTES, "");
        if (descripcion != null && !descripcion.isEmpty()) {
            newCamion.setDescripcion(descripcion);
        }

        Set<Sisterna> newSisternas = new HashSet<>();
        if (sisternasNode != null && sisternasNode.isArray()) {
            for (JsonNode sisternaNode : sisternasNode) {
                Sisterna sisterna = new Sisterna();

                long capacidad = (long) JsonUtils.getValue(sisternaNode, SISTERNA_CAPACIDAD_ATTRIBUTES, 0);
                if (capacidad > 0) {
                    sisterna.setCapacidad(capacidad);
                }

                String licencia = JsonUtils.getString(sisternaNode, SISTERNA_LICENCIA_ATTRIBUTES, "");
                if (licencia != null && !licencia.isEmpty()) {

                    sisterna.setLicencia(licencia);
                }

                // Agregar al set
                newSisternas.add(sisterna);
            }
        }
        newCamion.setSisternas(newSisternas);
        return newCamion;
    }

    public static Cliente buildCliente(JsonNode clienteNode) throws BadRequestException {
    	Cliente newCliente= new Cliente();

        String razonSocial = JsonUtils.getString(clienteNode, CLIENTE_RAZON_SOCIAL_ATTRIBUTES, "");
        if (razonSocial != null && !razonSocial.isEmpty()) {
            newCliente.setRazonSocial(razonSocial);
        } else {
            throw new BadRequestException("El nombre del cliente no puede ser nulo o vacío");
        }

        String email = JsonUtils.getString(clienteNode, CLIENTE_EMAIL_ATTRIBUTES, "");
        if (email != null && !email.isEmpty()) {
            newCliente.setEmail(email);
        } else {
            throw new BadRequestException("El email del cliente no puede ser nulo o vacío");
        }
        return newCliente;
    }


    public static Producto buildProducto(JsonNode productoNode) throws BadRequestException {
        Producto newProducto = new Producto();
        String producto = JsonUtils.getString(productoNode, PRODUCTO_NOMBRE_ATTRIBUTES, "");

        if (producto != null && !producto.isEmpty()) {
            newProducto.setProducto(producto);
        } else {
            throw new BadRequestException("El nombre del producto no puede ser nulo o vacío");
        }
        return newProducto;
    }
}
