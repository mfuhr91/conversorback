package com.conversorback.api.utils;

public enum PaginaWeb {
    

    //URL1("https://www.paralelohoy.com.ar/p/cotizacion-euro-hoy-argentina.html"),
    URL1("https://www.paralelohoy.com.ar/p/cotizacion-euro-hoy-argentina.html"),
    URL2("https://www.euroblue.com.ar/"),
    URL3("https://www.precioeuroblue.com.ar");

    private String url;
    
    private PaginaWeb( String url ) {
        this.url = url;
    }

    public void setUrl( String url ){
        this.url = url;
    }

    public String getUrl(){
        return this.url;
    }
    
}
