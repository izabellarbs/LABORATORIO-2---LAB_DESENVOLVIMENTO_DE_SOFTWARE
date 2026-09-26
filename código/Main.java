import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {
    private static final Scanner entrada = new Scanner(System.in);
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final SistemaMatriculas sistema;
    private final Path dados;

    private Main(SistemaMatriculas sistema, Path dados) {
        this.sistema = sistema;
        this.dados = dados;
    }

    public static void main(String[] args) {
        Path pasta = Paths.get(args.length > 0 ? args[0] : "dados");
        Path dados = pasta.resolve("matriculas.dat");
        Path cobrancas = pasta.resolve("notificacoes-cobranca.csv");
        try {
            SistemaMatriculas sistema;
            if (Files.exists(dados))
                sistema = SistemaMatriculas.carregar(dados, cobrancas);
            else {
                sistema = new SistemaMatriculas(cobrancas);
                sistema.cadastrarSecretaria(new Secretaria(sistema.novoId(), "Secretaria", "admin", "admin123"));
                sistema.salvar(dados);
                System.out.println("Primeiro acesso: admin / admin123. Altere a senha antes de uso real.");
            }
            new Main(sistema, dados).executar();
        } catch (Exception e) {
            System.err.println("Erro ao iniciar: " + e.getMessage());
        }
    }

    private static String ler(String rotulo) {
        System.out.print(rotulo + ": ");
        if (!entrada.hasNextLine())
            throw new NoSuchElementException("Entrada encerrada");
        String s = entrada.nextLine().trim();
        if (s.isEmpty())
            throw new IllegalArgumentException("Campo obrigatório: " + rotulo);
        return s;
    }

    private static int numero(String rotulo) {
        return Integer.parseInt(ler(rotulo));
    }

    private static long id() {
        return Long.parseLong(ler("ID"));
    }

    private static LocalDateTime data(String rotulo) {
        return LocalDateTime.parse(ler(rotulo + " (dd/MM/yyyy HH:mm)"), FORMATO);
    }

    private static <T> T buscar(Collection<T> colecao, java.util.function.ToLongFunction<T> codigo, long id) {
        for (T t : colecao)
            if (codigo.applyAsLong(t) == id)
                return t;
        throw new IllegalArgumentException("ID não encontrado: " + id);
    }

    private void mudar(Runnable acao) {
        acao.run();
        sistema.salvar(dados);
        System.out.println("Operação concluída.");
    }

    private void executar() {
        while (true) {
            try {
                System.out.println("\n=== SISTEMA DE MATRÍCULAS ===");
                String login = ler("Login (ou sair)");
                if (login.equalsIgnoreCase("sair"))
                    return;
                Usuario usuario = sistema.autenticar(login, ler("Senha"));
                if (usuario == null) {
                    System.out.println("Credenciais inválidas.");
                    continue;
                }
                System.out.println("Bem-vindo(a), " + usuario.getNome());
                while (true) {
                    System.out.println("\n0 Voltar | 1 Consultar ofertas | " +
                            (usuario instanceof Secretaria ? "2 Cadastros | 3 Período | 4 Encerrar período"
                                    : usuario instanceof Aluno ? "2 Matricular | 3 Minhas matrículas | 4 Cancelar"
                                            : "2 Minhas turmas | 3 Consultar alunos"));
                    String opcao = ler("Opção");
                    if (opcao.equals("0"))
                        break;
                    try {
                        if (opcao.equals("1"))
                            listarOfertas();
                        else if (usuario instanceof Secretaria)
                            secretaria(opcao, (Secretaria) usuario);
                        else if (usuario instanceof Aluno)
                            aluno(opcao, (Aluno) usuario);
                        else
                            professor(opcao, (Professor) usuario);
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        System.out.println("Não foi possível: " + ex.getMessage());
                    }
                }
            } catch (NoSuchElementException ex) {
                return;
            } catch (IllegalArgumentException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void listarOfertas() {
        String semestre = ler("Semestre (ex.: 2026/2; digite * para todos)");
        for (Oferta o : sistema.consultarOfertas(semestre.equals("*") ? null : semestre))
            System.out.printf("ID %d | %s | %s | %s | %s | %s | %d/60 alunos%n",
                    o.getId(), o.getSemestre(), o.getDisciplina().getCodigo() + " " + o.getDisciplina().getNome(),
                    o.getProfessor().getNome(), o.getTipo(), o.getSituacao(), o.getInscritos());
    }

    private void aluno(String opcao, Aluno a) {
        switch (opcao) {
            case "2": {
                long codigo = id();
                mudar(() -> {
                    Matricula m = a.realizarMatricula(buscar(sistema.getOfertas(), Oferta::getId, codigo));
                    System.out.println("Matrícula ID " + m.getId());
                });
                break;
            }
            case "3":
                for (Matricula m : a.getMatriculas())
                    System.out.printf("ID %d | %s | %s | %s%n", m.getId(),
                            m.getOferta().getSemestre(), m.getOferta().getDisciplina().getNome(), m.getSituacao());
                break;
            case "4": {
                long codigo = id();
                mudar(() -> a.cancelarMatricula(buscar(a.getMatriculas(), Matricula::getId, codigo)));
                break;
            }
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void professor(String opcao, Professor p) {
        switch (opcao) {
            case "2":
                for (Oferta o : p.getOfertas())
                    System.out.println(o.getId() + " | " + o.getDisciplina().getNome() + " | " + o.getSemestre());
                break;
            case "3": {
                Oferta o = buscar(sistema.getOfertas(), Oferta::getId, id());
                for (Aluno a : p.consultarAlunos(o))
                    System.out.println(a.getMatricula() + " | " + a.getNome());
                break;
            }
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void secretaria(String opcao, Secretaria s) {
        switch (opcao) {
            case "2":
                cadastros(s);
                break;
            case "3": {
                String semestre = ler("Semestre");
                LocalDateTime inicio = data("Início"), fim = data("Fim");
                mudar(() -> s.definirPeriodo(new PeriodoMatricula(semestre, inicio, fim)));
                break;
            }
            case "4": {
                String semestre = ler("Semestre");
                mudar(() -> s.encerrarPeriodo(sistema.periodo(semestre)));
                break;
            }
            default:
                System.out.println("Opção inválida.");
        }
    }

    private void cadastros(Secretaria s) {
        System.out.println("1 Aluno | 2 Professor | 3 Curso | 4 Disciplina | 5 Oferta | 6 Listar cadastros");
        switch (ler("Opção")) {
            case "1": {
                String nome = ler("Nome"), login = ler("Login"), senha = ler("Senha"), registro = ler("Matrícula");
                mudar(() -> s.cadastrarAluno(new Aluno(sistema.novoId(), nome, login, senha, registro)));
                break;
            }
            case "2": {
                String nome = ler("Nome"), login = ler("Login"), senha = ler("Senha"),
                        registro = ler("Registro funcional");
                mudar(() -> s.cadastrarProfessor(new Professor(sistema.novoId(), nome, login, senha, registro)));
                break;
            }
            case "3": {
                String nome = ler("Nome"), creditos = ler("Créditos");
                mudar(() -> s.cadastrarCurso(new Curso(sistema.novoId(), nome, Integer.parseInt(creditos))));
                break;
            }
            case "4": {
                String codigo = ler("Código"), nome = ler("Nome");
                int creditos = numero("Créditos");
                long curso = id();
                mudar(() -> s.cadastrarDisciplina(new Disciplina(codigo, nome, creditos,
                        buscar(sistema.getCursos(), Curso::getId, curso))));
                break;
            }
            case "5": {
                String semestre = ler("Semestre");
                TipoOferta tipo = TipoOferta.valueOf(ler("Tipo (OBRIGATORIA/OPTATIVA)").toUpperCase(Locale.ROOT));
                long curso = Long.parseLong(ler("ID do professor"));
                String disciplina = ler("Código da disciplina");
                Disciplina d = null;
                for (Disciplina atual : sistema.getDisciplinas())
                    if (atual.getCodigo().equalsIgnoreCase(disciplina))
                        d = atual;
                if (d == null)
                    throw new IllegalArgumentException("Disciplina não encontrada");
                Disciplina escolhida = d;
                mudar(() -> s.criarOferta(new Oferta(sistema.novoId(), semestre, tipo, escolhida,
                        buscar(sistema.getProfessores(), Professor::getId, curso))));
                break;
            }
            case "6":
                for (Curso c : sistema.getCursos())
                    System.out.println("Curso " + c.getId() + " " + c.getNome());
                for (Disciplina d : sistema.getDisciplinas())
                    System.out.println("Disciplina " + d.getCodigo() + " " + d.getNome());
                for (Aluno a : sistema.getAlunos())
                    System.out.println("Aluno " + a.getId() + " " + a.getNome() + " / " + a.getMatricula());
                for (Professor p : sistema.getProfessores())
                    System.out.println("Professor " + p.getId() + " " + p.getNome());
                for (PeriodoMatricula p : sistema.getPeriodos())
                    System.out.println("Período " + p.getSemestre() + " " + p.getInicio().format(FORMATO) + " a "
                            + p.getFim().format(FORMATO) + " " + p.getSituacao());
                break;
            default:
                System.out.println("Opção inválida.");
        }
    }
}
