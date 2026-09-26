public class Secretaria extends Usuario {
    private static final long serialVersionUID = 1L;
    private transient SistemaMatriculas sistema;

    public Secretaria(Long id, String nome, String login, String senha) {
        super(id, nome, login, senha);
    }

    void conectar(SistemaMatriculas s) {
        sistema = s;
    }

    public void cadastrarAluno(Aluno a) {
        sistema.cadastrarAluno(a);
    }

    public void cadastrarProfessor(Professor p) {
        sistema.cadastrarProfessor(p);
    }

    public void cadastrarCurso(Curso c) {
        sistema.cadastrarCurso(c);
    }

    public void cadastrarDisciplina(Disciplina d) {
        sistema.cadastrarDisciplina(d);
    }

    public void criarOferta(Oferta o) {
        sistema.criarOferta(o);
    }

    public void definirPeriodo(PeriodoMatricula p) {
        sistema.definirPeriodo(p);
    }

    public void encerrarPeriodo(PeriodoMatricula p) {
        sistema.encerrarPeriodo(p);
    }
}
