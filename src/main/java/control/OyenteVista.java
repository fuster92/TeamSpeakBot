/*
 * OyenteVista.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package control;

/**
 * Interfaz de OyenteVista
 */
public interface OyenteVista {
    public enum Evento {SALIR}

    /**
     * Notifica de un evento a control
     */
    public void notificarEvento(Evento evento, Object objeto);
}