package model;

import exception.CadastroInvalidoException;
import exception.PontuacaoInsuficienteException;
import interfaces.Bonificavel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public abstract class MembroClube implements Comparable<MembroClube>, Bonificavel {
    private final int id;
    private String nome;
    private final String email;
    private int pontos;
    private final List<String> historico = new ArrayList<>();

    protected MembroClube(int id, String nome, String email) throws CadastroInvalidoException {
        if (id <= 0) throw new CadastroInvalidoException("O ID deve ser positivo.");
        if (email == null || !email.trim().matches("[^\\s@]+@[^\\s@]+\\.[^\\s@]+")) {
            throw new CadastroInvalidoException("Informe um e-mail válido, como nome@exemplo.com.");
        }
        this.id = id;
        setNome(nome);
        this.email = email.trim().toLowerCase(Locale.ROOT);
        registrar("Adesão ao clube. Saldo inicial: 0 pontos.");
    }

    public abstract String getPlano();
    protected abstract int getMultiplicador();
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    @Override public int getPontos() { return pontos; }
    public List<String> getHistorico() { return List.copyOf(historico); }

    public final void setNome(String nome) throws CadastroInvalidoException {
        if (nome == null || nome.trim().length() < 3) {
            throw new CadastroInvalidoException("O nome deve ter pelo menos 3 caracteres.");
        }
        this.nome = nome.trim();
    }

    @Override public void acumularPontos(int pontosBase) throws CadastroInvalidoException {
        long credito = (long) pontosBase * getMultiplicador();
        if (pontosBase <= 0 || credito + pontos > Integer.MAX_VALUE) {
            throw new CadastroInvalidoException("Informe pontos positivos dentro do limite do saldo (" + Integer.MAX_VALUE + ").");
        }
        pontos += (int) credito;
        registrar("Crédito: +" + credito + " pontos (base " + pontosBase + ", fator " + getMultiplicador() + "x). Saldo: " + pontos);
    }

    @Override public void resgatarPontos(int quantidade) throws PontuacaoInsuficienteException {
        if (quantidade <= 0) throw new PontuacaoInsuficienteException("O resgate deve ser positivo.");
        if (quantidade > pontos) {
            throw new PontuacaoInsuficienteException("Saldo insuficiente: você tem " + pontos + " pontos e precisa de " + quantidade + ".");
        }
        pontos -= quantidade;
        registrar("Resgate: -" + quantidade + " pontos. Saldo: " + pontos);
    }

    public void resgatarBeneficio(Beneficio beneficio) throws PontuacaoInsuficienteException {
        if (beneficio == null) throw new PontuacaoInsuficienteException("Selecione um benefício.");
        // A operação só registra o benefício depois que o débito for validado.
        resgatarPontos(beneficio.getCusto());
        registrar("Benefício entregue: " + beneficio.getDescricao());
    }

    private void registrar(String descricao) {
        historico.add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " | " + descricao);
    }

    // A identidade é imutável: ordenação natural, equals e hashCode usam o ID.
    @Override public final int compareTo(MembroClube outro) { return Integer.compare(id, outro.id); }
    @Override public final boolean equals(Object objeto) {
        if (this == objeto) return true;
        if (!(objeto instanceof MembroClube)) return false;
        return id == ((MembroClube) objeto).id;
    }
    @Override public final int hashCode() { return Integer.hashCode(id); }
    @Override public String toString() {
        return "#" + id + " | " + nome + " | " + getPlano() + " | " + pontos + " pontos";
    }
}
