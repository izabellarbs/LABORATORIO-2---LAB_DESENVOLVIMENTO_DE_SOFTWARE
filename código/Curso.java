import java.io.Serializable;
import java.util.*;

public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String nome;
    private final int creditos;
    private final List<Disciplina> disciplinas = new ArrayList<>();

    public Curso(Long id, String nome, int creditos) {
        this.id = id;
        this.nome = nome;
        this.creditos = creditos;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public List<Disciplina> getDisciplinas() {
        return Collections.unmodifiableList(disciplinas);
    }

    public void adicionarDisciplina(Disciplina d) {
        if (d.getCurso() != this)
            throw new IllegalArgumentException("Disciplina de outro curso");
        if (!disciplinas.contains(d))
            disciplinas.add(d);
    }
}
