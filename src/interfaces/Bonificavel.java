package interfaces;

import exception.CadastroInvalidoException;
import exception.PontuacaoInsuficienteException;

public interface Bonificavel {
    void acumularPontos(int pontosBase) throws CadastroInvalidoException;
    void resgatarPontos(int pontos) throws PontuacaoInsuficienteException;
    int getPontos();

    default String consultarSaldo() {
        return "Saldo disponível: " + getPontos() + " pontos";
    }
}
