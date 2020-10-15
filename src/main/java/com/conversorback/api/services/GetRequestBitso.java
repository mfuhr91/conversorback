package com.conversorback.api.services;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

import java.util.List;

import com.conversorback.api.model.Book;
import com.conversorback.api.model.Respuesta;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class GetRequestBitso {

    public static Double getRequest() {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.bitso.com/v3/trades/?book=btc_ars"))
                .build();
        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }    
        ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        Book ultimo = null;
        try {
            Respuesta respuesta = mapper.readValue(response.body(), Respuesta.class);
            List<Book> books = respuesta.getPayload();
            Date fechaActual = new Date();
            System.out.println(fechaActual);  
            for (Book book : books) {
                System.out.println(book);
                
                ultimo = book;
                for (Book book2 : books){
                    if(fechaActual.getTime() - ultimo.getCreatedAt().getTime() >= fechaActual.getTime() - book2.getCreatedAt().getTime()){
                        if(book2.getMakerSide().equals("buy")){
                            ultimo = book2;
                        }
                    }
                }
            }    
            System.out.println("ULTIMO VALOR COMPRA BITCOIN: "+ultimo);
            
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return ultimo.getPrice();
    }
    
    
    
}
