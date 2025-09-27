package com.projeto_fecaf.biblioteca_digital.controller;

import com.projeto_fecaf.biblioteca_digital.dto.EmprestimoRequest; // IMPORTAÇÃO CORRIGIDA
import com.projeto_fecaf.biblioteca_digital.model.Emprestimo;
import com.projeto_fecaf.biblioteca_digital.model.Usuario;
import com.projeto_fecaf.biblioteca_digital.repository.UsuarioRepository;
import com.projeto_fecaf.biblioteca_digital.service.EmprestimoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emprestimos")
public class EmprestimoController {

    @Autowired
    private EmprestimoService emprestimoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<?> reservarLivro(@RequestBody EmprestimoRequest request, Principal principal) {
        try {
            String userEmail = principal.getName();
            Usuario usuario = usuarioRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuário autenticado não encontrado."));

            Emprestimo novoEmprestimo = emprestimoService.realizarEmprestimo(
                    request.getLivroId(),
                    usuario.getId(),
                    LocalDate.parse(request.getDataDevolucao())
            );
            return ResponseEntity.ok(novoEmprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // O restante do controller permanece igual...
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Emprestimo>> listarEmprestimosPorUsuario(@PathVariable Long usuarioId) {
        List<Emprestimo> emprestimos = emprestimoService.listarEmprestimosPorUsuario(usuarioId);
        return ResponseEntity.ok(emprestimos);
    }

    @PutMapping("/{emprestimoId}/devolver")
    public ResponseEntity<Void> devolverLivro(@PathVariable Long emprestimoId) {
        try {
            emprestimoService.devolverLivro(emprestimoId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Emprestimo>> listarTodosEmprestimos() {
        return ResponseEntity.ok(emprestimoService.listarEmprestimos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Emprestimo> buscarEmprestimoPorId(@PathVariable Long id) {
        Optional<Emprestimo> emprestimo = emprestimoService.buscarPorId(id);
        return emprestimo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Emprestimo> atualizarEmprestimo(@PathVariable Long id, @RequestBody Emprestimo emprestimoAtualizado) {
        try {
            Emprestimo emprestimo = emprestimoService.atualizarEmprestimo(id, emprestimoAtualizado);
            return ResponseEntity.ok(emprestimo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarEmprestimo(@PathVariable Long id) {
        emprestimoService.deletarEmprestimo(id);
        return ResponseEntity.noContent().build();
    }
}
