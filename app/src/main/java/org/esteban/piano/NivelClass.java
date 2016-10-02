package org.esteban.piano;

/**
 * Created by Duarte Amaral on 08/05/2016.
 */
public class NivelClass {
    private Integer nivel;
    private String nome;
    private String sequencia_notas;
    private Integer pontuacao;

    public Integer getNivel() {
        return nivel;
    }

    public void setNivel(Integer nivel) {
        this.nivel = nivel;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String _nome) {
        this.nome = _nome;
    }

    public String getSequenciaNotas() {
        return sequencia_notas;
    }

    public void setSequenciaNotas(String _sequenciaNotas) {
        // 1x2x3x4x5x6x7
        // Split Sequencia Nota
        this.sequencia_notas = _sequenciaNotas;
    }

    public Integer getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Integer _pontuacao) {
        this.pontuacao = _pontuacao;
    }
}
