package scanner;

public class Token {
    public TokenType tipo;
    public String valor;
    public int linha;

    public Token(TokenType tipo, String valor, int linha) {
        this.tipo = tipo;
        this.valor = valor;
        this.linha = linha;
    }

    public String toString() {
        return tipo + "(" + valor + ") linha:" + linha;
    }
}
