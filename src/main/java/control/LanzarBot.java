/*
 * LanzarBot.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package control;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.compute.Compute;
import com.google.api.services.compute.model.Instance;
import com.google.api.services.compute.model.InstanceList;
import modelo.BotTeamSpeak;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

/**
 * Clase de LanzarBot
 */
public class LanzarBot {
    private static final String CABECERA = "Fichero de configuracion";

    public static final String NOMBRE_BOT = "nombre_bot";
    private static String nombreBot = "jamaos_bot";

    private static final String TOKEN_BOT = "token_bot";
    //System.getenv("TEAMSPEAK_BOT_TOKEN");
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

    /** Set PROJECT_ID to your Project ID from the Overview pane in the Developers console. */
    private static final String PROJECT_ID = "practica4bd";

    /** Set Compute Engine zone. */
    private static final String ZONE_NAME = "europe-west2-c";

    /*
        Variables cloud api
     */
    private static HttpTransport httpTransport;
    /** Global instance of the JSON factory. */
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();



    public static void main(String[] args) throws GeneralSecurityException, IOException {
        httpTransport = GoogleNetHttpTransport.newTrustedTransport();

        GoogleCredential credential = GoogleCredential.getApplicationDefault();

        Compute compute = new Compute.Builder(httpTransport, JSON_FACTORY, credential).build();


        leerConfiguracion();
        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        BotTeamSpeak bot = BotTeamSpeak.devolverInstancia(
                nombreBot, tokenBot);
        ProcesadorUpdates procesadorUpdates = ProcesadorUpdates.devolverInstancia(bot);
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
            tokenBot = configuracion.getProperty(TOKEN_BOT);
            apiKey = configuracion.getProperty(API_KEY);

        } catch (IOException e) {
            configuracion.setProperty(URL, url);
            configuracion.setProperty(NOMBRE_BOT, nombreBot);
            configuracion.setProperty(TOKEN_BOT, INSERTA_TOKEN_BOT);
            configuracion.setProperty(ID_ADMIN, INSERTA_ID_ADMIN);
            configuracion.getProperty(ID_CHAT_ADMIN, INSERTA_ID_CHAT_ADMIN);
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

    public static void printInstances(Compute compute) throws IOException {
        System.out.println("lista");

        Compute.Instances.List instances = compute.instances().list(PROJECT_ID, ZONE_NAME);
        InstanceList lista = instances.execute();
        if (lista.getItems() == null) {
            System.out.println("No instances found. Sign in to the Google Developers Console and create "
                    + "an instance at: https://console.developers.google.com/");
        } else {
            for (Instance instance : lista.getItems()) {
                System.out.println(instance.toPrettyString());
            }
        }
    }
}
