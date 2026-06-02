import scanner.Scanner;
import scanner.Token;
import parser.Parser;
import parser.Node;
import semantic.AnalisadorSemantico;
import ir.GeradorIR;
import ir.Instrucao;
import codegen.GeradorCodigo;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String programa = "int x;\n" +
                          "bool ativo;\n" +
                          "x = 10;\n" +
                          "if (x != 5) {\n" +
                          "    print x;\n" +
                          "}\n";

        System.out.println("fonte:");
        System.out.println(programa);

        Scanner scanner = new Scanner(programa);
        List<Token> tokens = scanner.tokenizar();

        System.out.println("tokens:");
        for (Token t : tokens) {
            System.out.println(t);
        }

        System.out.println();
        System.out.println("AST:");
        Parser p = new Parser(tokens);
        Node ast = p.parse();
        System.out.println(ast);

        System.out.println("analise semantica:");
        AnalisadorSemantico semantico = new AnalisadorSemantico();
        semantico.analisar(ast);
        System.out.println("sem erros semanticos");

        System.out.println();
        System.out.println("codigo intermediario:");
        GeradorIR gerador = new GeradorIR();
        List<Instrucao> ir = gerador.gerar(ast);
        for (Instrucao inst : ir) {
            System.out.println(inst);
        }

        System.out.println();
        System.out.println("bytecode:");
        GeradorCodigo gc = new GeradorCodigo();
        List<String> bytecode = gc.gerar(ast);
        for (String linha : bytecode) {
            System.out.println(linha);
        }

    }
}