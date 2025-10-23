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
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Perfil;
import com.maxiguias.maxigestion.maxigestion.modelo.Rol;
import com.maxiguias.maxigestion.maxigestion.servicio.PerfilService;
import com.maxiguias.maxigestion.maxigestion.servicio.RolService;

@RestController
@RequestMapping("/api/perfiles")
public class PerfilController {

    private final PerfilService perfilService;
    private final RolService rolService;

    public PerfilController(PerfilService perfilService, RolService rolService) {
        this.perfilService = perfilService;
        this.rolService = rolService;
    }

    @GetMapping
    public ResponseEntity<List<Perfil>> listarPerfiles() {
        List<Perfil> perfiles = perfilService.obtenerPerfiles();
        return ResponseEntity.ok(perfiles);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<Rol>> obtenerRoles() {
        List<Rol> roles = rolService.listarRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Perfil> obtenerPerfilPorId(@PathVariable Long id) {
        Optional<Perfil> perfil = perfilService.obtenerPerfilPorId(id);
        if (perfil.isPresent()) {
            return ResponseEntity.ok(perfil.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Perfil> crearPerfil(@RequestBody Perfil perfil) {
        Perfil perfilGuardado = perfilService.guardarPerfil(perfil);
        return ResponseEntity.ok(perfilGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Perfil> actualizarPerfil(@PathVariable Long id, @RequestBody Perfil perfil) {
        Optional<Perfil> perfilExistente = perfilService.obtenerPerfilPorId(id);
        if (perfilExistente.isPresent()) {
            perfil.setId(id);
            Perfil perfilActualizado = perfilService.guardarPerfil(perfil);
            return ResponseEntity.ok(perfilActualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarPerfil(@PathVariable Long id) {
        Optional<Perfil> perfil = perfilService.obtenerPerfilPorId(id);
        if (perfil.isPresent()) {
            perfilService.eliminarPerfil(id);
            return ResponseEntity.ok("Perfil eliminado exitosamente");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}