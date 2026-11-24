package model;
import exception.CadastroInvalidoException;

public final class MembroPremium extends MembroClube {
    public MembroPremium(int id, String nome, String email) throws CadastroInvalidoException {
        super(id, nome, email);
    }
    @Override public String getPlano() { return "Premium"; }
    @Override protected int getMultiplicador() { return 2; }
}
