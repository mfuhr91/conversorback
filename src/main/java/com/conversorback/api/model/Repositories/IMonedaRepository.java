package com.conversorback.api.model.Repositories;

import java.util.List;

import com.conversorback.api.model.Entities.Moneda;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface IMonedaRepository extends JpaRepository<Moneda, Long> {
    
    List<Moneda> findByTipo(String tipo);
    
}
