/*
 * TSServer.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package modelo;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Clase de TSServer
 */
public class TSServer {
    private static final String COMANDO = "sudo systemctl";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String TEAMSPEAK = "teamspeak";
    private static final String ESTA_ACTIVO = "is-active";
    private static final String ACTIVE = "active";


    /**
     * Enciende el server de ts
     */
    public static synchronized void encender(){
        CommandLine SYSTEMCTL = CommandLine.parse(COMANDO);
        SYSTEMCTL.addArgument(START);
        SYSTEMCTL.addArgument("teamspeak");
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        try {
            defaultExecutor.execute(SYSTEMCTL);
        } catch (IOException e) {
            System.out.println("No estas en linux");
        }
    }

    /**
     * Apaga el server de TeamSpeak
     */
    public static synchronized void apagar() {
        CommandLine SYSTEMCTL = CommandLine.parse(COMANDO);
        SYSTEMCTL.addArgument(STOP);
        SYSTEMCTL.addArgument(TEAMSPEAK);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        try {
            defaultExecutor.execute(SYSTEMCTL);
        } catch (IOException e) {
            System.out.println("No estas en Linux");
        }
    }

    /**
     * Comprueba si está encendido el server
     */
    public static boolean estaEncendido(){
        CommandLine SYSTEMCTL = CommandLine.parse(COMANDO);
        SYSTEMCTL.addArgument(ESTA_ACTIVO);
        SYSTEMCTL.addArgument(TEAMSPEAK);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        ByteArrayOutputStream salida = capturarSalida(defaultExecutor);

        try {
            defaultExecutor.execute(SYSTEMCTL);
            return salida.toString().equals(ACTIVE);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Captura la salida estandar del comando
     */
    private static ByteArrayOutputStream capturarSalida(DefaultExecutor executor){
        ByteArrayOutputStream salidaEstandar = new ByteArrayOutputStream();
        PumpStreamHandler streamHandler = new PumpStreamHandler(salidaEstandar);
        executor.setStreamHandler(streamHandler);
        return salidaEstandar;
    }
}
