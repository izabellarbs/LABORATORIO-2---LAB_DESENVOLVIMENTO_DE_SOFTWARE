import java.io.Serializable;
import java.time.LocalDateTime;
public class Matricula implements Serializable {
    private static final long serialVersionUID=1L;
    private final Long id; private final LocalDateTime data; private SituacaoMatricula situacao;
    private final Aluno aluno; private final Oferta oferta;
    public Matricula(Long id,Aluno aluno,Oferta oferta) {
        this(id,aluno,oferta,LocalDateTime.now(),SituacaoMatricula.ATIVA);
    }
    Matricula(Long id,Aluno aluno,Oferta oferta,LocalDateTime data,SituacaoMatricula situacao) {
        this.id=id; this.aluno=aluno; this.oferta=oferta; this.data=data; this.situacao=situacao;
    }
    public Long getId(){return id;} public Aluno getAluno(){return aluno;}
    public Oferta getOferta(){return oferta;} public LocalDateTime getData(){return data;}
    public SituacaoMatricula getSituacao(){return situacao;}
    public void cancelar(){situacao=SituacaoMatricula.CANCELADA;}
}
