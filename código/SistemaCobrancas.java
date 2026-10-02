import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SistemaCobrancas implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient Path arquivo;

    public SistemaCobrancas(Path arquivo) {
        this.arquivo = arquivo;
    }

    void configurar(Path arquivo) {
        this.arquivo = arquivo;
    }

    private void registrar(String acao, Matricula m) {
        String linha = LocalDateTime.now() + ";" + acao + ";" + m.getId() + ";" +
                m.getAluno().getMatricula() + ";" + m.getAluno().getNome() + ";" +
                m.getOferta().getSemestre() + ";" +
                m.getOferta().getDisciplina().getCodigo() + System.lineSeparator();
        try {
            Files.createDirectories(arquivo.toAbsolutePath().getParent());
            Files.write(arquivo, linha.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new IllegalStateException("Falha ao registrar cobrança", e);
        }
    }

    public void notificarMatricula(Matricula m) {
        registrar("MATRICULA", m);
    }

    public void notificarCancelamento(Matricula m) {
        registrar("CANCELAMENTO", m);
    }

    public void exibirNotificacoes() {
        if (!Files.exists(arquivo)) {
            System.out.println("Nenhuma notificação de cobrança.");
            return;
        }

        try {
            List<String> linhas = Files.readAllLines(arquivo);

            if (linhas.isEmpty()) {
                System.out.println("Nenhuma notificação de cobrança.");
                return;
            }

            DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            System.out.println("\n========== NOTIFICAÇÕES DE COBRANÇA ==========");

            for (String linha : linhas) {
                if (linha.isBlank()) continue;

                String[] dados = linha.split(";");

                LocalDateTime data = LocalDateTime.parse(dados[0]);

                System.out.println();
                System.out.println("Data: " + data.format(formato));
                System.out.println("Tipo: " + dados[1]);
                System.out.println("Matrícula ID: " + dados[2]);
                System.out.println("Aluno: " + dados[4]);
                System.out.println("Matrícula do aluno: " + dados[3]);
                System.out.println("Semestre: " + dados[5]);
                System.out.println("Disciplina: " + dados[6]);
            }

            System.out.println("\n================================================");

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler notificações de cobrança.", e);
        }
    }
}
