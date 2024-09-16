package com.codigo.ms_usuario.respository;

import com.codigo.ms_usuario.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository  extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByNumDoc(String dni);
    Optional<Usuario> findById(Integer id);
    Optional<Usuario> findByEmail(String email);
    Optional<List<Usuario>> findByEstado(Integer estado);
    boolean existsByNumDoc(String numDoc);
    boolean existsByEmail(String email);
    boolean existsById(Integer id);
}
