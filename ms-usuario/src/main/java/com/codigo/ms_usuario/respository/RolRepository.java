package com.codigo.ms_usuario.respository;

import com.codigo.ms_usuario.entities.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol,Long> {
    Optional<Rol> findByNombreRol(String rol);
}
