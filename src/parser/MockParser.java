package parser;

import scanner.Token;
import java.util.List;

public class MockParser {
    private List<Token> tokens;

    public MockParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        System.out.println("parser não implementado ainda, tokens recebidos: " + tokens.size());
        for (Token t : tokens) {
            System.out.println(t);
        }
    }
}
