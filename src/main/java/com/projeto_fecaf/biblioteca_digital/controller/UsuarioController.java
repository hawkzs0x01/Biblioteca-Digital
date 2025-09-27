package com.projeto_fecaf.biblioteca_digital.controller;

import com.projeto_fecaf.biblioteca_digital.model.Usuario;
import com.projeto_fecaf.biblioteca_digital.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody Usuario usuarioLogin, HttpServletRequest request) {
        try {
            // 1. Cria um "token" de tentativa de login com o email e senha que o usuário enviou
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(usuarioLogin.getEmail(), usuarioLogin.getSenha());

            // 2. Entrega o token para o Spring Security. Ele vai chamar nosso UserDetailsServiceImpl e verificar a senha.
            Authentication authentication = authenticationManager.authenticate(token);

            // 3. Se a autenticação foi um SUCESSO, nós a registramos na sessão de segurança.
            SecurityContext securityContext = SecurityContextHolder.getContext();
            securityContext.setAuthentication(authentication);

            // 4. Criamos a sessão HTTP para o navegador
            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            // 5. Buscamos o usuário completo no banco para retornar ao front-end
            Optional<Usuario> usuarioAutenticado = usuarioService.buscarPorEmail(usuarioLogin.getEmail());

            return usuarioAutenticado.map(ResponseEntity::ok)
                    .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado no banco de dados."));

        } catch (Exception e) {
            // Se authenticationManager.authenticate falhar (senha errada), ele joga uma exceção.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping
    public ResponseEntity<Usuario> criarUsuario(@RequestBody Usuario usuario) {
        Usuario novoUsuario = usuarioService.salvarUsuario(usuario);
        return ResponseEntity.ok(novoUsuario);
    }

    @PostMapping("/ativar-admin")
    public ResponseEntity<String> ativarAdmin(@RequestParam String email, @RequestParam String chave) {
        boolean sucesso = usuarioService.ativarAdmin(email, chave);
        if (sucesso) {
            return ResponseEntity.ok("Permissão de administrador ativada com sucesso para " + email);
        } else {
            return ResponseEntity.status(401).body("Chave de ativação inválida ou usuário não encontrado.");
        }
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarUsuarioPorId(@PathVariable Long id) {
        Optional<Usuario> usuario = usuarioService.buscarPorId(id);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/email")
    public ResponseEntity<Usuario> buscarUsuarioPorEmail(@RequestParam String email) {
        Optional<Usuario> usuario = usuarioService.buscarPorEmail(email);
        return usuario.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        try {
            Usuario usuario = usuarioService.atualizarUsuario(id, usuarioAtualizado);
            return ResponseEntity.ok(usuario);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Long id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}