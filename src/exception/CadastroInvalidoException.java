package exception;

public class CadastroInvalidoException extends Exception {
    private static final long serialVersionUID = 1L;
    public CadastroInvalidoException(String mensagem) { super(mensagem); }
}
