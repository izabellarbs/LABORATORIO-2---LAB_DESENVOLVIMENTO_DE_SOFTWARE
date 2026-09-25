import java.util.List;

public class SistemaMatriculas {
    private List<Aluno> alunos;
    private List<Professor> professores;
    private List<Curso> cursos;
    private List<Disciplina> disciplinas;
    private List<Oferta> ofertas;
    private PeriodoMatricula periodo;
    private SistemaCobrancas sistemaCobrancas;

    public Matricula realizarMatricula(Aluno aluno, Oferta oferta) { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
    public void cancelarMatricula(Aluno aluno, Matricula matricula) { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
    public List<Oferta> consultarOfertas(String semestre) { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
    public void encerrarPeriodo(PeriodoMatricula periodo) { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
}
