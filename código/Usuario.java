import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public abstract class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Long id;
    private final String nome, login, sal, hashSenha;

    protected Usuario(Long id, String nome, String login, String senha) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        if (senha == null || senha.isEmpty())
            throw new IllegalArgumentException("Senha obrigatória");
        byte[] random = new byte[16];
        new SecureRandom().nextBytes(random);
        this.sal = Base64.getEncoder().encodeToString(random);
        this.hashSenha = digest(this.sal + senha);
    }

    private static String digest(String valor) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(valor.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public boolean autenticar(String login, String senha) {
        return this.login.equals(login) && senha != null &&
                MessageDigest.isEqual(hashSenha.getBytes(StandardCharsets.UTF_8),
                        digest(sal + senha).getBytes(StandardCharsets.UTF_8));
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getLogin() {
        return login;
    }
}
