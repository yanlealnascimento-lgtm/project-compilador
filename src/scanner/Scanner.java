package scanner;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private String fonte;
    private int pos;
    private int linha;
    private List<Token> tokens;

    public Scanner(String fonte) {
        this.fonte = fonte;
        this.pos = 0;
        this.linha = 1;
        this.tokens = new ArrayList<>();
    }

    private char atual() {
        if (pos >= fonte.length()) return '\0';
        return fonte.charAt(pos);
    }

    private char proximo() {
        if (pos + 1 >= fonte.length()) return '\0';
        return fonte.charAt(pos + 1);
    }

    private void avanca() {
        if (pos < fonte.length()) {
            if (fonte.charAt(pos) == '\n') linha++;
            pos++;
        }
    }

    private void pulaBranco() {
        while (pos < fonte.length() && Character.isWhitespace(atual())) {
            avanca();
        }
    }

    private void pulaComentario() {
        while (pos < fonte.length() && atual() != '\n') {
            avanca();
        }
    }

    private Token leNumero() {
        int l = linha;
        String num = "";
        while (pos < fonte.length() && Character.isDigit(atual())) {
            num += atual();
            avanca();
        }
        return new Token(TokenType.NUMBER, num, l);
    }

    private Token leString() {
        int l = linha;
        avanca();
        String s = "";
        while (pos < fonte.length() && atual() != '"') {
            s += atual();
            avanca();
        }
        avanca();
        return new Token(TokenType.STRING, s, l);
    }

    private Token leIdentificador() {
        int l = linha;
        String palavra = "";
        while (pos < fonte.length() && (Character.isLetterOrDigit(atual()) || atual() == '_')) {
            palavra += atual();
            avanca();
        }

        if (palavra.equals("if")) return new Token(TokenType.IF, palavra, l);
        if (palavra.equals("while")) return new Token(TokenType.WHILE, palavra, l);
        if (palavra.equals("print")) return new Token(TokenType.PRINT, palavra, l);
        if (palavra.equals("int")) return new Token(TokenType.INT, palavra, l);
        if (palavra.equals("bool")) return new Token(TokenType.BOOL, palavra, l);
        if (palavra.equals("else")) return new Token(TokenType.ELSE, palavra, l);
        if (palavra.equals("true")) return new Token(TokenType.TRUE, palavra, l);
        if (palavra.equals("false")) return new Token(TokenType.FALSE, palavra, l);
        if (palavra.equals("read")) return new Token(TokenType.READ, palavra, l);

        return new Token(TokenType.IDENTIFIER, palavra, l);
    }

    public List<Token> tokenizar() {
        while (pos < fonte.length()) {
            pulaBranco();
            if (pos >= fonte.length()) break;

            if (atual() == '/' && proximo() == '/') {
                pulaComentario();
                continue;
            }

            char c = atual();
            int l = linha;

            if (Character.isDigit(c)) {
                tokens.add(leNumero());
                continue;
            }

            if (c == '"') {
                tokens.add(leString());
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                tokens.add(leIdentificador());
                continue;
            }

            if (c == '+') {
                tokens.add(new Token(TokenType.PLUS, "+", l));
                avanca();
            } else if (c == '(') {
                tokens.add(new Token(TokenType.LPAREN, "(", l));
                avanca();
            } else if (c == ')') {
                tokens.add(new Token(TokenType.RPAREN, ")", l));
                avanca();
            } else if (c == '=') {
                if (proximo() == '=') {
                    tokens.add(new Token(TokenType.EQ, "==", l));
                    avanca(); avanca();
                } else {
                    tokens.add(new Token(TokenType.ASSIGN, "=", l));
                    avanca();
                }
            } else if (c == '-') {
                tokens.add(new Token(TokenType.MINUS, "-", l));
                avanca();
            } else if (c == '{') {
                tokens.add(new Token(TokenType.LBRACE, "{", l));
                avanca();
            } else if (c == '}') {
                tokens.add(new Token(TokenType.RBRACE, "}", l));
                avanca();
            } else if (c == '*') {
                tokens.add(new Token(TokenType.TIMES, "*", l));
                avanca();
            } else if (c == '!') {
                if (proximo() == '=') {
                    tokens.add(new Token(TokenType.NEQ, "!=", l));
                    avanca(); avanca();
                } else {
                    System.out.println("caractere invalido: " + c + " na linha " + l);
                    avanca();
                }
            } else if (c == '<') {
                tokens.add(new Token(TokenType.LT, "<", l));
                avanca();
            } else if (c == ';') {
                tokens.add(new Token(TokenType.SEMICOLON, ";", l));
                avanca();
            } else if (c == '>') {
                tokens.add(new Token(TokenType.GT, ">", l));
                avanca();
            } else if (c == '/') {
                tokens.add(new Token(TokenType.DIVIDE, "/", l));
                avanca();
            } else {
                System.out.println("caractere invalido: " + c + " na linha " + l);
                avanca();
            }
        }

        tokens.add(new Token(TokenType.EOF, "", linha));
        return tokens;
    }
}
