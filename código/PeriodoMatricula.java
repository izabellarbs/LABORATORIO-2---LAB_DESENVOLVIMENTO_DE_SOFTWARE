import java.time.LocalDateTime;
import java.util.List;

public class PeriodoMatricula {
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private SituacaoPeriodo situacao;
    private List<Oferta> ofertas;

    public boolean estaAberto() { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
    public void abrir() { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
    public void encerrar() { throw new UnsupportedOperationException("Implementar na Sprint 3"); }
}
