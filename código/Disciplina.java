import java.io.Serializable;

public class Disciplina implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String codigo, nome;
    private final int creditos;
    private final Curso curso;

    public Disciplina(String codigo, String nome, int creditos, Curso curso) {
        if (curso == null)
            throw new IllegalArgumentException("Curso obrigatório");
        this.codigo = codigo;
        this.nome = nome;
        this.creditos = creditos;
        this.curso = curso;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getCreditos() {
        return creditos;
    }

    public Curso getCurso() {
        return curso;
    }
}
