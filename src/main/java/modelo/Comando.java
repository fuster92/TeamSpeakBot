/*
 * Comando.java
 *
 * 01/05/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package modelo;

import java.util.regex.Pattern;
// TODO: 05/05/2020 Que sea esta clase la que llame a los comandos
/**
 * Clase de Comando
 */
public enum  Comando {
    START("start", "Comando inicial"),
    APAGAR_TS("apagar", "Apaga el servidor de TS"),
    APAGAR_BOT("apagarbot", "Apaga el bot, solo admin", true),
    ENCENDER_TS("encender", "Enciende el servidor de TS"),
    HELP("help", "Lista los comandos"),
    NOTIFICAR("notificar", "Notifica de los comandos enviados", true),
    COMANDO_NO_VALIDO("Comando_no_valido", "Es un comando no válido shur");

    private String nombre;
    private String descripcion;
    private boolean necesitaAdmin;

    private static final String REGEX_COMANDO = "/[\\w][\\s\\w]*";

    /**
     * Construye un Comando
     */
    Comando(String nombre, String descripcion) {
        this(nombre, descripcion, false);
    }

    /**
     * Construye un Comando
     */
    Comando(String nombre, String descripcion, boolean necesitaAdmin) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.necesitaAdmin = necesitaAdmin;
    }

    /**
     * Devuelve la descripción
     */
    public String devuelveDescripcion(){
        return descripcion;
    }

    /**
     * Devuelve el nombre del comando
     */
    public String devuelveNombre(){
        return nombre;
    }

    /**
     * Necesita administrador
     */
    public boolean necesitaAdmin(){
        return necesitaAdmin;
    }

    /**
     * Lee un comando
     */
    public static Comando leeComando(String cadena){
        if (cadena.matches(REGEX_COMANDO)){
            String recibido = cadena.substring(1).toLowerCase();
            String[] cadenasRecibidas = recibido.split(" ");
            for (Comando comando : Comando.values()){
                if (cadenasRecibidas[0].equals(comando.nombre)){
                    return comando;
                }
            }
        }
        return COMANDO_NO_VALIDO;
    }

    /**
     * Lista todos los comandos
     */
    public static String listarComandos(boolean esAdmin){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Lista de comandos disponibles:\n");
        for (Comando comando : Comando.values()){
            if (comando != COMANDO_NO_VALIDO && (!comando.necesitaAdmin || esAdmin)) {
                stringBuilder.append("/");
                stringBuilder.append(comando.nombre).append(" -- ").append(comando.descripcion);
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
