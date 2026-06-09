package codegen;

import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class MaquinaVirtual {
    private List<String> instrucoes;
    private Stack<Integer> pilha;
    private HashMap<String, Integer> memoria;
    private HashMap<String, Integer> rotulos;
    private int ip;

    public MaquinaVirtual(List<String> bytecode) {
        this.instrucoes = bytecode;
        this.pilha = new Stack<>();
        this.memoria = new HashMap<>();
        this.rotulos = new HashMap<>();
        this.ip = 0;
    }

    private void resolveRotulos() {
        for (int i = 0; i < instrucoes.size(); i++) {
            String inst = instrucoes.get(i);
            if (inst.startsWith("LABEL ")) {
                rotulos.put(inst.substring(6), i);
            }
        }
    }

    public void executar() {
        resolveRotulos();
        Scanner entrada = new Scanner(System.in);

        while (ip < instrucoes.size()) {
            String inst = instrucoes.get(ip);
            ip++;

            if (inst.startsWith("PUSH ")) {
                pilha.push(Integer.parseInt(inst.substring(5)));

            } else if (inst.startsWith("LOAD ")) {
                pilha.push(memoria.getOrDefault(inst.substring(5), 0));

            } else if (inst.startsWith("STORE ")) {
                memoria.put(inst.substring(6), pilha.pop());

            } else if (inst.equals("ADD")) {
                int b = pilha.pop(), a = pilha.pop();
                pilha.push(a + b);

            } else if (inst.equals("SUB")) {
                int b = pilha.pop();
                int a = pilha.pop();
                pilha.push(a - b);

            } else if (inst.equals("MUL")) {
                int b = pilha.pop(), a = pilha.pop();
                pilha.push(a * b);

            } else if (inst.equals("DIV")) {
                int divisor = pilha.pop();
                int dividendo = pilha.pop();
                pilha.push(dividendo / divisor);

            } else if (inst.equals("EQ")) {
                int b = pilha.pop();
                int a = pilha.pop();
                pilha.push(a == b ? 1 : 0);

            } else if (inst.equals("NEQ")) {
                int b = pilha.pop(), a = pilha.pop();
                pilha.push(a != b ? 1 : 0);

            } else if (inst.equals("LT")) {
                int b = pilha.pop(), a = pilha.pop();
                boolean menor = a < b;
                pilha.push(menor ? 1 : 0);

            } else if (inst.equals("GT")) {
                int b = pilha.pop(), a = pilha.pop();
                pilha.push(a > b ? 1 : 0);

            } else if (inst.startsWith("JMP ")) {
                ip = rotulos.get(inst.substring(4));

            } else if (inst.startsWith("JNZ ")) {
                if (pilha.pop() != 0) ip = rotulos.get(inst.substring(4));

            } else if (inst.startsWith("LABEL ")) {
                // Não executa nada
            } else if (inst.equals("PRINT")) {
                System.out.println(pilha.pop());

            } else if (inst.startsWith("READ ")) {
                String var = inst.substring(5);
                System.out.print("entrada para " + var + ": ");
                memoria.put(var, entrada.nextInt());

            } else if (inst.equals("HALT")) {
                break;
            }
        }

        entrada.close();
    }
}
