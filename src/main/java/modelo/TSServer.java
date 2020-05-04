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

import java.io.IOException;

/**
 * Clase de TSServer
 */
public class TSServer {
    private static final String COMANDO = "sudo systemctl";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String TEAMSPEAK = "teamspeak";

    /**
     * Enciende el server de ts
     */
    public static void encender(){
        CommandLine SYSTEMCTL = CommandLine.parse(COMANDO);
        SYSTEMCTL.addArgument(START);
        SYSTEMCTL.addArgument("teamspeak");
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        try {
            defaultExecutor.execute(SYSTEMCTL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Apaga el server de TeamSpeak
     */
    public static void apagar() {
        CommandLine SYSTEMCTL = CommandLine.parse(COMANDO);
        SYSTEMCTL.addArgument(STOP);
        SYSTEMCTL.addArgument(TEAMSPEAK);
        DefaultExecutor defaultExecutor = new DefaultExecutor();
        try {
            defaultExecutor.execute(SYSTEMCTL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
