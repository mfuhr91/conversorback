package com.conversorback.api.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;

public class JsoupHtml {

    public static Map<String,String> obtenerEuro() throws IOException {

        String[] urls = {
            "https://www.oficialhoy.com.ar/p/cotizacion-euro.html",
            "https://www.paralelohoy.com.ar/p/cotizacion-euro-hoy-argentina.html",
            "https://www.precioeuroblue.com.ar",
        };

        Map<String, String> euroMapa = new HashMap<>();
        euroMapa.put("tipo", "euro_blue");
        // Double euroVenta = 0.0;
        // SI LA PÁGINA ESTA CAIDA O CAMBIÓ SU URL, NO INGRESA AL IF Y LEE LA SUGUIENTE PÁGINA
        if (getStatusConnectionCode(urls[0]) == 200) { 
             getPrimeraCotizacion(euroMapa, urls);
            
        } else if (getStatusConnectionCode(urls[1]) == 200){
            
            System.out.println("El Status Code de www.oficialhoy.com.ar no es OK, es: " + getStatusConnectionCode(urls[0]));

            getSegundaCotizacion(euroMapa, urls); 
        } else if (getStatusConnectionCode(urls[2]) == 200){
            
            System.out.println("El Status Code de www.paralelohoy.com.ar no es OK, es: " + getStatusConnectionCode(urls[1]));
            
            getTerceraCotizacion(euroMapa, urls); 
        }else {
            System.out.println("El Status Code de www.precioeuroblue.com.ar no es OK, es: " + getStatusConnectionCode(urls[2]));
            euroMapa.put("valor", "0.0");
            return euroMapa;
        }
        //System.out.println(euroVenta);
        


        
       
        return euroMapa;
    }


    private static void getPrimeraCotizacion(Map<String,String>  euroMapa, String[] urls){

        Document doc = getHtmlDocument(urls[0]);

        String fila = doc.select("#websendeos tbody tr").last().text();

        String[] result = fila.split(" ");

        String euro = result[3].substring(1);

        try {
            Double euroVenta = Double.parseDouble(euro);

            euroMapa.put("valor", euroVenta.toString());


            System.out.println("VENTA OFICIALHOY.COM.AR: " + euroVenta);
        } catch (NumberFormatException e) {
            // SI CAMBIÓ LA ESTRUCTURA DE LA PAGINA Y NO SE PUEDE CONVERTIR EL TEXTO A DOUBLE, SIGUE CON LA OTRA PAGINA
            getSegundaCotizacion(euroMapa, urls); 
            System.out.println("No se pudo leer el precio del euro de la página OFICIALHOY.COM.AR");
        }

        
    }

    private static void getSegundaCotizacion(Map<String,String>  euroMapa, String[] urls){
        Document doc = getHtmlDocument(urls[1]);

        String fila = doc.select(".tabla tbody td").last().text();

        String euro = fila.substring(1);
        
        try {
            Double euroVenta = Double.parseDouble(euro);
            euroMapa.put("valor", euroVenta.toString());
            System.out.println("VENTA PARALELOHOY.COM.AR: " + euro);
            
        } catch (NumberFormatException e) {
            // SI CAMBIÓ LA ESTRUCTURA DE LA PAGINA Y NO SE PUEDE CONVERTIR EL TEXTO A DOUBLE, SIGUE CON LA OTRA PAGINA
            getTerceraCotizacion(euroMapa, urls);
            System.out.println("No se pudo leer el precio del euro de la página PARALELOHOY.COM.AR");
        }
    }
    
    private static void getTerceraCotizacion(Map<String,String> euroMapa, String[] urls){
        Document doc = getHtmlDocument(urls[2]);

        String euro = doc.select(".entry span").last().text();

        
        
        try {
            Double euroParse = Double.parseDouble(euro);

            Double euroVenta = (double) Math.round(euroParse);

            euroMapa.put("valor", euroVenta.toString());

            System.out.println("VENTA PRECIOEUROBLUE.COM.AR: " + euroVenta);
            
        } catch (NumberFormatException e) {
            euroMapa.put("valor", "0.0");
            System.out.println("No se pudo leer el precio del euro de la página PRECIOEUROBLUE.COM.AR");
        }
    }

    /**
     * Con esta método compruebo el Status code de la respuesta que recibo al hacer
     * la petición EJM: 200 OK 300 Multiple Choices 301 Moved Permanently 305 Use
     * Proxy 400 Bad Request 403 Forbidden 404 Not Found 500 Internal Server Error
     * 502 Bad Gateway 503 Service Unavailable
     * 
     * @param url
     * @return Status Code
     */
    public static int getStatusConnectionCode(String url) {

        Response response = null;

        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
            return 404;
        }
        return response.statusCode();
    }

    /**
     * Con este método devuelvo un objeto de la clase Document con el contenido del
     * HTML de la web que me permitirá parsearlo con los métodos de la librelia
     * JSoup
     * 
     * @param url
     * @return Documento con el HTML
     */
    public static Document getHtmlDocument(String url) {

        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).get();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el HTML de la página" + ex.getMessage());
        }
        return doc;
    }

}
