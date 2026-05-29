package parser;

import java.util.ArrayList;
import java.util.List;

public class Node {
    public String tipo;
    public String valor;
    public List<Node> filhos;

    public Node(String tipo, String valor) {
        this.tipo = tipo;
        this.valor = valor;
        this.filhos = new ArrayList<>();
    }

    public Node(String tipo) {
        this(tipo, "");
    }

    public void addFilho(Node filho) {
        filhos.add(filho);
    }

    public String toString() {
        return toString(0);
    }

    private String toString(int nivel) {
        String indent = "";
        for (int i = 0; i < nivel; i++) indent += "  ";

        String resultado = indent + tipo;
        if (!valor.isEmpty()) resultado += "(" + valor + ")";
        resultado += "\n";

        for (Node filho : filhos) {
            resultado += filho.toString(nivel + 1);
        }
        return resultado;
    }
}
