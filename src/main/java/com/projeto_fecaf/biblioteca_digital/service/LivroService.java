package com.projeto_fecaf.biblioteca_digital.service;

import com.projeto_fecaf.biblioteca_digital.model.Livro;
import com.projeto_fecaf.biblioteca_digital.repository.LivroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LivroService {

    @Autowired
    private LivroRepository livroRepository;

    public Livro salvarLivro(Livro livro) {
        return livroRepository.save(livro);
    }

    public List<Livro> listarLivros() {
        return livroRepository.findAll();
    }

    public Optional<Livro> buscarPorId(Long id) {
        return livroRepository.findById(id);
    }

    // NOVO MÉTODO ADICIONADO PARA O FILTRO
    public List<Livro> buscarPorCategoria(Long categoriaId) {
        return livroRepository.findByCategoriaId(categoriaId);
    }

    public Livro atualizarLivro(Long id, Livro livroAtualizado) {
        Livro livroExistente = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Livro não encontrado"));
        livroExistente.setTitulo(livroAtualizado.getTitulo());
        livroExistente.setAutor(livroAtualizado.getAutor());
        livroExistente.setAnoPublicacao(livroAtualizado.getAnoPublicacao());
        livroExistente.setIsbn(livroAtualizado.getIsbn());
        livroExistente.setUrlCapa(livroAtualizado.getUrlCapa());
        livroExistente.setQuantidadeDisponivel(livroAtualizado.getQuantidadeDisponivel());
        return livroRepository.save(livroExistente);
    }

    public void deletarLivro(Long id) {
        livroRepository.deleteById(id);
    }
}