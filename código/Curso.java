import java.io.Serializable;
import java.util.*;

public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String nome;
    private final int creditos;
    private static long proximoId = 1;
    private final List<Disciplina> disciplinas = new ArrayList<>();

    public Curso(String nome, int creditos) {
        this.id = proximoId++;
        this.nome = nome;
        this.creditos = creditos;
    }

    Curso(Long id, String nome, int creditos) {
        this.id = id;
        this.nome = nome;
        this.creditos = creditos;
        if (id >= proximoId)
            proximoId = id + 1;
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

    @Override
    public String toString() {
        String disciplinasTexto = disciplinas.isEmpty()
                ? "Nenhuma"
                : String.join(", ", disciplinas.stream().map(Disciplina::getCodigo).toList());

        return String.format(
                "ID: %d | %s | Créditos totais: %d%nDisciplinas: %s",
                id, nome, creditos, disciplinasTexto);
    }
}
