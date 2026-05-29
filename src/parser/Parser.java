package parser;

import scanner.Token;
import scanner.TokenType;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
    }

    private Token atual() {
        return tokens.get(pos);
    }

    private boolean veja(TokenType tipo) {
        return atual().tipo == tipo;
    }

    private Token consome(TokenType tipo) {
        Token t = atual();
        if (t.tipo != tipo) {
            throw new RuntimeException("erro na linha " + t.linha + ": esperado " + tipo + " mas veio " + t.tipo);
        }
        pos++;
        return t;
    }

    public Node parse() {
        Node programa = new Node("programa");
        while (!veja(TokenType.EOF)) {
            programa.addFilho(parseStatement());
        }
        return programa;
    }

    private Node parseStatement() {
        if (veja(TokenType.INT) || veja(TokenType.BOOL)) {
            return parseDeclaracao();
        } else if (veja(TokenType.IDENTIFIER)) {
            return parseAtribuicao();
        } else if (veja(TokenType.IF)) {
            return parseIf();
        } else if (veja(TokenType.WHILE)) {
            return parseWhile();
        } else if (veja(TokenType.PRINT)) {
            return parsePrint();
        } else if (veja(TokenType.READ)) {
            return parseRead();
        } else {
            throw new RuntimeException("statement invalido na linha " + atual().linha);
        }
    }

    private Node parseDeclaracao() {
        Node decl = new Node("declaracao");
        Token tipo = atual();
        pos++;
        decl.addFilho(new Node("tipo", tipo.valor));
        Token nome = consome(TokenType.IDENTIFIER);
        decl.addFilho(new Node("id", nome.valor));
        consome(TokenType.SEMICOLON);
        return decl;
    }

    private Node parseAtribuicao() {
        Node atrib = new Node("atribuicao");
        Token nome = consome(TokenType.IDENTIFIER);
        atrib.addFilho(new Node("id", nome.valor));
        consome(TokenType.ASSIGN);
        atrib.addFilho(parseExpr());
        consome(TokenType.SEMICOLON);
        return atrib;
    }

    private Node parseIf() {
        Node ifNode = new Node("if");
        consome(TokenType.IF);
        consome(TokenType.LPAREN);
        ifNode.addFilho(parseExpr());
        consome(TokenType.RPAREN);
        ifNode.addFilho(parseBloco());
        if (veja(TokenType.ELSE)) {
            pos++;
            ifNode.addFilho(parseBloco());
        }
        return ifNode;
    }

    private Node parseWhile() {
        Node whileNode = new Node("while");
        consome(TokenType.WHILE);
        consome(TokenType.LPAREN);
        whileNode.addFilho(parseExpr());
        consome(TokenType.RPAREN);
        whileNode.addFilho(parseBloco());
        return whileNode;
    }

    private Node parsePrint() {
        Node printNode = new Node("print");
        consome(TokenType.PRINT);
        printNode.addFilho(parseExpr());
        consome(TokenType.SEMICOLON);
        return printNode;
    }

    private Node parseRead() {
        Node readNode = new Node("read");
        consome(TokenType.READ);
        Token nome = consome(TokenType.IDENTIFIER);
        readNode.addFilho(new Node("id", nome.valor));
        consome(TokenType.SEMICOLON);
        return readNode;
    }

    private Node parseBloco() {
        Node bloco = new Node("bloco");
        consome(TokenType.LBRACE);
        while (!veja(TokenType.RBRACE) && !veja(TokenType.EOF)) {
            bloco.addFilho(parseStatement());
        }
        consome(TokenType.RBRACE);
        return bloco;
    }

    private Node parseExpr() {
        return parseComparacao();
    }

    private Node parseComparacao() {
        Node esq = parseAdicao();

        if (veja(TokenType.EQ) || veja(TokenType.NEQ) || veja(TokenType.LT) || veja(TokenType.GT)) {
            Token op = atual();
            pos++;
            Node dir = parseAdicao();
            Node node = new Node("op", op.valor);
            node.addFilho(esq);
            node.addFilho(dir);
            return node;
        }

        return esq;
    }

    private Node parseAdicao() {
        Node esq = parseTermo();

        while (veja(TokenType.PLUS) || veja(TokenType.MINUS)) {
            Token op = atual();
            pos++;
            Node dir = parseTermo();
            Node node = new Node("op", op.valor);
            node.addFilho(esq);
            node.addFilho(dir);
            esq = node;
        }

        return esq;
    }

    private Node parseTermo() {
        Node esq = parseFator();

        while (veja(TokenType.TIMES) || veja(TokenType.DIVIDE)) {
            Token op = atual();
            pos++;
            Node dir = parseFator();
            Node node = new Node("op", op.valor);
            node.addFilho(esq);
            node.addFilho(dir);
            esq = node;
        }

        return esq;
    }

    private Node parseFator() {
        Token t = atual();

        if (veja(TokenType.NUMBER)) {
            pos++;
            return new Node("numero", t.valor);
        } else if (veja(TokenType.TRUE) || veja(TokenType.FALSE)) {
            pos++;
            return new Node("booleano", t.valor);
        } else if (veja(TokenType.IDENTIFIER)) {
            pos++;
            return new Node("id", t.valor);
        } else if (veja(TokenType.LPAREN)) {
            pos++;
            Node expr = parseExpr();
            consome(TokenType.RPAREN);
            return expr;
        } else {
            throw new RuntimeException("fator invalido: " + t.tipo + " na linha " + t.linha);
        }
    }
}
