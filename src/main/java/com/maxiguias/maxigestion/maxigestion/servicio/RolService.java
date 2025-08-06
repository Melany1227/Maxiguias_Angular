package com.maxiguias.maxigestion.maxigestion.servicio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maxiguias.maxigestion.maxigestion.modelo.Rol;
import com.maxiguias.maxigestion.maxigestion.repositorio.RolRepository;

@Service
public class RolService {

    private final RolRepository rolRepository;

    public RolService(RolRepository rolRepository) {
        this.rolRepository = rolRepository;
    }

    public List<Rol> obtenerTodosLosRoles() {
        return rolRepository.findAll();
    }

    public Rol obtenerPorId(Long id) {
        return rolRepository.findById(id).orElse(null);
    }

    public Rol guardarRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public Rol actualizarRol(Rol rol) {
        return rolRepository.save(rol);
    }

    public void eliminarPorId(Long id) {
        rolRepository.deleteById(id);
    }
}