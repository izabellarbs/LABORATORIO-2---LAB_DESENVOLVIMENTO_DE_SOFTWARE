import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SistemaMatriculas implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Aluno> alunos = new ArrayList<>();
    private final List<Professor> professores = new ArrayList<>();
    private final List<Secretaria> secretarias = new ArrayList<>();
    private final List<Curso> cursos = new ArrayList<>();
    private final List<Disciplina> disciplinas = new ArrayList<>();
    private final List<Oferta> ofertas = new ArrayList<>();
    private final List<PeriodoMatricula> periodos = new ArrayList<>();
    private final SistemaCobrancas sistemaCobrancas;
    private long proximoId = 1;

    public SistemaMatriculas(Path arquivoCobrancas) {
        sistemaCobrancas = new SistemaCobrancas(arquivoCobrancas);
    }

    public long novoId() {
        return proximoId++;
    }

    public List<Aluno> getAlunos() {
        return Collections.unmodifiableList(alunos);
    }

    public List<Professor> getProfessores() {
        return Collections.unmodifiableList(professores);
    }

    public List<Secretaria> getSecretarias() {
        return Collections.unmodifiableList(secretarias);
    }

    public List<Curso> getCursos() {
        return Collections.unmodifiableList(cursos);
    }

    public List<Disciplina> getDisciplinas() {
        return Collections.unmodifiableList(disciplinas);
    }

    public List<Oferta> getOfertas() {
        return Collections.unmodifiableList(ofertas);
    }

    public List<PeriodoMatricula> getPeriodos() {
        return Collections.unmodifiableList(periodos);
    }

    private List<Usuario> usuarios() {
        List<Usuario> todos = new ArrayList<>();
        todos.addAll(secretarias);
        todos.addAll(alunos);
        todos.addAll(professores);
        return todos;
    }

    public Usuario autenticar(String login, String senha) {
        for (Usuario u : usuarios())
            if (u.autenticar(login, senha))
                return u;
        return null;
    }

    private void conferirLogin(Usuario usuario) {
        if (usuario.getLogin() == null || usuario.getLogin().trim().isEmpty())
            throw new IllegalArgumentException("Login obrigatório");
        for (Usuario u : usuarios())
            if (u.getLogin().equalsIgnoreCase(usuario.getLogin()))
                throw new IllegalArgumentException("Login já cadastrado");
    }

    public void cadastrarSecretaria(Secretaria s) {
        conferirLogin(s);
        secretarias.add(s);
        s.conectar(this);
    }

    public void cadastrarAluno(Aluno a) {
        conferirLogin(a);
        for (Aluno x : alunos)
            if (x.getMatricula().equalsIgnoreCase(a.getMatricula()))
                throw new IllegalArgumentException("Matrícula já cadastrada");
        alunos.add(a);
        a.conectar(this);
    }

    public void cadastrarProfessor(Professor p) {
        conferirLogin(p);
        for (Professor x : professores)
            if (x.getRegistroFuncional().equalsIgnoreCase(p.getRegistroFuncional()))
                throw new IllegalArgumentException("Registro já cadastrado");
        professores.add(p);
    }

    public void cadastrarCurso(Curso c) {
        for (Curso x : cursos)
            if (x.getId().equals(c.getId()))
                throw new IllegalArgumentException("Curso duplicado");
        cursos.add(c);
    }

    public void cadastrarDisciplina(Disciplina d) {
        if (!cursos.contains(d.getCurso()))
            throw new IllegalArgumentException("Curso não cadastrado");
        for (Disciplina x : disciplinas)
            if (x.getCodigo().equalsIgnoreCase(d.getCodigo()))
                throw new IllegalArgumentException("Código de disciplina duplicado");
        disciplinas.add(d);
        d.getCurso().adicionarDisciplina(d);
    }

    public void criarOferta(Oferta o) {
        if (!disciplinas.contains(o.getDisciplina()) || !professores.contains(o.getProfessor()))
            throw new IllegalArgumentException("Disciplina ou professor não cadastrado");
        for (Oferta x : ofertas)
            if (x.getId().equals(o.getId()) || (x.getSemestre().equals(o.getSemestre()) &&
                    x.getDisciplina() == o.getDisciplina()))
                throw new IllegalArgumentException("Oferta duplicada");
        PeriodoMatricula p = periodo(o.getSemestre());
        if (p != null && p.getSituacao() == SituacaoPeriodo.ENCERRADO)
            throw new IllegalArgumentException("Semestre encerrado");
        ofertas.add(o);
        o.getProfessor().adicionar(o);
        if (p != null)
            p.adicionarOferta(o);
    }

    public PeriodoMatricula periodo(String semestre) {
        for (PeriodoMatricula p : periodos)
            if (p.getSemestre().equals(semestre))
                return p;
        return null;
    }

    public void definirPeriodo(PeriodoMatricula p) {
        if (periodo(p.getSemestre()) != null)
            throw new IllegalArgumentException("Período já definido para o semestre");
        periodos.add(p);
        for (Oferta o : ofertas)
            if (o.getSemestre().equals(p.getSemestre()))
                p.adicionarOferta(o);
    }

    public List<Oferta> consultarOfertas(String semestre) {
        List<Oferta> resposta = new ArrayList<>();
        for (Oferta o : ofertas)
            if (semestre == null || semestre.isEmpty() || o.getSemestre().equals(semestre))
                resposta.add(o);
        return resposta;
    }

    public Matricula realizarMatricula(Aluno aluno, Oferta oferta) {
        if (!alunos.contains(aluno) || !ofertas.contains(oferta))
            throw new IllegalArgumentException("Aluno ou oferta inválido");
        if (oferta.getPeriodo() == null || !oferta.getPeriodo().estaAberto())
            throw new IllegalArgumentException("Período de matrícula fora da janela");
        if (!oferta.possuiVaga())
            throw new IllegalArgumentException("Oferta sem vagas");
        int qtd = 0;
        for (Matricula m : aluno.getMatriculas()) {
            if (m.getSituacao() != SituacaoMatricula.ATIVA || !m.getOferta().getSemestre().equals(oferta.getSemestre()))
                continue;
            if (m.getOferta() == oferta || m.getOferta().getDisciplina() == oferta.getDisciplina())
                throw new IllegalArgumentException("Aluno já inscrito nessa disciplina no semestre");
            if (m.getOferta().getTipo() == oferta.getTipo())
                qtd++;
        }
        if (qtd >= (oferta.getTipo() == TipoOferta.OBRIGATORIA ? 4 : 2))
            throw new IllegalArgumentException("Limite de opções desse tipo atingido");
        Matricula m = new Matricula(novoId(), aluno, oferta);
        sistemaCobrancas.notificarMatricula(m);
        aluno.adicionar(m);
        oferta.adicionarMatricula(m);
        return m;
    }

    public void cancelarMatricula(Aluno aluno, Matricula matricula) {
        if (!alunos.contains(aluno) || matricula.getAluno() != aluno || !aluno.getMatriculas().contains(matricula)
                || matricula.getSituacao() != SituacaoMatricula.ATIVA)
            throw new IllegalArgumentException("Matrícula não pertence ao aluno ou já cancelada");
        if (matricula.getOferta().getPeriodo() == null || !matricula.getOferta().getPeriodo().estaAberto())
            throw new IllegalArgumentException("Período de cancelamento encerrado");
        sistemaCobrancas.notificarCancelamento(matricula);
        matricula.getOferta().removerMatricula(matricula);
    }

    public void encerrarPeriodo(PeriodoMatricula p) {
        if (!periodos.contains(p) || p.getSituacao() == SituacaoPeriodo.ENCERRADO)
            throw new IllegalArgumentException("Período inválido ou já encerrado");
        p.encerrar();
        for (Oferta o : p.getOfertas())
            o.encerrar();
    }

    public void salvar(Path arquivo) {
        try {
            Path destino = arquivo.toAbsolutePath();
            Files.createDirectories(destino.getParent());
            Path temp = destino.resolveSibling(destino.getFileName() + ".tmp");
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
                out.writeObject(this);
            }
            try {
                Files.move(temp, destino, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temp, destino, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao salvar dados", e);
        }
    }

    public static SistemaMatriculas carregar(Path arquivo, Path cobrancas) {
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(arquivo))) {
            SistemaMatriculas s = (SistemaMatriculas) in.readObject();
            s.sistemaCobrancas.configurar(cobrancas);
            for (Aluno a : s.alunos)
                a.conectar(s);
            for (Secretaria a : s.secretarias)
                a.conectar(s);
            return s;
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Não foi possível ler os dados; restaure o backup", e);
        }
    }
}
