package com.projeto_fecaf.biblioteca_digital.repository;

import com.projeto_fecaf.biblioteca_digital.model.Emprestimo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {

    List<Emprestimo> findByUsuarioIdAndFoiDevolvidoFalse(Long usuarioId);

    boolean existsByLivroIdAndUsuarioIdAndFoiDevolvidoFalse(Long livroId, Long usuarioId);
}