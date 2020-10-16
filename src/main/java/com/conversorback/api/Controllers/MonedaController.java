package com.conversorback.api.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.conversorback.api.model.Entities.Moneda;
import com.conversorback.api.services.IMonedaService;



@RestController
@RequestMapping("/api/moneda")
public class MonedaController {

    @Autowired
    private IMonedaService monedaService;

    @GetMapping("")
    public List<Moneda> listar(){
        System.out.println("################### monedas listadas!");
        
        return monedaService.listarMonedas();
    }

    /* @PostMapping("/agregar")
    public String agregar(@RequestParam String tipo){
        System.out.println("################### moneda guardada!");
        return monedaService.agregarMoneda(tipo);
    } */

    @GetMapping("/buscar/{id}")
    public Moneda buscarPorId(@PathVariable Long id){
        System.out.println("################### moneda encontrada!");
        return monedaService.buscarMonedaPorId(id);
    }

    @DeleteMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id){
        System.out.println("################### monedas eliminada!");
        
        return monedaService.eliminarMonedaPorId(id);
    }

    @GetMapping("/listarUltimos")
    public List<Moneda> listarUltimos(){
        System.out.println("################### monedas listadas!");
        return monedaService.buscarUltimosRegistro();
    }



    
}
