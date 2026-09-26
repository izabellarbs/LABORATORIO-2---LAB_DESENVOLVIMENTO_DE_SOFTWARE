import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

public class PeriodoMatricula implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String semestre;
    private final LocalDateTime inicio, fim;
    private SituacaoPeriodo situacao = SituacaoPeriodo.ABERTO;
    private final List<Oferta> ofertas = new ArrayList<>();

    public PeriodoMatricula(String semestre, LocalDateTime inicio, LocalDateTime fim) {
        if (!inicio.isBefore(fim))
            throw new IllegalArgumentException("Início deve anteceder o fim");
        this.semestre = semestre;
        this.inicio = inicio;
        this.fim = fim;
    }

    public String getSemestre() {
        return semestre;
    }

    public LocalDateTime getInicio() {
        return inicio;
    }

    public LocalDateTime getFim() {
        return fim;
    }

    public SituacaoPeriodo getSituacao() {
        return situacao;
    }

    public List<Oferta> getOfertas() {
        return Collections.unmodifiableList(ofertas);
    }

    void adicionarOferta(Oferta o) {
        if (!ofertas.contains(o)) {
            ofertas.add(o);
            o.definirPeriodo(this);
        }
    }

    public boolean estaAberto() {
        LocalDateTime agora = LocalDateTime.now();
        return situacao == SituacaoPeriodo.ABERTO && !agora.isBefore(inicio) && !agora.isAfter(fim);
    }

    public void abrir() {
        situacao = SituacaoPeriodo.ABERTO;
    }

    public void encerrar() {
        situacao = SituacaoPeriodo.ENCERRADO;
    }
}
