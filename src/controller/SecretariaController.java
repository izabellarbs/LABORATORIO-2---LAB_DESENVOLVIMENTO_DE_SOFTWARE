package controller;

import model.Aluno;
import model.Curso;
import model.Disciplina;
import model.Oferta;
import model.PeriodoMatricula;
import model.Professor;
import model.Secretaria;
import model.SistemaMatriculas;
import model.TipoOferta;
import view.ConsoleView;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Locale;

public class SecretariaController extends ControladorBase {

    public SecretariaController(SistemaMatriculas sistema, Path dados, ConsoleView view) {
        super(sistema, dados, view);
    }

    public void executar(String opcao, Secretaria s) {
        switch (opcao) {
            case "2":
                cadastros(s);
                break;
            case "3": {
                String semestre = view.ler("Semestre");
                LocalDateTime inicio = view.data("Início"), fim = view.data("Fim");
                mudar(() -> s.definirPeriodo(new PeriodoMatricula(semestre, inicio, fim)));
                break;
            }
            case "4": {
                String semestre = view.ler("Semestre");
                mudar(() -> s.encerrarPeriodo(sistema.periodo(semestre)));
                break;
            }
            case "5":
                view.notificacoesCobranca(sistema.lerNotificacoesCobranca());
                break;
            default:
                view.opcaoInvalida();
        }
    }

    private void cadastros(Secretaria s) {
        view.menuCadastros();
        switch (view.ler("Opção")) {
            case "1": {
                String nome = view.ler("Nome");
                String login = view.ler("Login");
                String senha = view.ler("Senha");

                mudar(() -> {
                    Aluno aluno = new Aluno(nome, login, senha);
                    s.cadastrarAluno(aluno);
                    view.matriculaGerada(aluno.getMatricula());
                });

                break;
            }
            case "2": {
                String nome = view.ler("Nome"), login = view.ler("Login"), senha = view.ler("Senha"),
                        registro = view.ler("Registro funcional");
                mudar(() -> s.cadastrarProfessor(new Professor(nome, login, senha, registro)));
                break;
            }
            case "3": {
                String nome = view.ler("Nome"), creditos = view.ler("Créditos");
                mudar(() -> s.cadastrarCurso(new Curso(nome, Integer.parseInt(creditos))));
                break;
            }
            case "4": {
                String codigo = view.ler("Código");
                String nome = view.ler("Nome");
                int creditos = view.numero("Créditos");

                view.listarCadastros(
                        "CURSOS",
                        sistema.getCursos(),
                        "Nenhum curso cadastrado.");

                long curso = view.id();

                mudar(() -> s.cadastrarDisciplina(
                        new Disciplina(
                                codigo,
                                nome,
                                creditos,
                                buscar(sistema.getCursos(), Curso::getId, curso))));

                break;
            }
            case "5": {
                String semestre = view.ler("Semestre");

                TipoOferta tipo = TipoOferta.valueOf(
                        view.ler("Tipo (OBRIGATORIA/OPTATIVA)").toUpperCase(Locale.ROOT));

                view.listarCadastros("PROFESSORES",
                        sistema.getProfessores(),
                        "Nenhum professor cadastrado.");

                long professor = view.lerLong("ID do professor");

                view.listarCadastros(
                        "DISCIPLINAS",
                        sistema.getDisciplinas(),
                        "Nenhuma disciplina cadastrada.");

                String disciplina = view.ler("Código da disciplina");

                Disciplina d = null;

                for (Disciplina atual : sistema.getDisciplinas()) {
                    if (atual.getCodigo().equalsIgnoreCase(disciplina)) {
                        d = atual;
                    }
                }

                if (d == null) {
                    throw new IllegalArgumentException("Disciplina não encontrada");
                }

                Disciplina escolhida = d;

                mudar(() -> s.criarOferta(
                        new Oferta(
                                semestre,
                                tipo,
                                escolhida,
                                buscar(sistema.getProfessores(), Professor::getId, professor))));

                break;
            }
            case "6":
                view.listarCadastros("ALUNOS", sistema.getAlunos(), "Nenhum aluno cadastrado.");
                break;

            case "7":
                view.listarCadastros("PROFESSORES", sistema.getProfessores(), "Nenhum professor cadastrado.");
                break;

            case "8":
                view.listarCadastros("CURSOS", sistema.getCursos(), "Nenhum curso cadastrado.");
                break;

            case "9":
                view.listarCadastros("DISCIPLINAS", sistema.getDisciplinas(), "Nenhuma disciplina cadastrada.");
                break;

            case "10":
                view.listarCadastros("OFERTAS", sistema.getOfertas(), "Nenhuma oferta cadastrada.");
                break;
            case "11":
                view.listarCadastros("PERÍODOS DE MATRÍCULA", sistema.getPeriodos(),
                        "Nenhum período de matrícula cadastrado.");
                break;
            case "12":
                view.listarTudo(
                        sistema.getAlunos(),
                        sistema.getProfessores(),
                        sistema.getCursos(),
                        sistema.getDisciplinas(),
                        sistema.getOfertas(),
                        sistema.getPeriodos());
                break;

            case "0":
                return;
            default:
                view.opcaoInvalida();
        }
    }
}
