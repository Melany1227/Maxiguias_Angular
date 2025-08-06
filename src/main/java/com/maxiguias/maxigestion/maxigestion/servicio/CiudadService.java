package com.maxiguias.maxigestion.maxigestion.servicio;

import java.util.List;

import org.springframework.stereotype.Service;

import com.maxiguias.maxigestion.maxigestion.modelo.Ciudad;
import com.maxiguias.maxigestion.maxigestion.repositorio.CiudadRepository;

@Service
public class CiudadService {

    private final CiudadRepository ciudadRepository;

    public CiudadService(CiudadRepository ciudadRepository) {
        this.ciudadRepository = ciudadRepository;
    }

    public List<Ciudad> obtenerTodasLasCiudades() {
        return ciudadRepository.findAll();
    }

    public Ciudad obtenerPorId(Long id) {
        return ciudadRepository.findById(id).orElse(null);
    }

    public Ciudad guardarCiudad(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }

    public Ciudad actualizarCiudad(Ciudad ciudad) {
        return ciudadRepository.save(ciudad);
    }

    public void eliminarPorId(Long id) {
        ciudadRepository.deleteById(id);
    }
}