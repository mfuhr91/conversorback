package com.conversorback.api.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.conversorback.api.model.Entities.Moneda;
import com.conversorback.api.model.Repositories.IMonedaRepository;
import com.conversorback.api.utils.GetRequestBitso;
import com.conversorback.api.utils.JsoupHtml;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
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
        borrarMasAntiguo();
        if(tipo.equals("euro_blue")){
            return guardarEuro();
        }else{
            return guardarBitcoin();
        }
    
    }

    @Override
    public String eliminarMonedaPorId(Long id) {
        monedaRepo.deleteById(id);
        return "Moneda id: " + id + " eliminada con exito";

    }

    @Override
    public Moneda buscarMonedaPorId(Long id) {
        return monedaRepo.findById(id).orElse(null);
    }

    @Override
    @Scheduled(cron="0 0 0,6,12,18 * * *", zone = "America/Argentina/Ushuaia") // cron="0 0 0,6,12,18 * * *"
    public void guardarCotizacionAuto() {
        System.out.println("GUARDADO AUTOMATICO");
        
        guardarEuro();
        guardarBitcoin();
    }

    @Override
    public void borrarMasAntiguo() {
        
        List<Moneda> listaEuroBlue = monedaRepo.findByTipo("euro_blue");
        List<Moneda> listaBitcoin = monedaRepo.findByTipo("bitcoin");
        Long unMes = 2678400000L; 
        Date fechaActual = new Date();
        if(listaEuroBlue.size() > 120 ){
            for (Moneda moneda : listaEuroBlue) {
                Long tiempo = fechaActual.getTime() - moneda.getFecha().getTime();
                if(tiempo > unMes){
                    monedaRepo.deleteById(moneda.getId());
                }
            }
        }
        if(listaBitcoin.size() > 120){
            for (Moneda moneda : listaBitcoin) {
                Long tiempo = fechaActual.getTime() - moneda.getFecha().getTime();
                if(tiempo > unMes){
                    monedaRepo.deleteById(moneda.getId());
                }
            }
        }
    }

    @Override
    public String guardarEuro() {
        Moneda moneda = new Moneda();
        try {
            Map<String,String> euroMapa = JsoupHtml.obtenerEuro();
            moneda.setTipo(euroMapa.get("tipo").toString());
            Double precio = Double.parseDouble(euroMapa.get("valor").toString());
            moneda.setValor(precio);
        } catch (IOException e) {
            return "No se pudo guardar el euro blue";
        }
        if(moneda.getValor() != 0.0){

            monedaRepo.save(moneda);
            borrarMasAntiguo();
            return "Moneda id: " + moneda.getTipo() + " - " + moneda.getValor() + " guardado con exito";
        }else{
            return "No se guardo la moneda en la bd";
        }
    }

    @Override
    public String guardarBitcoin() {
        Moneda moneda = new Moneda();
        try {
            Map<String,String> bitcoinMapa = GetRequestBitso.getRequest();
            moneda.setTipo(bitcoinMapa.get("tipo").toString());
            Double precio = Double.parseDouble(bitcoinMapa.get("valor").toString());
            moneda.setValor(precio);
        } catch (IOException e) {
            return "No se pudo guardar el bitcoin";
        }
        if(moneda.getValor() != 0.0){

            monedaRepo.save(moneda);
            borrarMasAntiguo();
            return "Moneda id: " + moneda.getTipo() + " - " + moneda.getValor() + " guardado con exito";
        }else{
            return "No se guardo la moneda en la bd";
        }
    }
    
    @Override
    public List<Moneda> buscarUltimosRegistro() {
        
        List<Moneda> euros = monedaRepo.findByTipo("euro_blue");
        List<Moneda> bitcoins = monedaRepo.findByTipo("bitcoin");
        List<Moneda> monedasEncontradas = new ArrayList<>();
        Moneda ultimoEuroBlue = null;
        Moneda ultimoBitcoin = null;
        for (Moneda euro : euros) {
            ultimoEuroBlue = euro;
            for (Moneda euroNext : euros) {
        
                if(ultimoEuroBlue.getFecha().before(euroNext.getFecha())){
                    ultimoEuroBlue = euroNext;
                } 
                
            }
        }
        for (Moneda bitcoin : bitcoins) {    
            ultimoBitcoin = bitcoin;
            for (Moneda bitNext : bitcoins) {
          
                if(ultimoBitcoin.getFecha().before(bitNext.getFecha())){
                    ultimoBitcoin = bitNext;
                }
            }
        }
        
        monedasEncontradas.add(ultimoEuroBlue);
        monedasEncontradas.add(ultimoBitcoin);
        return monedasEncontradas;
    } 
}
