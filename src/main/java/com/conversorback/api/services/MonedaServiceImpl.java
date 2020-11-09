package com.conversorback.api.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        try {
          
            List<String> lista = JsoupHtml.obtenerEuro();

             if(lista.size() > 2){

                String texto = "";

                for (int i = 0; i < lista.size()-1; i++) {
                    if(!lista.get(i).contains("200")){
                        texto += "El estado de la página es: \n - " + lista.get(i) + "\n";
                    }
                }

                mailService.enviarMail("mfuhr91@gmail.com", 
                "ERROR EN SISTEMA CONVERPACK", texto);
            
            }
            moneda.setTipo("euro_blue");
            Double precio = Double.parseDouble(lista.get(lista.size()-1).toString());
            moneda.setValor(precio);
        } catch (IOException e) {
            return "No se pudo guardar el euro blue";
        }
        
        
        if(moneda.getValor() != 0.0){

            monedaRepo.save(moneda);
            return "Moneda id: " + moneda.getTipo() + " - " + moneda.getValor() + " guardado con exito";
        }else{
            return "No se guardo la moneda en la bd";
        }
    }

    @Override
    public String guardarBitcoin() {
        Moneda moneda = new Moneda();
        try {

            List<String> lista = GetRequestBitso.getRequest();
            if(lista.size() > 1){
                String texto = "";
                for (int i = 0; i < lista.size()-1; i++) {
                    if(!lista.get(i).contains("200")){
                        texto = "El estado de la página es: \n - " + lista.get(i) + "\n";
                    }
                }
                mailService.enviarMail("mfuhr91@gmail.com", 
                "ERROR EN SISTEMA CONVERPACK", texto);

            
            }


            moneda.setTipo("bitcoin");
            Double precio = Double.parseDouble(lista.get(lista.size()-1).toString());
            moneda.setValor(precio);
        } catch (IOException e) {
            return "No se pudo guardar el bitcoin";
        }
        if(moneda.getValor() != 0.0){

            monedaRepo.save(moneda);
            return "Moneda id: " + moneda.getTipo() + " - " + moneda.getValor() + " guardado con exito";
        }else{
            return "No se guardo la moneda en la bd";
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
