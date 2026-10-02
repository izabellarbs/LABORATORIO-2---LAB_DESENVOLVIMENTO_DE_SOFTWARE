package model;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
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

    public List<String> lerNotificacoes() {
        if (!Files.exists(arquivo))
            return List.of();

        try {
            return Files.readAllLines(arquivo);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler notificações de cobrança.", e);
        }
    }
}
