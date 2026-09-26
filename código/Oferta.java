import java.util.List;

public class Oferta {
    private Long id;
    private String semestre;
    private TipoOferta tipo;
    private SituacaoOferta situacao;
    private final int limiteMaximo = 60;
    private final int limiteMinimo = 3;
    private Disciplina disciplina;
    private Professor professor;
    private PeriodoMatricula periodo;
    private List<Matricula> matriculas;

    public void adicionarMatricula(Matricula matricula) {
        throw new UnsupportedOperationException("Implementar na Sprint 3");
    }

    public void removerMatricula(Matricula matricula) {
        throw new UnsupportedOperationException("Implementar na Sprint 3");
    }

    public boolean possuiVaga() {
        throw new UnsupportedOperationException("Implementar na Sprint 3");
    }

    public void encerrar() {
        throw new UnsupportedOperationException("Implementar na Sprint 3");
    }
}
