package com.projeto_fecaf.biblioteca_digital.service;

import com.projeto_fecaf.biblioteca_digital.model.Emprestimo;
import com.projeto_fecaf.biblioteca_digital.model.Livro;
import com.projeto_fecaf.biblioteca_digital.model.Usuario;
import com.projeto_fecaf.biblioteca_digital.repository.EmprestimoRepository;
import com.projeto_fecaf.biblioteca_digital.repository.LivroRepository;
import com.projeto_fecaf.biblioteca_digital.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmprestimoService {

    @Autowired
    private EmprestimoRepository emprestimoRepository;
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Emprestimo realizarEmprestimo(Long livroId, Long usuarioId, LocalDate dataDevolucaoPrevista) {
        // ========== INÍCIO DO CÓDIGO DE DEBUG ==========
        System.out.println("--- INICIANDO PROCESSO DE EMPRÉSTIMO ---");
        System.out.println("Recebido Livro ID: " + livroId);
        System.out.println("Recebido Usuário ID: " + usuarioId);
        // ========== FIM DO CÓDIGO DE DEBUG ==========

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado."));
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        System.out.println("Livro encontrado: " + livro.getTitulo());
        System.out.println("Quantidade disponível ANTES da verificação: " + livro.getQuantidadeDisponivel());

        // 1. VERIFICAÇÃO DE ESTOQUE
        if (livro.getQuantidadeDisponivel() <= 0) {
            System.out.println("ERRO: Verificação de estoque falhou. Quantidade é zero ou menor.");
            throw new RuntimeException("Livro indisponível no acervo para empréstimo.");
        }

        // 2. NOVA VERIFICAÇÃO: Impede que o mesmo usuário pegue o mesmo livro duas vezes
        boolean jaPossuiEmprestimoAtivo = emprestimoRepository.existsByLivroIdAndUsuarioIdAndFoiDevolvidoFalse(livroId, usuarioId);
        System.out.println("Usuário já possui empréstimo ativo para este livro? " + jaPossuiEmprestimoAtivo);
        if (jaPossuiEmprestimoAtivo) {
            System.out.println("ERRO: Verificação de empréstimo duplicado falhou.");
            throw new RuntimeException("Você já possui um empréstimo ativo para este livro.");
        }

        // 3. ATUALIZA O ESTOQUE
        System.out.println("Todas as verificações passaram. Atualizando o estoque...");
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() - 1);
        livroRepository.save(livro);

        // 4. CRIA O EMPRÉSTIMO
        System.out.println("Criando novo registro de empréstimo...");
        Emprestimo novoEmprestimo = new Emprestimo();
        novoEmprestimo.setLivro(livro);
        novoEmprestimo.setUsuario(usuario);
        novoEmprestimo.setDataEmprestimo(LocalDate.now());
        novoEmprestimo.setDataDevolucaoPrevista(dataDevolucaoPrevista);
        novoEmprestimo.setFoiDevolvido(false);

        Emprestimo emprestimoSalvo = emprestimoRepository.save(novoEmprestimo);
        System.out.println("--- PROCESSO DE EMPRÉSTIMO FINALIZADO COM SUCESSO ---");
        return emprestimoSalvo;
    }


    @Transactional
    public void devolverLivro(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoId)
                .orElseThrow(() -> new RuntimeException("Registro de empréstimo não encontrado."));

        if (emprestimo.getFoiDevolvido()) {
            throw new RuntimeException("Este livro já foi devolvido.");
        }

        Livro livro = emprestimo.getLivro();
        livro.setQuantidadeDisponivel(livro.getQuantidadeDisponivel() + 1);
        livroRepository.save(livro);

        emprestimo.setFoiDevolvido(true);
        emprestimoRepository.save(emprestimo);
    }

    public List<Emprestimo> listarEmprestimosPorUsuario(Long usuarioId) {
        return emprestimoRepository.findByUsuarioIdAndFoiDevolvidoFalse(usuarioId);
    }

    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.findAll();
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return emprestimoRepository.findById(id);
    }

    public Emprestimo atualizarEmprestimo(Long id, Emprestimo emprestimoAtualizado) {
        Emprestimo emprestimoExistente = emprestimoRepository.findById(id).orElseThrow(() -> new RuntimeException("Empréstimo não encontrado"));
        emprestimoExistente.setDataEmprestimo(emprestimoAtualizado.getDataEmprestimo());
        emprestimoExistente.setDataDevolucaoPrevista(emprestimoAtualizado.getDataDevolucaoPrevista());
        emprestimoExistente.setLivro(emprestimoAtualizado.getLivro());
        emprestimoExistente.setUsuario(emprestimoAtualizado.getUsuario());
        emprestimoExistente.setFoiDevolvido(emprestimoAtualizado.getFoiDevolvido());
        return emprestimoRepository.save(emprestimoExistente);
    }

    public void deletarEmprestimo(Long id) {
        emprestimoRepository.deleteById(id);
    }
}