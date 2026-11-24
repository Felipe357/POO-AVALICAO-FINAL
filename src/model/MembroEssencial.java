package model;
import exception.CadastroInvalidoException;

public final class MembroEssencial extends MembroClube {
    public MembroEssencial(int id, String nome, String email) throws CadastroInvalidoException {
        super(id, nome, email);
    }
    @Override public String getPlano() { return "Essencial"; }
    @Override protected int getMultiplicador() { return 1; }
}
