/*
 * ProcesadorUpdates.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package control;

import modelo.BotTeamSpeak;
import modelo.TSServer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase de ProcesadorUpdates
 */
public class ProcesadorUpdates {

    private static final long TIEMPO_MIN_COMANDOS = 10000;
    private static final String MENSAJE_LISTILLO = "Esperate un poco anda chusto";
    private static ProcesadorUpdates instancia = null; //Es singleton
    private static int MI_ID = 5376156;

    private static final String SERVER_APAGADO = "Ya he apagado el server, ";
    private static final String SERVER_ENCENDIDO = "Ya lo tienes encendido, ";


    private static final String START = "start";
    private static final String APAGAR = "apagar";
    private static final String APAGAR_BOT = "apagarbot";
    private static final String ENCENDER = "encender";
    private static final String HELP = "help";
    private static final String PLAY = "play";

    private Map<Long, Long> ultimoComandoUsuario = new HashMap<>();

    private static final String[] comandos = {START, APAGAR, APAGAR_BOT, ENCENDER};

    private static final String MENSAJE_COMANDO_NO_VALIDO = "Eso no es un comando ababol";
    private static final String MENSAJE_PRESENTACION = "Que pasa locuelos";


    private BotTeamSpeak bot;

    /**
     * Construye un ProcesadorUpdates
     * @param bot
     */
    private ProcesadorUpdates(BotTeamSpeak bot) {
        this.bot = bot;
    }

    /**
     * Devuelve la instancia
     */
    public static ProcesadorUpdates devolverInstancia(BotTeamSpeak bot) {
        if (instancia == null) {
            instancia = new ProcesadorUpdates(bot);
        }
        return instancia;
    }

    public void procesar(Update update) {
        if (esMensajeTexto(update)){
            procesarTexto(update);
        }

    }

    /**
     * Comprueba si el update es un mensaje de texto
     */
    private boolean esMensajeTexto(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    /**
     * Procesa un mensaje de texto
     */
    private void procesarTexto(Update update){
        long idUsuario = obtenerIdUsuario(update);
        ultimoComandoUsuario.put(idUsuario, System.currentTimeMillis());
        if (esComando(update)) {
            String recibido = update.getMessage().getText().substring(1).toLowerCase();
            String[] comando = recibido.split(" ");
            switch (comando[0]){

                case START:
                    enviarMensaje(update, MENSAJE_PRESENTACION);
                    break;

                case HELP:
                    break;

                case APAGAR:
                    comandoApagar(update);
                    break;

                case ENCENDER:
                    comandoEncender(update);
                    break;

                case PLAY:

                    break;

                case APAGAR_BOT:
                    apagarBot(update);

                default:
                    enviarMensaje(update, MENSAJE_COMANDO_NO_VALIDO);
                    break;
            }
        }
    }

    /**
     * Obtiene el id del usuario
     * @param update
     * @return
     */
    private Long obtenerIdUsuario(Update update) {
        return update.getMessage().getChat().getId();
    }

    /**
     * Apaga el bot
     */
    private void apagarBot(Update update) {
        if (esAdmin(update)){
            System.exit(0);
        } else {
            enviarMensaje(update, "No eres admin shur");
        }
    }

    /**
     * Comprueba si el usuario es administrador
     */
    private boolean esAdmin(Update update) {
        // TODO: 01/05/2020 implementar mejor solución
        return update.getMessage().getFrom().getId() == MI_ID;
    }

    /**
     * Acciones para el comando de encender
     */
    private void comandoEncender(Update update) {
        encenderServerTs();
        SendMessage mensaje = construyeMensaje(update);
        mensaje.setText(SERVER_ENCENDIDO + update.getMessage().getChat().getFirstName());
    }

    /**
     * Acciones para el comando de apagar
     */
    private void comandoApagar(Update update) {
        if (haPasadoSuficienteTiempo(update)){
            apagarServerTs();
            enviarMensaje(update, SERVER_APAGADO + devuelveNombreEmisor(update));
        }else {
            enviarMensaje(update, MENSAJE_LISTILLO);
        }
    }

    /**
     * Comprueba si el usuario se esta pasando de enviar comandos
     * @return
     * @param update
     */
    private boolean haPasadoSuficienteTiempo(Update update) {
        return (System.currentTimeMillis() - ultimoComandoUsuario.get(obtenerIdUsuario(update))) > TIEMPO_MIN_COMANDOS;
    }

    /**
     * Construye un mensaje nuevo
     */
    private SendMessage construyeMensaje(Update update){
        SendMessage mensaje = new SendMessage();
        mensaje.setChatId(update.getMessage().getChatId());
        return mensaje;
    }

    /**
     * Intenta envíar un mensaje
     */
    private void enviarMensaje(Update update, String texto) {
        SendMessage mensaje = construyeMensaje(update);
        mensaje.setText(texto);
        enviarMensaje(mensaje);
    }

    /**
     * Intenta envíar un mensaje
     */
    private void enviarMensaje(SendMessage message) {
        try {
            bot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Comprueba si el mensaje es un comando
     */
    private boolean esComando(Update update){
        return update.getMessage().getText().matches("/[\\w][\\s\\w]*");
    }

    /**
     * Devuelve nombre del emisor
     */
    private String devuelveNombreEmisor(Update update){
        return update.getMessage().getChat().getFirstName();
    }

    /**
     * Apaga el server de TeamSpeak
     */
    private void apagarServerTs() {
        TSServer.apagar();
    }

    /**
     * Enciende el server de TeamSpeak
     */
    private void encenderServerTs() {
        TSServer.encender();
    }
}
