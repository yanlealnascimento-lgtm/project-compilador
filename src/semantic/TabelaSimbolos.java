package semantic;

import java.util.HashMap;
import java.util.Stack;

public class TabelaSimbolos {
    private Stack<HashMap<String, String>> escopos;

    public TabelaSimbolos() {
        escopos = new Stack<>();
        escopos.push(new HashMap<>());
    }

    public void abreEscopo() {
        escopos.push(new HashMap<>());
    }

    public void fechaEscopo() {
        escopos.pop();
    }

    public void declarar(String nome, String tipo) {
        if (escopos.peek().containsKey(nome)) {
            throw new RuntimeException("variavel '" + nome + "' ja foi declarada neste escopo");
        }
        escopos.peek().put(nome, tipo);
    }

    public String buscar(String nome) {
        for (int i = escopos.size() - 1; i >= 0; i--) {
            if (escopos.get(i).containsKey(nome)) {
                return escopos.get(i).get(nome);
            }
        }
        throw new RuntimeException("'" + nome + "' nao foi declarado");
    }
}