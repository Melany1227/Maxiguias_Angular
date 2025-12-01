package com.maxiguias.maxigestion.maxigestion.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Perfil;
import com.maxiguias.maxigestion.maxigestion.modelo.TipoUsuario;
import com.maxiguias.maxigestion.maxigestion.servicio.PerfilService;
import com.maxiguias.maxigestion.maxigestion.servicio.TipoUsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AuthorityController {

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private TipoUsuarioService tipoUsuarioService;

    @GetMapping("/perfiles")
    public ResponseEntity<List<Perfil>> obtenerPerfiles() {
        List<Perfil> perfiles = perfilService.obtenerPerfiles();
        return ResponseEntity.ok(perfiles);
    }

    @GetMapping("/tipos-usuario")
    public ResponseEntity<List<TipoUsuario>> obtenerTiposUsuario() {
        List<TipoUsuario> tipos = tipoUsuarioService.obtenerTiposUsuario();
        return ResponseEntity.ok(tipos);
    }

    @GetMapping("/perfiles/permitidos")
    public ResponseEntity<PerfilesPermitidos> obtenerPerfilesPermitidos() {
        PerfilesPermitidos perfiles = new PerfilesPermitidos();
        
        perfiles.ADMIN = List.of("DEVELOPER", "ADMIN");
        perfiles.juridico = List.of("ALMACEN", "REPRESENTANTE", "NN_GENERAL");
        perfiles.natural = List.of();
        
        return ResponseEntity.ok(perfiles);
    }

    public static class PerfilesPermitidos {
        public List<String> ADMIN;
        public List<String> juridico;
        public List<String> natural;
    }
}