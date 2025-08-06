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

import com.maxiguias.maxigestion.maxigestion.modelo.Usuario;
import com.maxiguias.maxigestion.maxigestion.servicio.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> obtenerUsuarios() {
        List<Usuario> usuarios = usuarioService.obtenerTodosLosUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PostMapping
    public ResponseEntity<String> crearUsuario(@RequestBody Usuario usuario) {
        String resultado = usuarioService.crearUsuario(usuario);
        return ResponseEntity.ok(resultado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> actualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuario) {
        usuario.setDocumento(id);
        String resultado = usuarioService.actualizarUsuario(usuario);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarPorId(id);
        return ResponseEntity.ok("Usuario eliminado exitosamente.");
    }
}