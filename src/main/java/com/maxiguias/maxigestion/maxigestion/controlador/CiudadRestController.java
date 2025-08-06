package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Ciudad;
import com.maxiguias.maxigestion.maxigestion.servicio.CiudadService;

@RestController
@RequestMapping("/api/ciudades")
public class CiudadRestController {

    @Autowired
    private CiudadService ciudadService;

    @GetMapping
    public ResponseEntity<List<Ciudad>> obtenerCiudades() {
        List<Ciudad> ciudades = ciudadService.obtenerTodasLasCiudades();
        return ResponseEntity.ok(ciudades);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ciudad> obtenerCiudadPorId(@PathVariable Long id) {
        Ciudad ciudad = ciudadService.obtenerPorId(id);
        if (ciudad == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(ciudad);
    }

    @PostMapping
    public ResponseEntity<Ciudad> crearCiudad(@RequestBody Ciudad ciudad) {
        Ciudad ciudadGuardada = ciudadService.guardarCiudad(ciudad);
        return ResponseEntity.ok(ciudadGuardada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Ciudad> actualizarCiudad(@PathVariable Long id, @RequestBody Ciudad ciudad) {
        if (ciudadService.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        ciudad.setId(id.intValue());
        Ciudad ciudadActualizada = ciudadService.actualizarCiudad(ciudad);
        return ResponseEntity.ok(ciudadActualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarCiudad(@PathVariable Long id) {
        if (ciudadService.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        ciudadService.eliminarPorId(id);
        return ResponseEntity.ok("Ciudad eliminada exitosamente.");
    }
}