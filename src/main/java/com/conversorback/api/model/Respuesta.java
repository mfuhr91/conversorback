package com.conversorback.api.model;

import java.util.List;

import lombok.Data;
// CLASE DE MAPEO JSON DESDE https://api.bitso.com/v3/trades/?book=btc_ars
@Data
public class Respuesta {
    private Boolean success;
    private List<Book> payload;

    public Respuesta(){}

    public Respuesta(Boolean success, List<Book> payload) {
        this.success = success;
        this.payload = payload;
    }

}
