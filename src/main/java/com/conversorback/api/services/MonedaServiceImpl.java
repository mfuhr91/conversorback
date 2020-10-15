package com.conversorback.api.services;

import java.io.IOException;
import java.util.List;

import com.conversorback.api.model.Entities.Moneda;
import com.conversorback.api.model.Repositories.IMonedaRepository;
import com.conversorback.api.utils.JsoupHtml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MonedaServiceImpl implements IMonedaService {

    @Autowired
    private IMonedaRepository monedaRepo;

    @Override
    public List<Moneda> listarMonedas() {
        return monedaRepo.findAll();
    }

    @Override
    public String agregarMoneda(String tipo) {
        Moneda moneda = new Moneda();
        moneda.setTipo(tipo);
        if(tipo.equals("euro_blue")){
            try {
                moneda.setValor(JsoupHtml.obtenerEuro());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            moneda.setValor(GetRequestBitso.getRequest());
        }
        monedaRepo.save(moneda);
        return "Moneda id: " + moneda.getId() + " guardada con exito";

    }

    @Override
    public String eliminarMonedaPorId(Integer id) {
        monedaRepo.deleteById(id);
        return "Moneda id: " + id + " eliminada con exito";

    }

    @Override
    public Moneda buscarMonedaPorId(Integer id) {
        return monedaRepo.findById(id).orElse(null);
    }

    
}
