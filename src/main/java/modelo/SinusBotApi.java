/*
 * SinusBotApi.java
 *
 * 02/05/2020
 * Javier Fuster Trallero
 * Versión 0.0
 */

package modelo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 * Clase de SinusBotApi
 */
public class SinusBotApi {

    private final String APISTR = "/api/v1";
    private String token;
    private String ip;
    private int port;

    /**
     * Construye un SinusBotApi
     */
    public SinusBotApi(String ip, int port, String username, String password, String botId) {
        this.ip = ip;
        this.port = port;
        JSONObject loginObj = login(username, password, botId);
        if (loginObj == null) {
            throw new RuntimeException("Login failed!");
        } else {
            this.token = loginObj.getString("token");
        }
    }

    /**
     * Hace login en sinusBot
     */
    private JSONObject login(String username, String password, String botId) {
        @SuppressWarnings("serial")
        HashMap<String,String> arguments = new HashMap<String,String>() {{
            put("username", username.toString());
            put("password", password.toString());
            put("botId", botId.toString());
        }};

        return apicallObject("/bot/login", arguments, "POST");
    }

    /**
     * Hace una llamada a la api de SinusBot
     */
    private String apicall(String api, Map<String, String> args, String requestMethod) {
        StringJoiner sj = new StringJoiner(",");
        for (Map.Entry<String, String> arg : args.entrySet()) {
            sj.add("\"" + arg.getKey() + "\":\"" + arg.getValue() + "\"");

            if (api.contains(":" + arg.getKey()))
                api = api.replace(":" + arg.getKey(), arg.getValue());
        }
        byte[] body = ("{" + sj.toString() + "}").getBytes(StandardCharsets.UTF_8);
        int length = body.length;

        String URL = "http://"+ip+":"+port+APISTR+api;

        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();

            http.setRequestMethod(requestMethod);
            http.setRequestProperty("Authorization", "bearer " + token);

            if (!args.isEmpty()) {
                http.setDoOutput(true);
                http.setFixedLengthStreamingMode(length);
                http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                http.connect();
                try(OutputStream os = http.getOutputStream()) {
                    os.write(body);
                }
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));

            return br.lines().collect(Collectors.joining());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private JSONObject apicallObject(String api, Map<String, String> args, String requestMethod) {
        String jsonStr = apicall(api, args, requestMethod);
        if (jsonStr != null)
            return new JSONObject(jsonStr);
        return null;
    }

    private JSONArray apicallArray(String api, Map<String, String> args, String requestMethod) {
        String jsonStr = apicall(api, args, requestMethod);
        if (jsonStr != null)
            return new JSONArray(jsonStr);
        return null;
    }

    /**
     * Playback - Pause playback
     * @param instanceId uuid/id of the instance
     * @return JSONObject with fields <br>
     * <b>success</b>
     */
    public JSONObject pause(String instanceId) {
        @SuppressWarnings("serial")
        HashMap<String,String> arguments = new HashMap<String,String>() {{
            put("instanceId", instanceId);
        }};

        return apicallObject("/bot/i/:instanceId/pause", arguments, "POST");
    }
}
