package com.conversorback.api.services;
import java.util.List;

import com.conversorback.api.model.Entities.Moneda;

public interface IMonedaService {

    public List<Moneda> listarMonedas();

    public String agregarMoneda(String tipo);

    public String eliminarMonedaPorId(Long id);

    public Moneda buscarMonedaPorId(Long id);

    public void guardarCotizacionAuto();

    public void borrarMasAntiguo();

    public String guardarEuro();

    public String guardarBitcoin();

    public List<Moneda> buscarUltimosRegistro();

}
