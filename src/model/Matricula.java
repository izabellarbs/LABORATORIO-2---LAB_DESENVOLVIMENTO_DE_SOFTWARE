package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Matricula implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final LocalDateTime data;
    private SituacaoMatricula situacao;
    private final Aluno aluno;
    private final Oferta oferta;
    private static long proximoId = 1;

    public Matricula(Aluno aluno, Oferta oferta) {
        this.id = proximoId++;
        this.aluno = aluno;
        this.oferta = oferta;
        this.data = LocalDateTime.now();
        this.situacao = SituacaoMatricula.ATIVA;
    }

    Matricula(Long id, Aluno aluno, Oferta oferta, LocalDateTime data, SituacaoMatricula situacao) {
        this.id = id;

        if (id >= proximoId)  proximoId = id + 1;

        this.aluno = aluno;
        this.oferta = oferta;
        this.data = data;
        this.situacao = situacao;
    }

    public Long getId() {
        return id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public Oferta getOferta() {
        return oferta;
    }

    public LocalDateTime getData() {
        return data;
    }

    public SituacaoMatricula getSituacao() {
        return situacao;
    }

    public void cancelar() {
        situacao = SituacaoMatricula.CANCELADA;
    }

    @Override
    public String toString() {
        return String.format(
                "ID: %d | %s | %s | %s",
                id,
                oferta.getSemestre(),
                oferta.getDisciplina().getNome(),
                situacao);
    }
}

