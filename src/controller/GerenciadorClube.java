package controller;

import exception.CadastroInvalidoException;
import exception.PontuacaoInsuficienteException;
import interfaces.Bonificavel;
import model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class GerenciadorClube {
    private final List<MembroClube> membros = new ArrayList<>();
    private int proximoId = 1;

    public MembroClube cadastrar(String nome, String email, boolean premium) throws CadastroInvalidoException {
        for (MembroClube membro : membros) {
            if (email != null && membro.getEmail().equalsIgnoreCase(email.trim())) {
                throw new CadastroInvalidoException("Este e-mail já pertence a um membro do clube.");
            }
        }
        MembroClube novo = premium ? new MembroPremium(proximoId, nome, email)
                                  : new MembroEssencial(proximoId, nome, email);
        membros.add(novo);
        proximoId++;
        return novo;
    }

    public MembroClube buscarPorId(int id) throws CadastroInvalidoException {
        for (MembroClube membro : membros) {
            if (membro.getId() == id) return membro;
        }
        throw new CadastroInvalidoException("Nenhum membro encontrado com o ID " + id + ".");
    }

    public List<MembroClube> pesquisar(String termo, boolean ranking) {
        String filtro = termo == null ? "" : termo.trim().toLowerCase(Locale.ROOT);
        List<MembroClube> resultado = new ArrayList<>();
        for (MembroClube membro : listar(ranking)) {
            if (membro.getNome().toLowerCase(Locale.ROOT).contains(filtro)
                    || membro.getEmail().contains(filtro)
                    || String.valueOf(membro.getId()).equals(filtro)) resultado.add(membro);
        }
        return List.copyOf(resultado);
    }

    public List<MembroClube> listar(boolean ranking) {
        List<MembroClube> ordenados = new ArrayList<>(membros);
        Collections.sort(ordenados);
        if (ranking) {
            Collections.sort(ordenados, Comparator.comparingInt(MembroClube::getPontos).reversed()
                    .thenComparingInt(MembroClube::getId));
        }
        return List.copyOf(ordenados);
    }

    public void acumular(int id, int pontosBase) throws CadastroInvalidoException {
        Bonificavel participante = buscarPorId(id);
        participante.acumularPontos(pontosBase); // Despacho polimórfico: Essencial 1x, Premium 2x.
    }

    public void resgatar(int id, Beneficio beneficio)
            throws CadastroInvalidoException, PontuacaoInsuficienteException {
        buscarPorId(id).resgatarBeneficio(beneficio);
    }

    public String emitirExtrato(int id) throws CadastroInvalidoException {
        MembroClube membro = buscarPorId(id);
        return membro + "\n" + membro.getEmail() + "\n" + membro.consultarSaldo()
                + "\n\n" + String.join("\n", membro.getHistorico());
    }

    public int getQuantidadeMembros() { return membros.size(); }
    public long getTotalPontos() {
        long total = 0;
        for (MembroClube membro : membros) total += membro.getPontos();
        return total;
    }
}
