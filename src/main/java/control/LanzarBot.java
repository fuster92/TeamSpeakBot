/*
 * LanzarBot.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package control;

import modelo.BotTeamSpeak;
import modelo.Instancia;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Clase de LanzarBot
 */
public class LanzarBot {
    private static final String CABECERA = "Fichero de configuracion";

    public static final String NOMBRE_BOT = "nombre_bot";
    private static String nombreBot = "jamaos_bot";

    private static final String NOMBRE_SERVER_CARO = "nombreServerCaro";
    private static String nombreServerCaro = "postgresql-p4";


    private static final String TOKEN_BOT = "token_bot";
    private static String tokenBot = "";

    public static final String ID_CHAT_ADMIN = "id_chat_admin";
    private static final String ID_ADMIN = "id_admin";

    public static final String API_KEY = "apiGoogle";
    private static String apiKey = "";
    public static final String URL = "url";
    private static String url = "javierfuster.codes";

    private static final String INSERTA_TOKEN_BOT = "<Inserta el token aqui>";
    private static final String INSERTA_ID_CHAT_ADMIN = "<Inserta el id del chat de admin";
    private static final String INSERTA_ID_ADMIN = "<Inserta el id aqui>";

    private static String RUTA_ARCHIVO_CONFIG = "configuracionBot.properties";
    private static String RUTA_ARCHIVO_ADMIN = "adminBot.properties";
    public static Properties configuracion;


    public static void main(String[] args) {
        leerConfiguracion();
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        BotTeamSpeak bot = BotTeamSpeak.devolverInstancia(
                nombreBot, tokenBot);
        Instancia servidor = new Instancia(nombreServerCaro);
        ProcesadorUpdates procesadorUpdates = ProcesadorUpdates.devolverInstancia(bot, servidor);
        bot.establecerProcesadorUpdates(procesadorUpdates);


        try{
            botsApi.registerBot(bot);

        } catch (TelegramApiException e){
        }
    }

    private static void leerConfiguracion() {
        configuracion = new Properties();
        try {
            configuracion.load(new FileInputStream(RUTA_ARCHIVO_CONFIG));
            url = configuracion.getProperty(URL);
            nombreBot = configuracion.getProperty(NOMBRE_BOT);
            nombreServerCaro = configuracion.getProperty(NOMBRE_SERVER_CARO);
            tokenBot = configuracion.getProperty(TOKEN_BOT);
            apiKey = configuracion.getProperty(API_KEY);

        } catch (IOException e) {
            configuracion.setProperty(URL, url);
            configuracion.setProperty(NOMBRE_BOT, nombreBot);
            configuracion.setProperty(TOKEN_BOT, INSERTA_TOKEN_BOT);
            configuracion.setProperty(ID_ADMIN, INSERTA_ID_ADMIN);
            configuracion.setProperty(ID_CHAT_ADMIN, INSERTA_ID_CHAT_ADMIN);
            configuracion.setProperty(NOMBRE_SERVER_CARO, nombreServerCaro);
            configuracion.setProperty(API_KEY, apiKey);
            guardarConfiguracion();
            System.exit(0);//salimos para poder configurar
        }
    }

    /**
     * Guarda la configuración
     */
    private static void guardarConfiguracion() {
        try {
            FileOutputStream fichero = new FileOutputStream(RUTA_ARCHIVO_CONFIG);
            configuracion.store(fichero, CABECERA);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
