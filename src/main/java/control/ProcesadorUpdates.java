/*
 * ProcesadorUpdates.java
 *
 * 30/04/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package control;

import modelo.BotTeamSpeak;
import modelo.Instancia;
import modelo.utils.Comando;
import modelo.TSServer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static control.LanzarBot.configuracion;

/**
 * Clase de ProcesadorUpdates
 */
public class ProcesadorUpdates  {

    private static final String MENSAJE_LISTILLO = "Esperate un poco anda chusto";
    private static ProcesadorUpdates instancia = null; //Es singleton
    private static int MI_ID = 5376156;

    private static final String SERVER_APAGADO = "Ya he apagado el server, ";
    private static final String SERVER_ENCENDIDO = "Ya lo tienes encendido, ";

    private Map<Long, Long> ultimoComandoUsuario = new HashMap<>();

    private static final String MENSAJE_COMANDO_NO_VALIDO = "Eso no es un comando ababol";
    private static final String MENSAJE_PRESENTACION = "Que pasa locuelos";


    private BotTeamSpeak bot;
    private Instancia server;
    private ExecutorService executorService;

    private boolean esAdmin = false;
    private boolean notificacionActivada = false;
    private Long idUsuario = 0L;

    /**
     * Construye un ProcesadorUpdates
     * @param bot
     */
    private ProcesadorUpdates(BotTeamSpeak bot, Instancia server) {
        this.bot = bot;
        this.server = server;

        executorService = Executors.newFixedThreadPool(8);
    }

    /**
     * Devuelve la instancia
     */
    public static ProcesadorUpdates devolverInstancia(BotTeamSpeak bot, Instancia server) {
        if (instancia == null) {
            instancia = new ProcesadorUpdates(bot, server);
        }
        return instancia;
    }

    public void procesar(Update update) {
        idUsuario = obtenerIdUsuario(update);
        esAdmin = esAdmin(update);

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

        if (esComando(update)) {
            ultimoComandoUsuario.put(idUsuario, System.currentTimeMillis());
            ejecutarComando(update);
        }
    }

    /**
     * Ejecuta el comando pasado
     * @param update
     */
    private void ejecutarComando(final Update update) {
        executorService.submit(new Runnable() {
            public void run() {
                String recibido = update.getMessage().getText();
                Comando comandoRecibido = Comando.leeComando(recibido);
                System.out.println(comandoRecibido);
                //Si el comando no necesita administrador o el user es
                if (!comandoRecibido.necesitaAdmin() || esAdmin) {
                    if (comandoRecibido != Comando.COMANDO_NO_VALIDO  && notificacionActivada){
                        notificarAdmin(update, comandoRecibido);
                    }
                    switch (Comando.leeComando(recibido)){

                        case START:
                            enviarMensaje(update, MENSAJE_PRESENTACION);
                            break;

                        case HELP:
                            listarComandos(update);
                            break;

                        case APAGAR_TS:
                            comandoApagar(update);
                            break;

                        case ENCENDER_TS:
                            comandoEncender(update);
                            break;

                        case APAGAR_BOT:
                            apagarBot(update);
                            break;

                        case ENCENDER_SERVER_CARO:
                            encenderServerCaro();
                            break;

                        case APAGAR_SERVER_CARO:
                            apagarServerCaro();
                            break;

                        case NOTIFICAR:
                            toggleNotificacion();
                            break;

                        case COMANDO_NO_VALIDO:
                            enviarMensaje(update, Comando.COMANDO_NO_VALIDO.devuelveDescripcion());
                            break;
                    }
                } else {
                    enviarMensaje(update, "No tienes permisos para esto");
                }
            }
        });
    }

    /**
     * Apaga el server caro
     */
    private void apagarServerCaro() {
        server.apagar();
    }

    /**
     * Enciende el server caro
     */
    private synchronized void encenderServerCaro() {
        server.encender();
    }

    /**
     * Activa o desactiva la notificacion
     */
    private void toggleNotificacion() {
        if (notificacionActivada){
            enviarMensajeAdmin("Quitando notificaciones");
        } else {
            enviarMensajeAdmin("Activando notificaciones");
        }
        notificacionActivada = !notificacionActivada;
    }

    /**
     * Manda un listado de comandos
     * @param update
     */
    private void listarComandos(Update update) {
        enviarMensaje(update, Comando.listarComandos(esAdmin));
    }

    /**
     * Notifica al admin
     */
    private void notificarAdmin(Update update, Comando comando){
        if (esAdmin){
            return;
        }
        String texto = "Usuario " +
                obtenerIdUsuario(update) + ":" +
                obtenerNombreUsuario(update) + " Comando " + comando;
        enviarMensajeAdmin(texto);
    }

    /**
     * Envia un mensaje al admin
     * @param texto
     */
    private void enviarMensajeAdmin(String texto) {
        SendMessage mensaje = new SendMessage();
        mensaje.setText(texto);
        mensaje.setChatId(configuracion.getProperty(LanzarBot.ID_CHAT_ADMIN));
        enviarMensaje(mensaje);
    }

    private String obtenerNombreUsuario(Update update) {
        return update.getMessage().getChat().getUserName();
    }

    /**
     * Obtiene el id del usuario
     */
    private Long obtenerIdUsuario(Update update) {
        return update.getMessage().getChat().getId();
    }

    /**
     * Apaga el bot
     */
    private void apagarBot(Update update) {
        System.exit(0);
    }

    /**
     * Comprueba si el usuario es administrador
     */
    private boolean esAdmin(Update update) {
        // TODO: 01/05/2020 implementar mejor solución
        return obtenerIdUsuario(update) == MI_ID;
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
        apagarServerTs();
        enviarMensaje(update, SERVER_APAGADO + devuelveNombreEmisor(update));
        notificarAdmin(update, Comando.APAGAR_TS);
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
    public synchronized void enviarMensaje(SendMessage message) {
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
