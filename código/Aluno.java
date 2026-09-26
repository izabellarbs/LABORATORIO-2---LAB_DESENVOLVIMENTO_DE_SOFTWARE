import java.util.*;

public class Aluno extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String matricula;
    private final List<Matricula> matriculas = new ArrayList<>();
    private transient SistemaMatriculas sistema;

    public Aluno(Long id, String nome, String login, String senha, String matricula) {
        super(id, nome, login, senha);
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public List<Matricula> getMatriculas() {
        return Collections.unmodifiableList(matriculas);
    }

    void adicionar(Matricula m) {
        matriculas.add(m);
    }

    void conectar(SistemaMatriculas s) {
        sistema = s;
    }

    public List<Oferta> consultarOfertas() {
        return sistema.consultarOfertas(null);
    }

    public Matricula realizarMatricula(Oferta oferta) {
        return sistema.realizarMatricula(this, oferta);
    }

    public void cancelarMatricula(Matricula matricula) {
        sistema.cancelarMatricula(this, matricula);
    }
}
