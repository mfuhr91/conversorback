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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
public class MonedaServiceImpl implements IMonedaService {

    Logger log = LoggerFactory.getLogger(MonedaServiceImpl.class);

    @Autowired
    private IMonedaRepository monedaRepo;

    @Autowired
    private IMailService mailService;

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
    @Scheduled(cron="0 0 * * * *", zone = "America/Argentina/Ushuaia") // CRON QUE SE EJECUTA A CADA HORA
    public void guardarCotizacionAuto() {
        System.out.println("GUARDADO AUTOMATICO");
        
        guardarEuro();
        guardarBitcoin();
        borrarMasAntiguo(); 
    }


    @Override
    public String guardarEuro() {
        Moneda moneda = new Moneda();
        moneda.setTipo("euro_blue");
        String result = "";
        try {
          
            List<Map<String,String>> lista = JsoupHtml.obtenerEuro();

            result = guardarMoneda(lista, moneda);
        
        } catch (IOException e) {
            log.error("No se pudo guardar el " + moneda.getTipo() + " ERROR: " + e );
            return "No se pudo guardar el " + moneda.getTipo() + " ERROR: " + e;
        }
        return result;
    }

    @Override
    public String guardarBitcoin() {
        Moneda moneda = new Moneda();
        moneda.setTipo("bitcoin");
        String result = "";
        try {
            
            List<Map<String,String>> lista = GetRequestBitso.getRequest();
            
            result = guardarMoneda(lista, moneda);
        } catch (IOException e) {
            log.error("No se pudo guardar el " + moneda.getTipo() + " ERROR: " + e );
            return "No se pudo guardar el " + moneda.getTipo() + " ERROR: " + e;
        }
       return result;
    }

    public String guardarMoneda(List<Map<String,String>> lista,  Moneda moneda){

        String texto = "";
        for( Map<String, String> mapa : lista){

            if(mapa.containsKey("error")){
                texto += "El estado de la página es: \n - " + mapa.get("error") + "\n";
            }
            if(mapa.containsKey("estado")){
                texto += "El estado de la página es: \n - " + mapa.get("estado") + "\n";                    
            }
            if(mapa.containsKey("precio")){

                Double precio = Double.parseDouble(mapa.get("precio"));
                moneda.setValor(precio);
            }     
        }

        if(texto != ""){
            mailService.enviarMail("mfuhr91@gmail.com", 
            "ERROR EN SISTEMA CONVERPACK", texto);
        }
        if(moneda.getValor() != 0.0){

            monedaRepo.save(moneda);
            log.info("Moneda id: " + moneda.getTipo() + " - " + moneda.getValor() + " guardado con exito");
            return "Moneda id: " + moneda.getTipo() + " - " + moneda.getValor() + " guardado con exito";
        }else{
            log.error("No se guardo el " + moneda.getTipo() + " en la bd");
            return "No se guardo el " + moneda.getTipo() + " en la bd";
        }
    }

    @Override
    public void borrarMasAntiguo() {
        
        List<Moneda> listaEuroBlue = monedaRepo.findByTipo("euro_blue");
        List<Moneda> listaBitcoin = monedaRepo.findByTipo("bitcoin");
        Long medioMes = 1296000000L; 
        Date fechaActual = new Date();
        if(listaEuroBlue.size() > 360 ){
            for (Moneda moneda : listaEuroBlue) {
                Long tiempo = fechaActual.getTime() - moneda.getFecha().getTime();
                if(tiempo > medioMes){
                    monedaRepo.deleteById(moneda.getId());
                }
            }
        }
        if(listaBitcoin.size() > 360){
            for (Moneda moneda : listaBitcoin) {
                Long tiempo = fechaActual.getTime() - moneda.getFecha().getTime();
                if(tiempo > medioMes){
                    monedaRepo.deleteById(moneda.getId());
                }
            }
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
