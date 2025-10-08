package com.bancoagil.auth_service.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.bancoagil.auth_service.Entities.Empresa;

public interface IEmpresaRepository extends JpaRepository<Empresa, Integer> {

    // 🛑 CORRECCIÓN: Usar el nombre de la propiedad de la entidad ('ruc')
    boolean existsByRuc(String ruc); 
    
    // Si realmente quieres usar 'Nit' en el código, puedes usar:
    /*
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN TRUE ELSE FALSE END FROM Empresa e WHERE e.ruc = :nit")
    boolean existsByNit(@Param("nit") String nit);
    */
}