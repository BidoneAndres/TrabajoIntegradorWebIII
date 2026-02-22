package ar.iua.edu.trabajointegrador.controllers;

public final class Constants {
	public static final String URL_API = "/api";
	public static final String URL_API_VERSION = "/v1";
	public static final String URL_BASE = URL_API + URL_API_VERSION;
	
	//Agregar las url de los servicios que vayamos creando
    public static final String URL_PRODUCTOS = URL_BASE + "/producto";
    public static final String URL_ORDENES = URL_BASE + "/orden";
    public static final String URL_USUARIOS = URL_BASE + "/usuarios";
    public static final String URL_CARGA = URL_BASE + "/carga";
    public static final String URL_CONCILIACION = URL_BASE + "/conciliacion";
    
	public static final String URL_LOGIN = URL_BASE + "/login";
	public static final String URL_MAIL = URL_BASE + "/mail";	
	public static final String URL_AUTHORIZATION = URL_BASE + "/authtest";
	
}
