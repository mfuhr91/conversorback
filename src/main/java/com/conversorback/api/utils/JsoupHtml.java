package com.conversorback.api.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;



public class JsoupHtml {

    private static String errorEuro;

    /**
     * Con este método se comprueba el estado de cada una de las webs,
     * y si la respuesta es 200(OK), se procede a ejecutar la lógica
     * para obtener el valor correspondiente del euro, luego se devuelve la
     * lista para ser enviada al servicio de Moneda
     * 
     * @param urls
     * @param lista
     * @return lista con errores si los hay, y valor del euro como ultimo item de la lista
     */
    public static List<String> obtenerEuro() throws IOException {

        final List<String> lista = new ArrayList<>();

        String[] urls = {
            "https://www.precioeuroblue.com.ar",
            "https://www.oficialhoy.com.ar/p/cotizacion-euro.html",
            "https://www.paralelohoy.com.ar/p/cotizacion-euro-hoy-argentina.html",
        };
    
        // SI LA PÁGINA ESTA CAIDA O CAMBIÓ SU URL, NO INGRESA AL IF Y LEE LA SUGUIENTE PÁGINA
        if (getStatusConnectionCode(urls[0], lista) == 200) { 
             getPrimeraCotizacion(urls, lista);
            
        } else if (getStatusConnectionCode(urls[1], lista) == 200){

            getSegundaCotizacion(urls, lista); 
        } else if (getStatusConnectionCode(urls[2], lista) == 200){

            getTerceraCotizacion(urls, lista); 
        }else {              
 
            lista.add("0.0");
            return lista;
        }

        System.out.println(lista);
          
        return lista;
    }

    /**
     * Con este método se consigue la cotización desde la página 
     * www.precioeuroblue.com.ar, se parsea a un double, y se agruega a la
     * lista para ser enviada al servicio de Moneda
     * 
     * @param urls
     * @param lista
     */
    private static void getPrimeraCotizacion(String[] urls, List<String> lista){
        Document doc = getHtmlDocument(urls[0]);

        String euro = doc.select(".entry span").last().text(); // .entry span

        Double euroVenta = 0.0;
        
        try {
            euroVenta = Double.parseDouble(euro);

            System.out.println("VENTA PRECIOEUROBLUE.COM.AR: " + euroVenta);
            
        } catch (NumberFormatException e) {

            errorEuro = " Error al obtener el valor del euro --> PÁGINA WEB: " + urls[0];
            lista.add(errorEuro);
            System.out.println("No se pudo leer el precio del euro de la página PRECIOEUROBLUE.COM.AR");
        }

        if(!lista.get(lista.size()-1).contains("0.0")){
            lista.add(euroVenta.toString());
        }

    }

    /**
     * Con este método se consigue la cotización desde la página 
     * www.paralelohoy.com.ar, se parsea a un double, y se agruega a la
     * lista para ser enviada al servicio de Moneda
     * 
     * @param urls
     * @param lista
     */
    private static void getSegundaCotizacion(String[] urls, List<String> lista){
        Document doc = getHtmlDocument(urls[1]);

        String fila = doc.select(".tabla tbody td").last().text(); // .tabla tbody td
        Double euroVenta = 0.0;

        
        try {
            String euro = fila.substring(1);
            euroVenta = Double.parseDouble(euro);
            System.out.println("VENTA PARALELOHOY.COM.AR: " + euro);

        } catch (Exception e) {
            errorEuro = " Error al obtener el valor del euro --> PÁGINA WEB: " + urls[1];
            lista.add(errorEuro);
            
            // SI CAMBIÓ LA ESTRUCTURA DE LA PAGINA Y NO SE PUEDE CONVERTIR EL TEXTO A DOUBLE, SIGUE CON LA OTRA PAGINA
            getTerceraCotizacion(urls, lista);
            System.out.println("No se pudo leer el precio del euro de la página PARALELOHOY.COM.AR");
        }

        if(!lista.get(lista.size()-1).contains("0.0")){
            lista.add(euroVenta.toString());
        }
         
    }

    /**
     * Con este método se consigue la cotización desde la página 
     * www.oficialhoy.com.ar, se parsea a un double, y se agruega a la
     * lista para ser enviada al servicio de Moneda
     * 
     * @param urls
     * @param lista
     */
    private static void getTerceraCotizacion(String[] urls, List<String> lista){

        Document doc = getHtmlDocument(urls[2]);

        String fila = doc.select("#websendeos tbody tr").last().text(); // #websendeos tbody tr

        String[] result = fila.split(" ");

        String euro = result[3].substring(1);

        Double euroVenta = 0.0;

        try {
            euroVenta = Double.parseDouble(euro);
            System.out.println("VENTA OFICIALHOY.COM.AR: " + euroVenta);
        } catch (NumberFormatException e) {
            errorEuro = " Error al obtener el valor del euro --> PÁGINA WEB: " + urls[2];
            lista.add(errorEuro);     
            
            // SI CAMBIÓ LA ESTRUCTURA DE LA PAGINA Y NO SE PUEDE CONVERTIR EL TEXTO A DOUBLE, SIGUE CON LA OTRA PAGINA
            getSegundaCotizacion(urls, lista); 
            System.out.println("No se pudo leer el precio del euro de la página OFICIALHOY.COM.AR");
        }
        if(!lista.get(lista.size()-1).contains("0.0")){
            lista.add(euroVenta.toString());
        }
    }

    /**
     * Con este método compruebo el Status code de la respuesta que recibo al hacer
     * la petición EJM: 200 OK 300 Multiple Choices 301 Moved Permanently 305 Use
     * Proxy 400 Bad Request 403 Forbidden 404 Not Found 500 Internal Server Error
     * 502 Bad Gateway 503 Service Unavailable
     * 
     * @param url
     * @param lista
     * @return Status Code
     */
    public static Integer getStatusConnectionCode(String url , List<String> lista) {
        
        
        Response response = null;

        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
            System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
            
            String estado = "STATUS CODE: " + 404 + " --> PÁGINA WEB: " + url;
            lista.add(estado);
            return 404;
        }

        String estado = "STATUS CODE: " + response.statusCode() + " --> PÁGINA WEB: " + url;

        lista.add(estado);

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
