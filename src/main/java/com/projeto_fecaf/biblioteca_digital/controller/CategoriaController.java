package com.projeto_fecaf.biblioteca_digital.controller;

import com.projeto_fecaf.biblioteca_digital.model.Categoria;
import com.projeto_fecaf.biblioteca_digital.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

// Anotação @RestController para indicar um controlador REST.
@RestController
// Mapeamento da URL base para os endpoints da Categoria.
@RequestMapping("/api/categorias")
public class CategoriaController {

    // Injeção de dependência do CategoriaService.
    @Autowired
    private CategoriaService categoriaService;

    // Endpoint para criar uma nova categoria.
    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(@RequestBody Categoria categoria) {
        Categoria novaCategoria = categoriaService.salvarCategoria(categoria);
        return ResponseEntity.ok(novaCategoria);
    }

    // Endpoint para listar todas as categorias.
    @GetMapping
    public ResponseEntity<List<Categoria>> listarCategorias() {
        List<Categoria> categorias = categoriaService.listarCategorias();
        return ResponseEntity.ok(categorias);
    }

    // Endpoint para buscar uma categoria por ID.
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoriaPorId(@PathVariable Long id) {
        Optional<Categoria> categoria = categoriaService.buscarPorId(id);
        return categoria.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint para atualizar uma categoria.
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoriaAtualizada) {
        try {
            Categoria categoria = categoriaService.atualizarCategoria(id, categoriaAtualizada);
            return ResponseEntity.ok(categoria);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint para deletar uma categoria.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(@PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
