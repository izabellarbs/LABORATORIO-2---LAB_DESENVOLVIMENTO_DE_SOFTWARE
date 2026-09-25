public abstract class Usuario {
    private Long id;
    private String nome;
    private String login;
    private String senha;

    public boolean autenticar(String login, String senha) {
        throw new UnsupportedOperationException("Implementar na Sprint 3");
    }
}
