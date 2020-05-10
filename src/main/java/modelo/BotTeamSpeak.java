/*
 * BotTeamSpeak.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package modelo;

import control.ProcesadorUpdates;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Clase de BotTeamSpeak
 */
public class BotTeamSpeak extends TelegramLongPollingBot{

    private static BotTeamSpeak instancia = null;
    private final String NOMBRE_BOT;
    private final String TOKEN_BOT;



    private ProcesadorUpdates procesadorUpdates;

    /**
     * Construye un control.BotServerSQL
     */
    private BotTeamSpeak(String nombre, String token) {
        NOMBRE_BOT = nombre;
        TOKEN_BOT = token;
    }

    /**
     * Establece cual es el procesador de updates
     */
    public void establecerProcesadorUpdates(ProcesadorUpdates procesadorUpdates) {
        this.procesadorUpdates = procesadorUpdates;
    }

    /**
     * Devuelve la instancia
     */
    public static synchronized BotTeamSpeak devolverInstancia(String nombre,
                                                              String token){
        if (instancia == null){
            instancia = new BotTeamSpeak(nombre, token);
        }
        return instancia;
    }

    @Override
    public void onUpdateReceived(Update update) {
        procesadorUpdates.procesar(update);
    }

    @Override
    public String getBotUsername() {
        return NOMBRE_BOT;
    }

    @Override
    public String getBotToken() {
        return TOKEN_BOT;
    }
}
