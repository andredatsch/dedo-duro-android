package br.com.dedoduro.model;

/**
 * Created by gasparbarancelli on 11/06/16.
 */
public enum TipoDenuncia {

    ILUMINACAO("Iluminação"),
    PAVIMENTACAO("Pavimentação"),
    VANDALISMO("Vandalismo"),
    ESTACIONAMENTO("Estacionamento"),
    VIGILANCIA_SANITARIA("Vigiláncia Sanitária"),
    LIXO("Lixo"),
    OUTRA("Outra");

    private String label;

    TipoDenuncia(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
