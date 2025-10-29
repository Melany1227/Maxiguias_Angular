package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Ciudad;
import com.maxiguias.maxigestion.maxigestion.servicio.CiudadService;

@RestController
@RequestMapping("/api/ciudades")
public class CiudadController {

    private final CiudadService ciudadService;

    public CiudadController(CiudadService ciudadService) {
        this.ciudadService = ciudadService;
    }

    @GetMapping
    public ResponseEntity<List<Ciudad>> listarCiudades(@RequestParam(required = false) Integer departamentoId) {
        List<Ciudad> ciudades;
        if (departamentoId != null) {
            ciudades = ciudadService.listarCiudadesPorDepartamento(departamentoId);
        } else {
            ciudades = ciudadService.listarCiudades();
        }
        return ResponseEntity.ok(ciudades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> obtenerCiudadPorId(@PathVariable int id) {
        Optional<Ciudad> ciudad = ciudadService.obtenerCiudadPorId(id);
        if (ciudad.isPresent()) {
            return ResponseEntity.ok(ciudad.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Ciudad> crearCiudad(@RequestBody Ciudad ciudad) {
        Ciudad ciudadGuardada = ciudadService.guardarCiudad(ciudad);
        return ResponseEntity.ok(ciudadGuardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> actualizarCiudad(@PathVariable Long id, @RequestBody Ciudad ciudad) {
        Optional<Ciudad> ciudadExistente = ciudadService.obtenerCiudadPorId(id.intValue());
        if (ciudadExistente.isPresent()) {
            ciudad.setId(id.intValue());
            Ciudad ciudadActualizada = ciudadService.guardarCiudad(ciudad);
            return ResponseEntity.ok(ciudadActualizada);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCiudad(@PathVariable int id) {
        Optional<Ciudad> ciudad = ciudadService.obtenerCiudadPorId(id);
        if (ciudad.isPresent()) {
            ciudadService.eliminarCiudad(id);
            return ResponseEntity.ok("Ciudad eliminada exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}