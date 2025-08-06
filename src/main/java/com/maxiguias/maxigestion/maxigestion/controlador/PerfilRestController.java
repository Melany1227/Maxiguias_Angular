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

import com.maxiguias.maxigestion.maxigestion.modelo.Perfil;
import com.maxiguias.maxigestion.maxigestion.servicio.PerfilService;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilRestController {

    @Autowired
    private PerfilService perfilService;

    @GetMapping
    public ResponseEntity<List<Perfil>> obtenerPerfiles() {
        List<Perfil> perfiles = perfilService.obtenerPerfiles();
        return ResponseEntity.ok(perfiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Perfil> obtenerPerfilPorId(@PathVariable Long id) {
        Perfil perfil = perfilService.obtenerPorId(id);
        if (perfil == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(perfil);
    }

    @PostMapping
    public ResponseEntity<Perfil> crearPerfil(@RequestBody Perfil perfil) {
        Perfil perfilGuardado = perfilService.guardarPerfil(perfil);
        return ResponseEntity.ok(perfilGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Perfil> actualizarPerfil(@PathVariable Long id, @RequestBody Perfil perfil) {
        if (perfilService.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        perfil.setId(id);
        Perfil perfilActualizado = perfilService.actualizarPerfil(perfil);
        return ResponseEntity.ok(perfilActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPerfil(@PathVariable Long id) {
        if (perfilService.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        perfilService.eliminarPorId(id);
        return ResponseEntity.ok("Perfil eliminado exitosamente.");
    }
}