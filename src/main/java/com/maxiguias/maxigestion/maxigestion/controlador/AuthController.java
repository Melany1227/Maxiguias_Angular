package com.maxiguias.maxigestion.maxigestion.controlador;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maxiguias.maxigestion.maxigestion.modelo.Usuario;
import com.maxiguias.maxigestion.maxigestion.repositorio.UsuarioRepository;
import com.maxiguias.maxigestion.maxigestion.util.JwtUtil;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombreUsuario(request.getNombreUsuario());
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            
            // Verificar que el usuario tenga permisos para autenticarse
            String tipoUsuario = usuario.getTipoUsuario().getNombre();
            if (!tipoUsuario.equals("ADMIN") && !tipoUsuario.equals("JURIDICO")) {
                LoginResponse response = new LoginResponse();
                response.setSuccess(false);
                response.setMessage("Este tipo de usuario no tiene permisos para acceder al sistema");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Verificar contraseña
            if (passwordEncoder.matches(request.getContrasena(), usuario.getContrasena())) {
                String token = jwtUtil.generateToken(
                    usuario.getNombreUsuario(),
                    usuario.getTipoUsuario().getNombre(),
                    usuario.getPerfil().getNombrePerfil()
                );
                
                LoginResponse response = new LoginResponse();
                response.setSuccess(true);
                response.setMessage("Login exitoso");
                response.setUsuario(usuario);
                response.setToken(token);
                return ResponseEntity.ok(response);
            }
        }
        
        LoginResponse response = new LoginResponse();
        response.setSuccess(false);
        response.setMessage("Credenciales inválidas");
        return ResponseEntity.badRequest().body(response);
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateResponse> validateToken(@RequestBody ValidateRequest request) {
        try {
            String username = jwtUtil.extractUsername(request.getToken());
            String role = jwtUtil.extractRole(request.getToken());
            String perfil = jwtUtil.extractPerfil(request.getToken());
            
            if (jwtUtil.validateToken(request.getToken(), username)) {
                ValidateResponse response = new ValidateResponse();
                response.setValid(true);
                response.setUsername(username);
                response.setRole(role);
                response.setPerfil(perfil);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            // Token inválido
        }
        
        ValidateResponse response = new ValidateResponse();
        response.setValid(false);
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
        private String token;
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class ValidateRequest {
        private String token;
        
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class ValidateResponse {
        private boolean valid;
        private String username;
        private String role;
        private String perfil;
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public String getPerfil() { return perfil; }
        public void setPerfil(String perfil) { this.perfil = perfil; }
    }
}