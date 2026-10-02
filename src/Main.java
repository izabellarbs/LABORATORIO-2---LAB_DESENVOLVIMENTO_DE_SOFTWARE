import controller.SistemaController;
import model.PersistenciaArquivo;
import model.Secretaria;
import model.SistemaMatriculas;
import view.ConsoleView;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        Path pasta = Paths.get(args.length > 0 ? args[0] : "dados");
        Path dados = pasta.resolve("dados.txt");
        Path cobrancas = pasta.resolve("notificacoes-cobranca.csv");
        ConsoleView view = new ConsoleView();
        try {
            SistemaMatriculas sistema;
            if (Files.exists(dados))
                sistema = PersistenciaArquivo.carregar(dados, cobrancas);
            else {
                sistema = new SistemaMatriculas(cobrancas);
                sistema.cadastrarSecretaria(new Secretaria("Secretaria", "admin", "admin123"));
                PersistenciaArquivo.salvar(dados, sistema);
                view.primeiroAcesso();
            }
            new SistemaController(sistema, dados, view).executar();
        } catch (Exception e) {
            view.erroAoIniciar(e.getMessage());
        }
    }
}
