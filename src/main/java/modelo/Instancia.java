/*
 * Instancia.java
 *
 * 11/05/2020
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
 * Clase de Instancia
 */
public class Instancia {

    private static final String COMANDO = "gcloud";
    private static final String COMPUTE = "compute";
    private static final String INSTANCES = "instances";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String NO_INTERACTIVO = "q";
    private static final String LIST = "list";
    private static final String ENCENDIDA = "RUNNING";

    private static String FILTRO = "--filter=\"name=('%s')\"";
    private String nombre;
    private boolean encendida;

    /**
     * Construye un Instancia
     */
    public Instancia(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Enciende la instancia
     */
    public void encender(){
        CommandLine encender = CommandLine.parse(COMANDO);
        encender.addArgument(COMPUTE);
        encender.addArgument(INSTANCES);
        encender.addArgument(START);
        encender.addArgument(nombre);
        encender.addArgument(NO_INTERACTIVO);
    }

    /**
     * Apaga la instancia
     */
    public void apagar(){
        CommandLine encender = CommandLine.parse(COMANDO);
        encender.addArgument(COMPUTE);
        encender.addArgument(INSTANCES);
        encender.addArgument(STOP);
        encender.addArgument(nombre);
        encender.addArgument(NO_INTERACTIVO);
    }

    /**
     * Comprueba si esta encendida la instancia
     */
    public boolean estaEncendida(){
        //gcloud compute instances list --filter="name=('postgresql-p4')" --format="csv[no-heading](status)"
        CommandLine encender = CommandLine.parse(COMANDO);
        DefaultExecutor defaultExecutor = new DefaultExecutor();

        encender.addArgument(COMPUTE);
        encender.addArgument(INSTANCES);
        encender.addArgument(LIST);
        //Filtro
        encender.addArgument(String.format(FILTRO, nombre));
        encender.addArgument("--format=\"csv[no-heading](status)\"");
        ByteArrayOutputStream salida = capturarSalida(defaultExecutor);
        try {
            defaultExecutor.execute(encender);
            return salida.toString().equals(ENCENDIDA);
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
