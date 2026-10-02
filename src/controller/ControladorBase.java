package controller;

import model.Oferta;
import model.PersistenciaArquivo;
import model.SistemaMatriculas;
import view.ConsoleView;

import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.function.ToLongFunction;

public abstract class ControladorBase {
    protected final SistemaMatriculas sistema;
    protected final Path dados;
    protected final ConsoleView view;

    protected ControladorBase(SistemaMatriculas sistema, Path dados, ConsoleView view) {
        this.sistema = sistema;
        this.dados = dados;
        this.view = view;
    }

    protected static <T> T buscar(Collection<T> colecao, ToLongFunction<T> codigo, long id) {
        for (T t : colecao)
            if (codigo.applyAsLong(t) == id)
                return t;
        throw new IllegalArgumentException("ID não encontrado: " + id);
    }

    protected void mudar(Runnable acao) {
        acao.run();
        PersistenciaArquivo.salvar(dados, sistema);
        view.operacaoConcluida();
    }

    protected void listarOfertas() {
        String semestre = view.ler("Semestre (ex.: 2026/2; digite * para todos)");
        List<Oferta> ofertas = sistema.consultarOfertas(semestre.equals("*") ? null : semestre);
        view.ofertas(ofertas);
    }
}
