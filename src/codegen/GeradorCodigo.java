package codegen;

import parser.Node;
import java.util.ArrayList;
import java.util.List;

public class GeradorCodigo {
    private List<String> bytecode;
    private int nLabel;

    public GeradorCodigo() {
        bytecode = new ArrayList<>();
        nLabel = 0;
    }

    private String novoLabel() {
        return "L" + nLabel++;
    }

    private void emite(String inst) {
        bytecode.add(inst);
    }

    public List<String> gerar(Node no) {
        geraStmt(no);
        emite("HALT");
        return bytecode;
    }

    private void geraStmt(Node no) {
        if (no.tipo.equals("programa") || no.tipo.equals("bloco")) {
            for (Node filho : no.filhos) geraStmt(filho);

        } else if (no.tipo.equals("atribuicao")) {
            geraExpr(no.filhos.get(1));
            emite("STORE " + no.filhos.get(0).valor);

        } else if (no.tipo.equals("if")) {
            String lThen = novoLabel();
            String lFim = novoLabel();
            String lElse = (no.filhos.size() == 3) ? novoLabel() : null;

            geraExpr(no.filhos.get(0));
            emite("JNZ " + lThen);
            emite("JMP " + (lElse != null ? lElse : lFim));
            emite("LABEL " + lThen);
            geraStmt(no.filhos.get(1));

            if (lElse != null) {
                emite("JMP " + lFim);
                emite("LABEL " + lElse);
                geraStmt(no.filhos.get(2));
            }

            emite("LABEL " + lFim);

        } else if (no.tipo.equals("while")) {
            String rotInicio = novoLabel();
            String rotCorpo = novoLabel();
            String rotFim = novoLabel();

            emite("LABEL " + rotInicio);
            geraExpr(no.filhos.get(0));
            emite("JNZ " + rotCorpo);
            emite("JMP " + rotFim);
            emite("LABEL " + rotCorpo);
            geraStmt(no.filhos.get(1));
            emite("JMP " + rotInicio);
            emite("LABEL " + rotFim);

        } else if (no.tipo.equals("print")) {
            geraExpr(no.filhos.get(0));
            emite("PRINT");

        } else if (no.tipo.equals("read")) {
            emite("READ " + no.filhos.get(0).valor);
        }
    }

    private void geraExpr(Node no) {
        if (no.tipo.equals("numero")) {
            emite("PUSH " + no.valor);
            return;
        }

        if (no.tipo.equals("booleano")) {
            int val = no.valor.equals("true") ? 1 : 0;
            emite("PUSH " + val);
            return;
        }

        if (no.tipo.equals("id")) {
            emite("LOAD " + no.valor);
            return;
        }

        if (no.tipo.equals("op")) {
            geraExpr(no.filhos.get(0));
            geraExpr(no.filhos.get(1));
            String op = no.valor;
            if (op.equals("+")) emite("ADD");
            else if (op.equals("-")) emite("SUB");
            else if (op.equals("*")) emite("MUL");
            else if (op.equals("/")) emite("DIV");
            else if (op.equals("==")) emite("EQ");
            else if (op.equals("!=")) emite("NEQ");
            else if (op.equals("<")) emite("LT");
            else if (op.equals(">")) emite("GT");
        }
    }
}
