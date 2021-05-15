package com.conversorback.api.Controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.conversorback.api.model.Entities.Moneda;
import com.conversorback.api.services.IMonedaService;



@RestController
@RequestMapping("/api/moneda")
public class MonedaController {

    Logger log = LoggerFactory.getLogger(MonedaController.class);

    @Autowired
    private IMonedaService monedaService;

    @GetMapping("")
    public List<Moneda> listar(){

        log.info("MONEDAS LISTADAS CORRECTAMENTE");
        
        return monedaService.listarMonedas();
    }

    
    /*
     @PostMapping("/agregar")
    public String agregar(@RequestParam String tipo){
        System.out.println("################### moneda guardada!");
        return monedaService.agregarMoneda(tipo);
    }  
    */
    

    @GetMapping("/buscar/{id}")
    public Moneda buscarPorId(@PathVariable Long id){

        log.info("MONEDAS ENCONTRADA CORRECTAMENTE!");
       
        return monedaService.buscarMonedaPorId(id);
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id){

        log.info("MONEDAS ELIMINADA CORRECTAMENTE!");
        
        return monedaService.eliminarMonedaPorId(id);
    }

    @GetMapping("/listarUltimos")
    public List<Moneda> listarUltimos(){

        log.info("ULTIMAS COTIZACIONES LISTADAS CORRECTAMENTE!");

        return monedaService.buscarUltimosRegistro();
    }



    
}
