package com.projeto_fecaf.biblioteca_digital.repository;

import com.projeto_fecaf.biblioteca_digital.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}
