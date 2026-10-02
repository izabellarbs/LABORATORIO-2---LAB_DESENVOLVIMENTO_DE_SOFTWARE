package controller;

import model.Aluno;
import model.Matricula;
import model.Oferta;
import model.SistemaMatriculas;
import view.ConsoleView;

import java.nio.file.Path;

public class AlunoController extends ControladorBase {

    public AlunoController(SistemaMatriculas sistema, Path dados, ConsoleView view) {
        super(sistema, dados, view);
    }

    public void executar(String opcao, Aluno a) {
        switch (opcao) {
            case "2": {
                view.listarCadastros(
                        "OFERTAS DISPONÍVEIS",
                        sistema.getOfertas(),
                        "Nenhuma oferta disponível.");

                long codigo = view.id();

                mudar(() -> {
                    Matricula m = a.realizarMatricula(
                            buscar(sistema.getOfertas(), Oferta::getId, codigo));

                    view.matriculaRealizada(m.getId());
                });

                break;
            }
            case "3":
                view.listarCadastros(
                        "MINHAS MATRÍCULAS",
                        a.getMatriculas(),
                        "Você não possui matrículas.");
                break;
            case "4": {
                if (!view.listarCadastros("SUAS MATRÍCULAS", a.getMatriculas(), "Você não possui matrículas."))
                    break;

                long codigo = view.id();

                mudar(() -> a.cancelarMatricula(
                        buscar(a.getMatriculas(), Matricula::getId, codigo)));

                break;
            }
            default:
                view.opcaoInvalida();
        }
    }
}
