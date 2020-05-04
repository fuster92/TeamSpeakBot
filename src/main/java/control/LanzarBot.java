/*
 * LanzarBot.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package control;

import modelo.BotTeamSpeak;
import modelo.SinusBotApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Clase de LanzarBot
 */
public class LanzarBot {
    private static final String NOMBRE_BOT = "jamaos_bot";
    private static final String TOKEN_BOT = System.getenv("TEAMSPEAK_BOT_TOKEN");

    private static String ip = "javierfuster.codes";
    private static int puerto = 8087;
    private static String user = "admin";
    private static String pass = "TQq4J1zWzWVs";
    private static String botID = "7669dc2b-3011-4c7c-a7eb-9d690b4540d4";

    public SinusBotApi botMusica = new SinusBotApi(ip, puerto, user, pass, botID);

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi botsApi = new TelegramBotsApi();
        BotTeamSpeak bot = BotTeamSpeak.devolverInstancia(NOMBRE_BOT, TOKEN_BOT);
        ProcesadorUpdates procesadorUpdates = ProcesadorUpdates.devolverInstancia(bot);
        bot.establecerProcesadorUpdates(procesadorUpdates);
        Logger logger = LoggerFactory.getLogger(LanzarBot.class);
        try{
            botsApi.registerBot(bot);

        } catch (TelegramApiException e){
            logger.error(e.toString());
        }
    }


}
