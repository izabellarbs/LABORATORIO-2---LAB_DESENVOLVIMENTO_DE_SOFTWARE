import java.io.Serializable;
import java.util.*;

public class Oferta implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String semestre;
    private final TipoOferta tipo;
    private SituacaoOferta situacao = SituacaoOferta.ABERTA;
    private final int limiteMaximo = 60, limiteMinimo = 3;
    private final Disciplina disciplina;
    private final Professor professor;
    private PeriodoMatricula periodo;
    private final List<Matricula> matriculas = new ArrayList<>();

    public Oferta(Long id, String semestre, TipoOferta tipo, Disciplina disciplina, Professor professor) {
        this.id = id;
        this.semestre = semestre;
        this.tipo = tipo;
        this.disciplina = disciplina;
        this.professor = professor;
    }

    public Long getId() {
        return id;
    }

    public String getSemestre() {
        return semestre;
    }

    public TipoOferta getTipo() {
        return tipo;
    }

    public SituacaoOferta getSituacao() {
        return situacao;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public Professor getProfessor() {
        return professor;
    }

    public PeriodoMatricula getPeriodo() {
        return periodo;
    }

    void definirPeriodo(PeriodoMatricula p) {
        periodo = p;
    }

    public List<Matricula> getMatriculas() {
        return Collections.unmodifiableList(matriculas);
    }

    public int getInscritos() {
        int n = 0;
        for (Matricula m : matriculas)
            if (m.getSituacao() == SituacaoMatricula.ATIVA)
                n++;
        return n;
    }

    public void adicionarMatricula(Matricula m) {
        if (m.getOferta() != this || !possuiVaga())
            throw new IllegalArgumentException("Oferta indisponível");
        matriculas.add(m);
    }

    public void removerMatricula(Matricula m) {
        if (!matriculas.contains(m) || m.getSituacao() != SituacaoMatricula.ATIVA)
            throw new IllegalArgumentException("Matrícula não ativa");
        m.cancelar();
    }

    public boolean possuiVaga() {
        return situacao == SituacaoOferta.ABERTA && getInscritos() < limiteMaximo;
    }

    public void encerrar() {
        situacao = getInscritos() >= limiteMinimo ? SituacaoOferta.ATIVA : SituacaoOferta.CANCELADA;
    }
}
