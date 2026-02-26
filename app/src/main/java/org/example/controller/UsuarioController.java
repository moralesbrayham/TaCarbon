package org.example.controller;

import org.example.model.Usuario;
import org.example.service.UsuarioService;
import org.example.model.LoginRequest;
import org.example.dto.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;


    // Obtener todos los usuarios
    @GetMapping
    public List<Usuario> obtenerUsuarios() {
        return usuarioService.obtenerTodosLosUsuarios();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Registrar un nuevo usuario
    @PostMapping
    public ResponseEntity<Usuario> registrarUsuario(@RequestBody Usuario usuario) {
        Usuario nuevoUsuario = usuarioService.registrarUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    // Autenticación de usuario
    @PostMapping("/login")
    public ResponseEntity<String> autenticarUsuario(@RequestParam String correo, @RequestParam String contraseña) {
        boolean autenticado = usuarioService.autenticarUsuario(correo, contraseña);
        return autenticado ? ResponseEntity.ok("Autenticación exitosa") : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
    }
    
    // ✅ Nuevo login para Android (usa JSON)
    @PostMapping("/login/app")
    public ResponseEntity<?> loginDesdeApp(@RequestBody LoginRequest request) {
        Optional<Usuario> usuarioOptional = usuarioService.obtenerPorCorreo(request.getCorreo());

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();

            if (passwordEncoder.matches(request.getContraseña(), usuario.getContraseña())) {
                LoginResponse response = new LoginResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getCorreo(),
                    usuario.getRol().toString()
                );
                return ResponseEntity.ok(response); // ✅ Solo se devuelve lo necesario
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
    }


    // Eliminar un usuario por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}