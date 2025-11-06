package ar.iua.edu.trabajointegrador.auth.filters;

public final class AuthConstants {
	public static final long EXPIRATION_TIME = (60 * 60 * 1000);
	public static final String SECRET = "N5gP9zYtLqX3vB2rE8aS1jM4cR6uF0kW7oTnD9xH5pG3sV2yC4mZ8eA1tQ0lR9";
	
	public static final String AUTH_HEADER_NAME = "Authorization";
	public static final String AUTH_PARAM_NAME = "authtoken";
	public static final String TOKEN_PREFIX = "Bearer ";
}