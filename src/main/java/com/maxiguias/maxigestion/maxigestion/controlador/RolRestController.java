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

import com.maxiguias.maxigestion.maxigestion.modelo.Rol;
import com.maxiguias.maxigestion.maxigestion.servicio.RolService;

@RestController
@RequestMapping("/api/roles")
public class RolRestController {

    @Autowired
    private RolService rolService;

    @GetMapping
    public ResponseEntity<List<Rol>> obtenerRoles() {
        List<Rol> roles = rolService.obtenerTodosLosRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rol> obtenerRolPorId(@PathVariable Long id) {
        Rol rol = rolService.obtenerPorId(id);
        if (rol == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rol);
    }

    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        Rol rolGuardado = rolService.guardarRol(rol);
        return ResponseEntity.ok(rolGuardado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rol> actualizarRol(@PathVariable Long id, @RequestBody Rol rol) {
        if (rolService.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        rol.setId(id);
        Rol rolActualizado = rolService.actualizarRol(rol);
        return ResponseEntity.ok(rolActualizado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarRol(@PathVariable Long id) {
        if (rolService.obtenerPorId(id) == null) {
            return ResponseEntity.notFound().build();
        }
        rolService.eliminarPorId(id);
        return ResponseEntity.ok("Rol eliminado exitosamente.");
    }
}