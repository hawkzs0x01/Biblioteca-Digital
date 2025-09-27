package com.projeto_fecaf.biblioteca_digital.dto;

public class EmprestimoRequest {
    private Long livroId;
    private String dataDevolucao;

    // Getters e Setters
    public Long getLivroId() {
        return livroId;
    }

    public void setLivroId(Long livroId) {
        this.livroId = livroId;
    }

    public String getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(String dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }
}