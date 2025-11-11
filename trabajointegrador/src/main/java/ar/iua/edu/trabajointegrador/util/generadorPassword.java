package ar.iua.edu.trabajointegrador.util;

public class generadorPassword {
    public static int generarPassword() {
        String caracteres = "0123456789";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            int index = (int) (Math.random() * caracteres.length());
            password.append(caracteres.charAt(index));
        }
        System.out.println(password.toString());
        return Integer.parseInt(password.toString());
    }
}
