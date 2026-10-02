package controller;

import model.Aluno;
import model.Professor;
import model.Secretaria;
import model.SistemaMatriculas;
import model.Usuario;
import view.ConsoleView;

import java.nio.file.Path;
import java.util.NoSuchElementException;

public class SistemaController extends ControladorBase {
    private final SecretariaController secretariaController;
    private final AlunoController alunoController;
    private final ProfessorController professorController;

    public SistemaController(SistemaMatriculas sistema, Path dados, ConsoleView view) {
        super(sistema, dados, view);
        this.secretariaController = new SecretariaController(sistema, dados, view);
        this.alunoController = new AlunoController(sistema, dados, view);
        this.professorController = new ProfessorController(sistema, dados, view);
    }

    public void executar() {
        while (true) {
            try {
                view.tituloSistema();
                String login = view.ler("Login (ou sair)");
                if (login.equalsIgnoreCase("sair"))
                    return;
                Usuario usuario = sistema.autenticar(login, view.ler("Senha"));
                if (usuario == null) {
                    view.credenciaisInvalidas();
                    continue;
                }
                view.boasVindas(usuario.getNome());
                while (true) {
                    if (usuario instanceof Secretaria) {
                        view.menuSecretaria();
                    } else if (usuario instanceof Aluno) {
                        view.menuAluno();
                    } else {
                        view.menuProfessor();
                    }

                    String opcao = view.ler("Opção");
                    if (opcao.equals("0"))
                        break;
                    try {
                        if (opcao.equals("1"))
                            listarOfertas();
                        else if (usuario instanceof Secretaria)
                            secretariaController.executar(opcao, (Secretaria) usuario);
                        else if (usuario instanceof Aluno)
                            alunoController.executar(opcao, (Aluno) usuario);
                        else
                            professorController.executar(opcao, (Professor) usuario);
                    } catch (IllegalArgumentException | IllegalStateException ex) {
                        view.naoFoiPossivel(ex.getMessage());
                    }
                }
            } catch (NoSuchElementException ex) {
                return;
            } catch (IllegalArgumentException ex) {
                view.mensagem(ex.getMessage());
            }
        }
    }
}
