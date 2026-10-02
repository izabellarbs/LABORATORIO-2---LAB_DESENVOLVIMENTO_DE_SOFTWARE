package view;

import model.Aluno;
import model.Curso;
import model.Disciplina;
import model.Oferta;
import model.PeriodoMatricula;
import model.Professor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ConsoleView {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final Scanner entrada = new Scanner(System.in);

    public String ler(String rotulo) {
        System.out.print(rotulo + ": ");
        if (!entrada.hasNextLine())
            throw new NoSuchElementException("Entrada encerrada");
        String s = entrada.nextLine().trim();
        if (s.isEmpty())
            throw new IllegalArgumentException("Campo obrigatório: " + rotulo);
        return s;
    }

    public int numero(String rotulo) {
        return Integer.parseInt(ler(rotulo));
    }

    public long lerLong(String rotulo) {
        return Long.parseLong(ler(rotulo));
    }

    public long id() {
        return lerLong("ID");
    }

    public LocalDateTime data(String rotulo) {
        return LocalDateTime.parse(ler(rotulo + " (dd/MM/yyyy HH:mm)"), FORMATO);
    }

    public void mensagem(String texto) {
        System.out.println(texto);
    }

    public void erroAoIniciar(String detalhe) {
        System.err.println("Erro ao iniciar: " + detalhe);
    }

    public void primeiroAcesso() {
        System.out.println("Primeiro acesso: admin / admin123. Altere a senha antes de uso real.");
    }

    public void tituloSistema() {
        System.out.println("\n=== SISTEMA DE MATRÍCULAS ===");
    }

    public void boasVindas(String nome) {
        System.out.println("Bem-vindo(a), " + nome);
    }

    public void credenciaisInvalidas() {
        System.out.println("Credenciais inválidas.");
    }

    public void naoFoiPossivel(String motivo) {
        System.out.println("Não foi possível: " + motivo);
    }

    public void operacaoConcluida() {
        System.out.println("Operação concluída.");
    }

    public void opcaoInvalida() {
        System.out.println("Opção inválida.");
    }

    public void matriculaRealizada(Long id) {
        System.out.println("Matrícula ID " + id);
    }

    public void matriculaGerada(String matricula) {
        System.out.println("Matrícula gerada: " + matricula);
    }

    public void menuSecretaria() {
        System.out.println(
                "\n========== MENU SECRETARIA ==========\n" +
                        "1 - Consultar ofertas\n" +
                        "2 - Gerenciar cadastros\n" +
                        "3 - Definir período de matrícula\n" +
                        "4 - Encerrar período de matrícula\n" +
                        "5 - Ver notificações de cobrança\n" +
                        "0 - Voltar\n" +
                        "=====================================");
    }

    public void menuAluno() {
        System.out.println(
                "\n========== MENU ALUNO ==========\n" +
                        "1 - Consultar ofertas\n" +
                        "2 - Realizar matrícula\n" +
                        "3 - Minhas matrículas\n" +
                        "4 - Cancelar matrícula\n" +
                        "0 - Voltar\n" +
                        "================================");
    }

    public void menuProfessor() {
        System.out.println(
                "\n========== MENU PROFESSOR ==========\n" +
                        "1 - Consultar ofertas\n" +
                        "2 - Minhas turmas\n" +
                        "3 - Consultar alunos da turma\n" +
                        "0 - Voltar\n" +
                        "====================================");
    }

    public void menuCadastros() {
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
    }

    public void ofertas(List<Oferta> ofertas) {
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

    public boolean listarCadastros(String titulo, Collection<?> itens, String mensagemVazia) {
        System.out.println("\n--- " + titulo + " ---");

        if (itens.isEmpty()) {
            System.out.println(mensagemVazia);
            return false;
        }

        for (Object item : itens) {
            System.out.println(item);
            System.out.println();
        }

        return true;
    }

    public void itens(Collection<?> itens) {
        for (Object item : itens)
            System.out.println(item);
    }

    public void listarTudo(List<Aluno> alunos, List<Professor> professores, List<Curso> cursos,
            List<Disciplina> disciplinas, List<Oferta> ofertas, List<PeriodoMatricula> periodos) {
        System.out.println("\n========== CADASTROS ==========");

        listarCadastros("ALUNOS", alunos, "Nenhum aluno cadastrado.");

        listarCadastros("PROFESSORES", professores, "Nenhum professor cadastrado.");

        listarCadastros("CURSOS", cursos, "Nenhum curso cadastrado.");

        listarCadastros("DISCIPLINAS", disciplinas, "Nenhuma disciplina cadastrada.");

        listarCadastros("OFERTAS", ofertas, "Nenhuma oferta cadastrada.");

        listarCadastros("PERÍODOS DE MATRÍCULA", periodos, "Nenhum período cadastrado.");
        System.out.println("===============================");
    }

    public void notificacoesCobranca(List<String> linhas) {
        if (linhas.isEmpty()) {
            System.out.println("Nenhuma notificação de cobrança.");
            return;
        }

        System.out.println("\n========== NOTIFICAÇÕES DE COBRANÇA ==========");

        for (String linha : linhas) {
            if (linha.isBlank()) continue;

            String[] dados = linha.split(";");

            LocalDateTime data = LocalDateTime.parse(dados[0]);

            System.out.println();
            System.out.println("Data: " + data.format(FORMATO));
            System.out.println("Tipo: " + dados[1]);
            System.out.println("Matrícula ID: " + dados[2]);
            System.out.println("Aluno: " + dados[4]);
            System.out.println("Matrícula do aluno: " + dados[3]);
            System.out.println("Semestre: " + dados[5]);
            System.out.println("Disciplina: " + dados[6]);
        }

        System.out.println("\n================================================");
    }
}
