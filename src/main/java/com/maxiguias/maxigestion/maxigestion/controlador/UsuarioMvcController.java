package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.maxiguias.maxigestion.maxigestion.modelo.Usuario;
import com.maxiguias.maxigestion.maxigestion.repositorio.CiudadRepository;
import com.maxiguias.maxigestion.maxigestion.repositorio.DepartamentoRepository;
import com.maxiguias.maxigestion.maxigestion.servicio.PerfilService;
import com.maxiguias.maxigestion.maxigestion.servicio.TipoUsuarioService;
import com.maxiguias.maxigestion.maxigestion.servicio.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioMvcController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PerfilService perfilService;

    @Autowired
    private TipoUsuarioService tipoUsuarioService;

    @Autowired
    private DepartamentoRepository departamentoRepository;

    @Autowired
    private CiudadRepository ciudadRepository;

    @GetMapping
    public ResponseEntity<Page<Usuario>> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String buscar) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("documento").ascending());
        Page<Usuario> usuariosPage;
        
        if (buscar != null && !buscar.trim().isEmpty()) {
            usuariosPage = usuarioService.buscarUsuariosPaginados(buscar.trim(), pageable);
        } else {
            usuariosPage = usuarioService.obtenerUsuariosPaginados(pageable);
        }
        
        return ResponseEntity.ok(usuariosPage);
    }

    @GetMapping("/formulario-data")
    public ResponseEntity<FormularioData> obtenerDatosFormulario() {
        FormularioData data = new FormularioData();
        data.setTiposUsuario(tipoUsuarioService.obtenerTiposUsuario());
        data.setPerfiles(perfilService.obtenerPerfiles());
        data.setDepartamentos(departamentoRepository.findAll());
        data.setCiudades(ciudadRepository.findAll());
        return ResponseEntity.ok(data);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        if (usuario != null) {
            usuario.setContrasena("");
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.notFound().build();
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
        return ResponseEntity.ok("Usuario eliminado exitosamente");
    }

    public static class FormularioData {
        private List<?> tiposUsuario;
        private List<?> perfiles;
        private List<?> departamentos;
        private List<?> ciudades;
        
        public List<?> getTiposUsuario() { return tiposUsuario; }
        public void setTiposUsuario(List<?> tiposUsuario) { this.tiposUsuario = tiposUsuario; }
        public List<?> getPerfiles() { return perfiles; }
        public void setPerfiles(List<?> perfiles) { this.perfiles = perfiles; }
        public List<?> getDepartamentos() { return departamentos; }
        public void setDepartamentos(List<?> departamentos) { this.departamentos = departamentos; }
        public List<?> getCiudades() { return ciudades; }
        public void setCiudades(List<?> ciudades) { this.ciudades = ciudades; }
    }
}