package com.maxiguias.maxigestion.maxigestion.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Usuario;
import com.maxiguias.maxigestion.maxigestion.repositorio.UsuarioRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(request.getNombreUsuario());
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar contraseña
            if (passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setMessage("Login exitoso");
                response.setUsuario(usuario);
                return ResponseEntity.ok(response);
            }
        }
        
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setMessage("Credenciales inválidas");
        return ResponseEntity.badRequest().body(response);
    }

    public static class LoginRequest {
        private String nombreUsuario;
        private String contrasena;
        
        public String getNombreUsuario() { return nombreUsuario; }
        public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
        public String getContrasena() { return contrasena; }
        public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    }

    public static class LoginResponse {
        private boolean success;
        private String message;
        private Usuario usuario;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    }
}