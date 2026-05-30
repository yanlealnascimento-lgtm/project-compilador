package semantic;

import parser.Node;

public class AnalisadorSemantico {
    private TabelaSimbolos tabela;

    public AnalisadorSemantico() {
        tabela = new TabelaSimbolos();
    }

    public void analisar(Node no) {
        if (no.tipo.equals("programa")) {
            for (Node filho : no.filhos) analisar(filho);

        } else if (no.tipo.equals("declaracao")) {
            String t = no.filhos.get(0).valor;
            String v = no.filhos.get(1).valor;
            tabela.declarar(v, t);

        } else if (no.tipo.equals("atribuicao")) {
            String nome = no.filhos.get(0).valor;
            String tipoEsperado = tabela.buscar(nome);
            String tipoRecebido = tipoExpr(no.filhos.get(1));
            if (!tipoEsperado.equals(tipoRecebido)) {
                throw new RuntimeException("atribuicao invalida: '" + nome + "' e do tipo " + tipoEsperado + " mas recebeu " + tipoRecebido);
            }

        } else if (no.tipo.equals("if")) {
            tipoExpr(no.filhos.get(0));
            tabela.abreEscopo();
            analisar(no.filhos.get(1));
            tabela.fechaEscopo();
            if (no.filhos.size() == 3) {
                tabela.abreEscopo();
                analisar(no.filhos.get(2));
                tabela.fechaEscopo();
            }

        } else if (no.tipo.equals("while")) {
            tipoExpr(no.filhos.get(0));
            tabela.abreEscopo();
            analisar(no.filhos.get(1));
            tabela.fechaEscopo();

        } else if (no.tipo.equals("bloco")) {
            for (Node filho : no.filhos) analisar(filho);

        } else if (no.tipo.equals("print")) {
            tipoExpr(no.filhos.get(0));

        } else if (no.tipo.equals("read")) {
            String nomeVar = no.filhos.get(0).valor;
            tabela.buscar(nomeVar);
        }
    }

    private String tipoExpr(Node no) {
        if (no.tipo.equals("numero")) return "int";
        if (no.tipo.equals("booleano")) return "bool";

        if (no.tipo.equals("id")) {
            return tabela.buscar(no.valor);
        }

        if (no.tipo.equals("op")) {
            String tipoEsq = tipoExpr(no.filhos.get(0));
            String tipoDir = tipoExpr(no.filhos.get(1));

            if (no.valor.equals("+") || no.valor.equals("-") || no.valor.equals("*") || no.valor.equals("/")) {
                if (!tipoEsq.equals("int") || !tipoDir.equals("int")) {
                    throw new RuntimeException("operacao '" + no.valor + "' so funciona com int");
                }
                return "int";
            }

            if (no.valor.equals("==") || no.valor.equals("!=")) {
                if (!tipoEsq.equals(tipoDir)) {
                    throw new RuntimeException("nao da pra comparar " + tipoEsq + " com " + tipoDir);
                }
                return "bool";
            }

            if (no.valor.equals("<") || no.valor.equals(">")) {
                if (!tipoEsq.equals("int") || !tipoDir.equals("int")) {
                    throw new RuntimeException("comparacao de ordem so funciona com int");
                }
                return "bool";
            }
        }

        throw new RuntimeException("nao sei o tipo de: " + no.tipo);
    }
}