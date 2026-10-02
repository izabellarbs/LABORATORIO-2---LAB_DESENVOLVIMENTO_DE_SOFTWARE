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
        Path dados = pasta.resolve("dados.txt");
        Path cobrancas = pasta.resolve("notificacoes-cobranca.csv");
        try {
            SistemaMatriculas sistema;
            if (Files.exists(dados))
                sistema = PersistenciaArquivo.carregar(dados, cobrancas);
            else {
                sistema = new SistemaMatriculas(cobrancas);
                sistema.cadastrarSecretaria(new Secretaria("Secretaria", "admin", "admin123"));
                PersistenciaArquivo.salvar(dados, sistema);
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
        PersistenciaArquivo.salvar(dados, sistema);
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
                    if (usuario instanceof Secretaria) {

                        System.out.println(
                                "\n========== MENU SECRETARIA ==========\n" +
                                        "1 - Consultar ofertas\n" +
                                        "2 - Gerenciar cadastros\n" +
                                        "3 - Definir período de matrícula\n" +
                                        "4 - Encerrar período de matrícula\n" +
                                        "0 - Voltar\n" +
                                        "=====================================");
                    } else if (usuario instanceof Aluno) {
                        System.out.println(
                                "\n========== MENU ALUNO ==========\n" +
                                        "1 - Consultar ofertas\n" +
                                        "2 - Realizar matrícula\n" +
                                        "3 - Minhas matrículas\n" +
                                        "4 - Cancelar matrícula\n" +
                                        "0 - Voltar\n" +
                                        "================================");
                    } else {
                        System.out.println(
                                "\n========== MENU PROFESSOR ==========\n" +
                                        "1 - Consultar ofertas\n" +
                                        "2 - Minhas turmas\n" +
                                        "3 - Consultar alunos da turma\n" +
                                        "0 - Voltar\n" +
                                        "====================================");
                    }

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
        List<Oferta> ofertas = sistema.consultarOfertas(semestre.equals("*") ? null : semestre);

        System.out.println("\n========== OFERTAS ==========");

        if (ofertas.isEmpty()) {
            System.out.println("\nNenhuma oferta encontrada.");
        } else {
            for (Oferta o : ofertas) {
                System.out.println();
                System.out.println(o);
            }
        }
        System.out.println("\n=============================");
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
                    System.out.println(m);
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
                    System.out.println(o);
                break;
            case "3": {
                Oferta o = buscar(sistema.getOfertas(), Oferta::getId, id());
                for (Aluno a : p.consultarAlunos(o))
                    System.out.println(a);
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
        System.out.println(
                "\n========== GERENCIAR CADASTROS ==========\n" +
                        "\n--- CADASTRAR ---\n" +
                        "1 - Cadastrar aluno\n" +
                        "2 - Cadastrar professor\n" +
                        "3 - Cadastrar curso\n" +
                        "4 - Cadastrar disciplina\n" +
                        "5 - Cadastrar oferta\n" +
                        "\n--- LISTAR ---\n" +
                        "6 - Listar alunos\n" +
                        "7 - Listar professores\n" +
                        "8 - Listar cursos\n" +
                        "9 - Listar disciplinas\n" +
                        "10 - Listar ofertas\n" +
                        "11 - Listar períodos de matrícula\n" +
                        "12 - Listar tudo\n" +
                        "\n0 - Voltar\n" +
                        "==========================================");
        switch (ler("Opção")) {
            case "1": {
                String nome = ler("Nome");
                String login = ler("Login");
                String senha = ler("Senha");

                mudar(() -> {
                    Aluno aluno = new Aluno(nome, login, senha);
                    s.cadastrarAluno(aluno);
                    System.out.println("Matrícula gerada: " + aluno.getMatricula());
                });

                break;
            }
            case "2": {
                String nome = ler("Nome"), login = ler("Login"), senha = ler("Senha"),
                        registro = ler("Registro funcional");
                mudar(() -> s.cadastrarProfessor(new Professor(nome, login, senha, registro)));
                break;
            }
            case "3": {
                String nome = ler("Nome"), creditos = ler("Créditos");
                mudar(() -> s.cadastrarCurso(new Curso(nome, Integer.parseInt(creditos))));
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

                mudar(() -> s.criarOferta(new Oferta(semestre, tipo, escolhida,
                        buscar(sistema.getProfessores(), Professor::getId, curso))));
                break;
            }
            case "6":
                listarCadastros("ALUNOS", sistema.getAlunos(),"Nenhum aluno cadastrado.");
                break;

            case "7":
                listarCadastros("PROFESSORES", sistema.getProfessores(),"Nenhum professor cadastrado.");
                break;

            case "8":
                listarCadastros("CURSOS", sistema.getCursos(),"Nenhum curso cadastrado.");
                break;

            case "9":
                listarCadastros("DISCIPLINAS", sistema.getDisciplinas(),"Nenhuma disciplina cadastrada.");
                break;

            case "10":
                listarCadastros("OFERTAS", sistema.getOfertas(),"Nenhuma oferta cadastrada.");
                break;
            case "11":
                listarCadastros("PERÍODOS DE MATRÍCULA", sistema.getPeriodos(),"Nenhum período de matrícula cadastrado.");
                break;
            case "12":
                listarTudo();
                break;

            case "0":
                return;
            default:
                System.out.println("Opção inválida.");
        }
    }

    private static void listarCadastros(String titulo, Collection<?> itens, String mensagemVazia) {
        System.out.println("\n--- " + titulo + " ---");
        if (itens.isEmpty()) {
            System.out.println(mensagemVazia);
            return;
        }

        for (Object item : itens) {
            System.out.println(item);
            System.out.println();
        }
    }

    private void listarTudo() {
        System.out.println("\n========== CADASTROS ==========");

        listarCadastros("ALUNOS", sistema.getAlunos(), "Nenhum aluno cadastrado.");

        listarCadastros("PROFESSORES", sistema.getProfessores(), "Nenhum professor cadastrado.");

        listarCadastros("CURSOS", sistema.getCursos(), "Nenhum curso cadastrado.");

        listarCadastros("DISCIPLINAS", sistema.getDisciplinas(), "Nenhuma disciplina cadastrada.");

        listarCadastros("OFERTAS", sistema.getOfertas(), "Nenhuma oferta cadastrada.");

        listarCadastros("PERÍODOS DE MATRÍCULA", sistema.getPeriodos(), "Nenhum período cadastrado.");
        System.out.println("===============================");
    }
}
