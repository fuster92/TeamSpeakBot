/*
 * Comando.java
 *
 * 01/05/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package modelo;

/**
 * Clase de Comando
 */
public class Comando {

    private String nombre;
    private String descripcion;
    private String[] modificadores;

    /**
     * Construye un Comando
     */
    public Comando(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
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
}
