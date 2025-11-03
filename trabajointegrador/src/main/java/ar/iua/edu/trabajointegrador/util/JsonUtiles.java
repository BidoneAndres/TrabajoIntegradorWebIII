package ar.iua.edu.trabajointegrador.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/*Esta es una clase utilitaria, la cual tiene como objetivo simplificarnos la serealizacion de deserealizacion*/

public class JsonUtiles {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdSerializer ser, String formatoFecha) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";

		// Basicamente definimos el formato de la fecha, en el caso de no estar definido
		// utilizaremos el que creamos por defecto
		if (formatoFecha != null)
			defaultFormat = formatoFecha; // Basicamente definimos el formato de la fecha, en el caso de no estar
											// definido utilizaremos el que creamos por defecto

		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule md = new SimpleModule();

		if (ser != null)
			md.addSerializer(clazz, ser);

		mapper.setDateFormat(df);
		mapper.registerModule(md);
		return mapper;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ObjectMapper getObjectMapper(Class clazz, StdDeserializer des, String formatoFecha) {
		ObjectMapper mapper = new ObjectMapper();
		String defaultFormat = "yyyy-MM-dd'T'HH:mm:ssZ";

		// Basicamente definimos el formato de la fecha, en el caso de no estar definido
		// utilizaremos el que creamos por defecto
		if (formatoFecha != null)
			defaultFormat = formatoFecha; // Basicamente definimos el formato de la fecha, en el caso de no estar
											// definido utilizaremos el que creamos por defecto

		SimpleDateFormat df = new SimpleDateFormat(defaultFormat, Locale.getDefault());
		SimpleModule md = new SimpleModule();

		if (des != null)
			md.addDeserializer(clazz, des);

		mapper.setDateFormat(df);
		mapper.registerModule(md);
		return mapper;

	}

	/*
	 * Estos metodos son todos iguales A partir de json, representado por JsonNode,
	 * nos permite cambiar un jeson por alguno de los siguientes tipos de datos
	 * Buscamos los atributos definidos en el attrs y es el valor retornado, si no
	 * se devuelve el default
	 */

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

	public static float getValue(JsonNode node, String[] attrs, float defaultValue) {
		Float r = null;
		for (String attr : attrs) {
			if (node.get(attr) != null) {
				// Intentamos manejar el valor como float sin depender de si el tipo es
				// específicamente un float
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

}
