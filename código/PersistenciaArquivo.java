import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

public final class PersistenciaArquivo {
    private PersistenciaArquivo(){}
    public static void salvar(Path arquivo,SistemaMatriculas s){
        List<String> l=new ArrayList<>();
        for(Secretaria x:s.getSecretarias()) l.add(linha("SECRETARIA",x.getId(),x.getNome(),x.getLogin(),x.getSal(),x.getHashSenha()));
        for(Aluno x:s.getAlunos()) l.add(linha("ALUNO",x.getId(),x.getNome(),x.getLogin(),x.getSal(),x.getHashSenha(),x.getMatricula()));
        for(Professor x:s.getProfessores()) l.add(linha("PROFESSOR",x.getId(),x.getNome(),x.getLogin(),x.getSal(),x.getHashSenha(),x.getRegistroFuncional()));
        for(Curso x:s.getCursos()) l.add(linha("CURSO",x.getId(),x.getNome(),x.getCreditos()));
        for(Disciplina x:s.getDisciplinas()) l.add(linha("DISCIPLINA",x.getCodigo(),x.getNome(),x.getCreditos(),x.getCurso().getId()));
        for(PeriodoMatricula x:s.getPeriodos()) l.add(linha("PERIODO",x.getSemestre(),x.getInicio(),x.getFim(),x.getSituacao()));
        for(Oferta x:s.getOfertas()) l.add(linha("OFERTA",x.getId(),x.getSemestre(),x.getTipo(),x.getDisciplina().getCodigo(),x.getProfessor().getId(),x.getSituacao()));
        for(Aluno a:s.getAlunos()) for(Matricula m:a.getMatriculas()) l.add(linha("MATRICULA",m.getId(),a.getId(),m.getOferta().getId(),m.getData(),m.getSituacao()));
        try{ Path d=arquivo.toAbsolutePath(); Files.createDirectories(d.getParent()); Files.write(d,l,StandardCharsets.UTF_8,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING); }
        catch(IOException e){throw new IllegalStateException("Falha ao salvar dados",e);}
    }
    public static SistemaMatriculas carregar(Path arquivo,Path cobrancas){
        try{ List<String> l=Files.readAllLines(arquivo,StandardCharsets.UTF_8); SistemaMatriculas s=new SistemaMatriculas(cobrancas);
            for(String z:l){String[] p=campos(z); if(p.length==0)continue; switch(p[0]){
                case "SECRETARIA":s.cadastrarSecretaria(new Secretaria(Long.parseLong(p[1]),p[2],p[3],p[4],p[5]));break;
                case "ALUNO":s.cadastrarAluno(new Aluno(Long.parseLong(p[1]),p[2],p[3],p[4],p[5],p[6]));break;
                case "PROFESSOR":s.cadastrarProfessor(new Professor(Long.parseLong(p[1]),p[2],p[3],p[4],p[5],p[6]));break;
                case "CURSO":s.cadastrarCurso(new Curso(Long.parseLong(p[1]),p[2],Integer.parseInt(p[3])));break;}}
            for(String z:l){String[] p=campos(z); if(p.length>0&&p[0].equals("DISCIPLINA")) s.cadastrarDisciplina(new Disciplina(p[1],p[2],Integer.parseInt(p[3]),curso(s,Long.parseLong(p[4]))));}
            for(String z:l){String[] p=campos(z); if(p.length>0&&p[0].equals("OFERTA")) s.criarOferta(new Oferta(Long.parseLong(p[1]),p[2],TipoOferta.valueOf(p[3]),disc(s,p[4]),prof(s,Long.parseLong(p[5])),SituacaoOferta.valueOf(p[6])));}
            for(String z:l){String[] p=campos(z); if(p.length>0&&p[0].equals("PERIODO")) s.definirPeriodo(new PeriodoMatricula(p[1],LocalDateTime.parse(p[2]),LocalDateTime.parse(p[3]),SituacaoPeriodo.valueOf(p[4])));}
            for(String z:l){String[] p=campos(z); if(p.length>0&&p[0].equals("MATRICULA")){Aluno a=aluno(s,Long.parseLong(p[2])); Oferta o=oferta(s,Long.parseLong(p[3])); Matricula m=new Matricula(Long.parseLong(p[1]),a,o,LocalDateTime.parse(p[4]),SituacaoMatricula.valueOf(p[5])); a.adicionar(m);o.adicionarMatriculaCarregada(m);}}
            s.atualizarProximoId(); return s;
        }catch(IOException e){throw new IllegalStateException("Não foi possível ler os dados",e);}
    }
    private static Curso curso(SistemaMatriculas s,long id){for(Curso x:s.getCursos())if(x.getId()==id)return x;throw new IllegalArgumentException("Curso não encontrado: "+id);}
    private static Professor prof(SistemaMatriculas s,long id){for(Professor x:s.getProfessores())if(x.getId()==id)return x;throw new IllegalArgumentException("Professor não encontrado: "+id);}
    private static Aluno aluno(SistemaMatriculas s,long id){for(Aluno x:s.getAlunos())if(x.getId()==id)return x;throw new IllegalArgumentException("Aluno não encontrado: "+id);}
    private static Oferta oferta(SistemaMatriculas s,long id){for(Oferta x:s.getOfertas())if(x.getId()==id)return x;throw new IllegalArgumentException("Oferta não encontrada: "+id);}
    private static Disciplina disc(SistemaMatriculas s,String cod){for(Disciplina x:s.getDisciplinas())if(x.getCodigo().equalsIgnoreCase(cod))return x;throw new IllegalArgumentException("Disciplina não encontrada: "+cod);}
    private static String linha(Object...v){StringJoiner j=new StringJoiner(";");for(Object x:v)j.add(esc(String.valueOf(x)));return j.toString();}
    private static String esc(String v){return v.replace("\\","\\\\").replace(";","\\;").replace("\n","\\n").replace("\r","");}
    private static String[] campos(String z){if(z==null||z.trim().isEmpty())return new String[0];List<String> r=new ArrayList<>();StringBuilder a=new StringBuilder();boolean e=false;for(char c:z.toCharArray()){if(e){a.append(c=='n'?'\n':c);e=false;}else if(c=='\\')e=true;else if(c==';'){r.add(a.toString());a.setLength(0);}else a.append(c);}if(e)a.append('\\');r.add(a.toString());return r.toArray(new String[0]);}
}
