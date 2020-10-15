package com.conversorback.api.utils;

import java.io.IOException;




import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;



public class JsoupHtml {

    /**
     * Con esta método compruebo el Status code de la respuesta que recibo al hacer la petición
     * EJM:
     * 		200 OK			300 Multiple Choices
     * 		301 Moved Permanently	305 Use Proxy
     * 		400 Bad Request		403 Forbidden
     * 		404 Not Found		500 Internal Server Error
     * 		502 Bad Gateway		503 Service Unavailable
     * @param url
     * @return Status Code
     */
    public static int getStatusConnectionCode(String url) {
            
        Response response = null;
        
        try {
        response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {
        System.out.println("Excepción al obtener el Status Code: " + ex.getMessage());
        }
        return response.statusCode();
    }

    /**
     * Con este método devuelvo un objeto de la clase Document con el contenido del
     * HTML de la web que me permitirá parsearlo con los métodos de la librelia JSoup
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


    
    
    public static Double obtenerEuro() throws IOException{
        final String url = "https://www.oficialhoy.com.ar/p/cotizacion-euro.html";
        
        Double euroVenta = 0.0;

        if(getStatusConnectionCode(url) == 200){
            Document doc = getHtmlDocument(url);
    
            String fila = doc.select("#websendeos tbody tr").last().text();
            
            String[] result = fila.split(" ");
            
            String euro = result[3].substring(1);

            try{
            euroVenta = Double.parseDouble(euro);
            
            System.out.println("VENTA: " + euroVenta);
            }catch(NumberFormatException e){
                System.out.println(e);
            }

        }else{

            System.out.println("El Status Code no es OK, es: "+getStatusConnectionCode(url));
        }
        return euroVenta;
    }
}
