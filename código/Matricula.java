import java.time.LocalDateTime;

public class Matricula {
    private Long id;
    private LocalDateTime data;
    private SituacaoMatricula situacao;
    private Aluno aluno;
    private Oferta oferta;

    public void cancelar() {
        throw new UnsupportedOperationException("Implementar na Sprint 3");
    }
}
