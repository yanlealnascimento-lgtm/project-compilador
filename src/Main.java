import scanner.Scanner;
import scanner.Token;
import parser.Parser;
import parser.Node;
import semantic.AnalisadorSemantico;
import ir.GeradorIR;
import ir.Instrucao;
import codegen.GeradorCodigo;
import codegen.MaquinaVirtual;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String caminho = args.length > 0 ? args[0] : "cod.txt";
        String programa = lerArquivo(caminho);

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

        System.out.println();
        System.out.println("execucao:");
        MaquinaVirtual vm = new MaquinaVirtual(bytecode);
        vm.executar();
    }

    private static String lerArquivo(String caminho) {
        StringBuilder conteudo = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(caminho));
            String linha;
            while ((linha = br.readLine()) != null) {
                conteudo.append(linha).append("\n");
            }
            br.close();
        } catch (IOException e) {
            throw new RuntimeException("erro pra ler o arquivo " + caminho);
        }

        return conteudo.toString();
    }
}