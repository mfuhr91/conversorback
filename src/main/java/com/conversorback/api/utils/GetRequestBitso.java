package com.conversorback.api.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.conversorback.api.model.Book;
import com.conversorback.api.model.Respuesta;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetRequestBitso {

    static Logger log = LoggerFactory.getLogger(JsoupHtml.class);
    
    static final List<Map<String, String>> lista = new ArrayList<Map<String, String>>();
    
    
    /**
     * Con este método se comprueba el estado del servidor API REST externo,
     * y si la respuesta es 200(OK), se procede a ejecutar la lógica
     * para obtener el valor correspondiente del  bitcoin, luego se devuelve la
     * lista para ser enviada al servicio de Moneda
     * 
     * @param urls
     * @param lista
     * @return lista con errores si los hay, y valor del bitcoin como ultimo item de la lista
     */
    public static List<Map<String, String>> getRequest() throws IOException {
        Map<String, String> mapa = new HashMap<String, String>();
        Book ultimo = null; 
        //final List<String> lista = new ArrayList<>();
        if(getStatusConnectionCode() == 200 ){
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = null;
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.bitso.com/v3/trades/?book=btc_ars"))
                .build();

            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {

                String estado = "No se pudo leer la API REST --> SERVIDOR: https://api.bitso.com/v3/trades/?book=btc_ars";
                log.error(estado);
                mapa.put("estado", estado);

                lista.add(mapa);
            }    
            
            // CAMBIA LA ESTRATEGIA DE NOMBRES DE ATRIBUTOS A SNAKE_CASE
            ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
                    
            try {
                Respuesta respuesta = mapper.readValue(response.body(), Respuesta.class);
                List<Book> books = respuesta.getPayload();                Date fechaActual = new Date();
                
                boolean hayUltimo = false;
                for (Book book : books) {
                   // System.out.println(book); 
                    if(hayUltimo){
                        break;
                    }
                    ultimo = book;
                    for (Book book2 : books){
                        if(fechaActual.getTime() - ultimo.getCreatedAt().getTime() >= fechaActual.getTime() - book2.getCreatedAt().getTime()){
                            if(book2.getMakerSide().equals("buy")){
                                ultimo = book2;
                                hayUltimo = true;
                                break;
                            }
                        }
                    }
                }    
               
                mapa.put("precio", ultimo.getPrice().toString());

                lista.add(mapa);

            } catch (Exception e) {
                String estado = "No se pudo mappear el bitcoin al objeto JAVA";

                log.error(estado);

                mapa.put("estado", estado);

                lista.add(mapa);
            }
        }else{

            log.error("No se pudo leer la API REST --> SERVIDOR: https://api.bitso.com/v3/trades/?book=btc_ars");
            mapa.put("error", "No se pudo leer la API REST --> SERVIDOR: https://api.bitso.com/v3/trades/?book=btc_ars");
            lista.add(mapa);
        }
        
        return lista;
    }

    /**
     * Con esta método compruebo el Status code de la respuesta que recibo al hacer
     * la petición EJM: 200 OK 300 Multiple Choices 301 Moved Permanently 305 Use
     * Proxy 400 Bad Request 403 Forbidden 404 Not Found 500 Internal Server Error
     * 502 Bad Gateway 503 Service Unavailable
     * 
     * @param url
     * @return Status Code
     * @throws IOException
     */
    public static int getStatusConnectionCode() throws IOException {
        Map<String, String> mapa = new HashMap<String, String>();
        URL url = null;
        int code = 0;
                
        try {
            url = new URL("https://api.bitso.com/v3/trades/?book=btc_ars");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            code = connection.getResponseCode();
            log.info("Conectado al servidor " + url + " con código: " + code);
        } catch (Exception e) {
            
            String estado = "El servidor no responde --> SERVIDOR: " + url;
            log.error("El servidor no responde --> SERVIDOR: " + url + " ERROR: " + e);
            mapa.put("estado", estado);

            lista.add(mapa);

            e.printStackTrace();
            
            
        }

        return code;
    }
    
    
    
}
