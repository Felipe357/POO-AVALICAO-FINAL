package model;

public enum Beneficio {
    CAFE("Café especial", 100),
    VALE("Vale-presente de R$ 25", 250),
    KIT("Kit exclusivo do clube", 500);

    private final String descricao;
    private final int custo;
    Beneficio(String descricao, int custo) {
        this.descricao = descricao;
        this.custo = custo;
    }
    public String getDescricao() { return descricao; }
    public int getCusto() { return custo; }
    @Override public String toString() { return descricao + " (" + custo + " pontos)"; }
}
