package com.conversorback.api.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class JsoupHtml {

    static Logger log = LoggerFactory.getLogger(JsoupHtml.class);

    private static String error;
    static List<Map<String, String>> lista = new ArrayList<Map<String, String>>();

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
    public static List<Map<String, String>> obtenerEuro() throws IOException {  

        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        
        Boolean hayPrecio = false;
        for(PaginaWeb pagina : PaginaWeb.values()){
            if(hayPrecio) {
                break;
            }
            // SI LA PÁGINA ESTA CAIDA O CAMBIÓ SU URL, NO INGRESA AL IF Y LEE LA SIGUIENTE PÁGINA
            if( getStatusConnectionCode( pagina.getUrl() ) == 200 ) {
                lista = getCotizacion(pagina.getUrl());

                for( Map<String, String> mapa : lista){
                    if(mapa.containsKey("estado") && !mapa.get("estado").contains("200")){
                        result.add(mapa);
                    }
                    if(mapa.containsKey("error")){
                        result.add(mapa);
                    }
                    if(mapa.containsKey("precio")){
                        result.add(mapa);
                        hayPrecio = true;
                        break;
                    }
                }
            } else {
                result = lista;
            }

        }
          
        return result;
    }

    /**
     * Con este método se consigue la cotización desde las distintas páginas,
     *  se parsea a un double, y se agrega a la
     * lista para ser enviada al servicio de Moneda
     * 
     * @param urls
     * @param lista
     */
    private static List<Map<String, String>> getCotizacion(String url){

        Map<String, String> mapa = new HashMap<String, String>();
        
        Document doc = getHtmlDocument(url);
        String fila = "";
        

        if ( url == PaginaWeb.URL1.getUrl() ){
            try{
                fila = doc.select(".tabla tbody td").last().text(); // .tabla tbody td
            }catch(NullPointerException e){
                System.out.println(e);
            }   
            
            try {
                String euro = fila.substring(1);
              
                log.info("VENTA " + url + ": " + euro);
                
                mapa.put("precio", euro);
                lista.add(mapa);
                return lista;
                
            } catch (Exception e) {
                error = " Error al obtener el valor del euro --> PÁGINA WEB: " + url;
                
                mapa.put("error", error);
                lista.add(mapa);
                
                
                // SI CAMBIÓ LA ESTRUCTURA DE LA PAGINA Y NO SE PUEDE CONVERTIR EL TEXTO A DOUBLE, SIGUE CON LA OTRA PAGINA
                log.error(error);
                getCotizacion(url);
            }
             
        } else if ( url == PaginaWeb.URL2.getUrl() ){
          
            try{
                fila = doc.select(".elementor-column table").first().select("tbody td").last().text(); // .tabla tbody td

                System.out.println(fila.toString());
                
            }catch(NullPointerException e){
                System.out.println(e);
            }
            
            try {
                String euro = fila; 
                log.info("VENTA " + url + ": " + euro);
            
                mapa.put("precio", euro);
                lista.add(mapa);
                return lista;
            
            } catch (Exception e) {
                error = " Error al obtener el valor del euro --> PÁGINA WEB: " + url;
                mapa.put("error", error);
                
                lista.add(mapa);
                // SI CAMBIÓ LA ESTRUCTURA DE LA PAGINA Y NO SE PUEDE CONVERTIR EL TEXTO A DOUBLE, SIGUE CON LA OTRA PAGINA
                log.error(error);
                getCotizacion(url);

            }
            
            lista.add(mapa);
            return lista;
        } else {
           
            try{
                fila = doc.select(".content_reference span").last().text(); // .tabla tbody td
            }catch(NullPointerException e){
                System.out.println(e);
            }
            
            try {
                String euro = fila;
               
                log.info("VENTA " + url + ": " + euro);
               
                mapa.put("precio", euro);

                lista.add(mapa);
                return lista;

            } catch (Exception e) {
                error = " Error al obtener el valor del euro --> PÁGINA WEB: " + url;
                mapa.put("error", error);
                
                log.error(error);
                
                lista.add(mapa);
                return lista;
            }   
           
        }
        mapa.put("error", "No se pudo leer el precio del euro de ninguna página web");
        lista.add(mapa);
        return lista;
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
    public static Integer getStatusConnectionCode(String url) {

        Map<String, String> mapa = new HashMap<String, String>();
        
        Response response = null;

        try {
            response = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(100000).ignoreHttpErrors(true).execute();
        } catch (IOException ex) {

            String estado = "STATUS CODE: " + 404 + " --> PÁGINA WEB: " + url;
            log.error(estado);
           
            mapa.put("estado", estado);
            lista.add(mapa);

            return 404;
        }

        String estado = "STATUS CODE: " + response.statusCode() + " --> PÁGINA WEB: " + url;

        mapa.put("estado", estado);
        lista.add(mapa);

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
            log.error("Excepción al obtener el HTML de la página" + ex.getMessage());
        }
        return doc;
    }

}
