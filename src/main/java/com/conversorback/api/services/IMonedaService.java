package com.conversorback.api.services;
import java.util.List;

import com.conversorback.api.model.Entities.Moneda;

public interface IMonedaService {

    public List<Moneda> listarMonedas();

    public String agregarMoneda(String tipo);

    public String eliminarMonedaPorId(Integer id);

    public Moneda buscarMonedaPorId(Integer id);


}
