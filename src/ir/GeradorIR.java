package ir;

import parser.Node;
import java.util.ArrayList;
import java.util.List;

public class GeradorIR {
    private List<Instrucao> codigo;
    private int nTemp;
    private int nLabel;

    public GeradorIR() {
        codigo = new ArrayList<>();
        nTemp = 0;
        nLabel = 0;
    }

    private String novoTemp() {
        return "t" + nTemp++;
    }

    private String novoLabel() {
        return "L" + nLabel++;
    }

    private void emite(String s) {
        codigo.add(new Instrucao(s));
    }

    public List<Instrucao> gerar(Node no) {
        geraStmt(no);
        return codigo;
    }

    private void geraStmt(Node no) {
        if (no.tipo.equals("programa") || no.tipo.equals("bloco")) {
            for (Node filho : no.filhos) geraStmt(filho);

        } else if (no.tipo.equals("atribuicao")) {
            String var = no.filhos.get(0).valor;
            String val = geraExpr(no.filhos.get(1));
            emite(var + " = " + val);

        } else if (no.tipo.equals("if")) {
            String cond = geraExpr(no.filhos.get(0));
            String labelThen = novoLabel();
            String labelFim = novoLabel();
            String labelElse = (no.filhos.size() == 3) ? novoLabel() : null;

            emite("if " + cond + " goto " + labelThen);
            emite("goto " + (labelElse != null ? labelElse : labelFim));
            emite(labelThen + ":");
            geraStmt(no.filhos.get(1));

            if (labelElse != null) {
                emite("goto " + labelFim);
                emite(labelElse + ":");
                geraStmt(no.filhos.get(2));
            }

            emite(labelFim + ":");

        } else if (no.tipo.equals("while")) {
            String inicio = novoLabel();
            String corpo = novoLabel();
            String fim = novoLabel();
            emite(inicio + ":");
            String cond = geraExpr(no.filhos.get(0));
            emite("if " + cond + " goto " + corpo);
            emite("goto " + fim);
            emite(corpo + ":");
            geraStmt(no.filhos.get(1));
            emite("goto " + inicio);
            emite(fim + ":");

        } else if (no.tipo.equals("print")) {
            String val = geraExpr(no.filhos.get(0));
            emite("print " + val);

        } else if (no.tipo.equals("read")) {
            emite("read " + no.filhos.get(0).valor);
        }
    }

    private String geraExpr(Node no) {
        if (no.tipo.equals("numero") || no.tipo.equals("booleano")) {
            return no.valor;
        }

        if (no.tipo.equals("id")) {
            return no.valor;
        }

        if (no.tipo.equals("op")) {
            String esq = geraExpr(no.filhos.get(0));
            String dir = geraExpr(no.filhos.get(1));
            String op = no.valor;
            String temp = novoTemp();
            emite(temp + " = " + esq + " " + op + " " + dir);
            return temp;
        }

        return "?";
    }
}
