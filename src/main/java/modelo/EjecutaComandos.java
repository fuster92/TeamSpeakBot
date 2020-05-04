/*
 * EjecutaComandos.java
 *
 * 01/05/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package modelo;

/**
 * Interfaz de EjecutaComandos
 */
public interface EjecutaComandos {

    /**
     * Lista los comandos disponibles y su descripcion
     */
    String listarComandos();

    /**
     * Incluye un comando nuevo
     */
    void incluirComando();

}
