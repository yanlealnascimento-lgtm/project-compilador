import scanner.Scanner;
import scanner.Token;
import parser.MockParser;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String programa = "int x;\n" +
                          "bool ativo;\n" +
                          "x = 10;\n" +
                          "if (x != 5) {\n" +
                          "    print x;\n" +
                          "}\n";

        System.out.println("fonte: ");
        System.out.println(programa);

        Scanner scanner = new Scanner(programa);
        List<Token> tokens = scanner.tokenizar();

        System.out.println("tokens:");
        for (Token t : tokens) {
            System.out.println(t);
        }

        System.out.println();
        MockParser p = new MockParser(tokens);
        p.parse();
    }
}
