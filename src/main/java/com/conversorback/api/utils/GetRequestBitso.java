package com.conversorback.api.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.conversorback.api.model.Book;
import com.conversorback.api.model.Respuesta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class GetRequestBitso {

    

    public static Map<String,String>  getRequest() throws IOException {
        Book ultimo = null; 
        Map<String, String> bitcoin = new HashMap<>();
        if(getStatusConnectionCode() == 200){
            HttpClient client = HttpClient.newHttpClient();
          HttpResponse<String> response = null;
          HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.bitso.com/v3/trades/?book=btc_ars"))
          .build();
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }    
            
            // CAMBIA LA ESTRATEGIA DE NOMBRES DE ATRIBUTOS A SNAKE_CASE
            ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
            
            try {
                Respuesta respuesta = mapper.readValue(response.body(), Respuesta.class);
                List<Book> books = respuesta.getPayload();
                Date fechaActual = new Date();
                
                for (Book book : books) {
                   // System.out.println(book); 
                    
                    ultimo = book;
                    for (Book book2 : books){
                        if(fechaActual.getTime() - ultimo.getCreatedAt().getTime() >= fechaActual.getTime() - book2.getCreatedAt().getTime()){
                            if(book2.getMakerSide().equals("buy")){
                                ultimo = book2;
                            }
                        }
                    }
                }    
                //System.out.println("ULTIMO VALOR COMPRA BITCOIN: "+ultimo); 
               

                bitcoin.put("tipo", "bitcoin");
                bitcoin.put("valor", ultimo.getPrice().toString());

            } catch (JsonProcessingException e) {
                bitcoin.put("tipo", "bitcoin");
                bitcoin.put("valor", "0.0");
                return bitcoin;
                /* e.printStackTrace();
                System.out.println("############# NO SE ENCONTRO LA RESPUESTA"); */
                
            }
        }else{
            bitcoin.put("tipo", "bitcoin");
            bitcoin.put("valor", "0.0");

            System.out.println("El Status Code no es OK, es: " + getStatusConnectionCode()); 
            
            return bitcoin;
        }
        
        

        return bitcoin;
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
        URL url;
        int code = 0;
        try {
            url = new URL("https://api.bitso.com/v3/trades/?book=btc_ars");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            code = connection.getResponseCode();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return code;
    }
    
    
    
}
