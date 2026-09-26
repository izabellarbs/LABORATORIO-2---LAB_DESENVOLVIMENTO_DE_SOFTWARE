import java.util.*;

public class Professor extends Usuario {
    private static final long serialVersionUID = 1L;
    private final String registroFuncional;
    private final List<Oferta> ofertas = new ArrayList<>();

    public Professor(Long id, String nome, String login, String senha, String registro) {
        super(id, nome, login, senha);
        this.registroFuncional = registro;
    }

    public String getRegistroFuncional() {
        return registroFuncional;
    }

    void adicionar(Oferta oferta) {
        ofertas.add(oferta);
    }

    public List<Oferta> getOfertas() {
        return Collections.unmodifiableList(ofertas);
    }

    public List<Aluno> consultarAlunos(Oferta oferta) {
        if (!ofertas.contains(oferta))
            throw new IllegalArgumentException("Oferta não pertence ao professor");
        List<Aluno> alunos = new ArrayList<>();
        for (Matricula m : oferta.getMatriculas())
            if (m.getSituacao() == SituacaoMatricula.ATIVA)
                alunos.add(m.getAluno());
        return alunos;
    }
}
