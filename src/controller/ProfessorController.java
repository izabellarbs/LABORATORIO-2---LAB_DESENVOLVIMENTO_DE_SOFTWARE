package controller;

import model.Oferta;
import model.Professor;
import model.SistemaMatriculas;
import view.ConsoleView;

import java.nio.file.Path;

public class ProfessorController extends ControladorBase {

    public ProfessorController(SistemaMatriculas sistema, Path dados, ConsoleView view) {
        super(sistema, dados, view);
    }

    public void executar(String opcao, Professor p) {
        switch (opcao) {
            case "2":
                view.itens(p.getOfertas());
                break;
            case "3": {
                view.listarCadastros(
                        "MINHAS TURMAS",
                        p.getOfertas(),
                        "Você não possui turmas.");

                Oferta o = buscar(p.getOfertas(), Oferta::getId, view.id());

                view.itens(p.consultarAlunos(o));

                break;
            }
            default:
                view.opcaoInvalida();
        }
    }
}
