# Relatório - Compilador

Esse documento explica como o compilador foi feito, dividido nas 5 fases que o enunciado pede (léxica, sintática, semântica, código intermediário e código final).

A linguagem suportada tem:
- tipos `int` e `bool`
- declaração de variável e atribuição
- `if`/`else` e `while`
- operadores aritméticos (`+ - * /`) e relacionais (`== != < >`)
- `read` e `print` pra entrada/saída

Organização do projeto (pastas dentro de `src`):

```
scanner/   -> análise léxica
parser/    -> análise sintática (monta a AST)
semantic/  -> análise semântica (tabela de símbolos + checagem de tipo)
ir/        -> código intermediário (TAC)
codegen/   -> código final (bytecode + a VM que executa)
Main.java  -> junta tudo
```

## Análise léxica

O `Scanner` lê o código caractere por caractere e vai montando os tokens (palavra reservada, identificador, número, operador, etc). Ele também ignora espaço em branco e comentário de linha (`//`).

Cada `Token` guarda o tipo (um enum, `TokenType`), o valor (o texto em si) e o número da linha - isso é usado depois pra dar mensagem de erro mais clara no parser e na análise semântica.

## Análise sintática

O parser usa descida recursiva: cada regra da gramática virou um método (`parseDeclaracao`, `parseIf`, `parseWhile`, `parseExpr`...). A precedência dos operadores fica definida pela ordem das chamadas:

`parseExpr -> parseComparacao -> parseAdicao -> parseTermo -> parseFator`

ou seja `*` e `/` "amarram" mais forte que `+`/`-`, que por sua vez vêm antes de `==`, `!=`, `<`, `>`.

O resultado é a AST, montada com a classe `Node` (tem um tipo, um valor e uma lista de filhos).

## Análise semântica

Aqui a ideia é checar se o programa "faz sentido" antes de gerar qualquer código:

- toda variável precisa ter sido declarada antes de usar, e não pode ter duas declarações iguais no mesmo escopo
- `int` e `bool` não podem se misturar em conta ou em `<`/`>`. Já `==`/`!=` só exige que os dois lados sejam do mesmo tipo (então `bool == bool` é válido)

Pra controlar o escopo, a classe `TabelaSimbolos` usa uma pilha de tabelas - cada bloco `{ }` empilha uma tabela nova e desempilha quando o bloco acaba. Assim uma variável declarada dentro de um `if`/`while` não existe fora dele.

`AnalisadorSemantico` percorre a árvore inteira recursivamente, abrindo e fechando esses escopos, e descobre o tipo de cada expressão no método `tipoExpr`. Se algo não bate (variável não declarada, tipo errado...) é lançada uma exception com a mensagem do erro.

## Código intermediário (TAC)

Pra essa parte foi usado código de três endereços, que é o que normalmente é visto em aula. Cada expressão complexa quebra em passos simples usando temporários (`t0`, `t1`...), e `if`/`while` usam rótulos (`L0`, `L1`...) com `goto`.

Por exemplo, esse trecho:

```
while (i < x) {
    soma = soma + i;
}
```

vira mais ou menos isso:

```
L0:
t0 = i < x
if t0 goto L1
goto L2
L1:
t1 = soma + i
soma = t1
goto L0
L2:
```

Quem gera isso é a classe `GeradorIR`, percorrendo a AST e devolvendo uma lista de `Instrucao` (cada uma é só uma linha de texto do TAC).

## Código final

Em vez de Assembly (x86/MIPS), a gente decidiu gerar bytecode pra uma máquina virtual de pilha própria, escrita em Java. O enunciado permite essa opção e tem a vantagem de o código gerado dar pra executar de verdade, sem precisar de nenhuma ferramenta externa.

Instruções da VM:

- `PUSH n` - empilha a constante n
- `LOAD var` / `STORE var` - lê/escreve uma variável
- `ADD`, `SUB`, `MUL`, `DIV` - tira dois valores da pilha, opera e empilha o resultado
- `EQ`, `NEQ`, `LT`, `GT` - comparação, empilha 1 ou 0
- `JMP rot` / `JNZ rot` - salto incondicional / salto se o topo for diferente de zero
- `LABEL rot` - só marca uma posição, não faz nada quando executa
- `PRINT` - tira o topo da pilha e imprime
- `READ var` - lê um inteiro do teclado
- `HALT` - termina a execução

A classe `GeradorCodigo` gera essas instruções a partir da AST (parecido com o `GeradorIR`, mas já no formato da VM). E a `MaquinaVirtual` recebe essa lista, faz uma passada pra achar onde cada `LABEL` está (`resolveRotulos`) e depois executa tudo num laço, usando uma `Stack<Integer>` pra pilha e um `HashMap<String, Integer>` pra memória das variáveis.

Sobre otimização: o enunciado deixa isso como opcional/bônus e a gente não chegou a implementar por falta de tempo. Daria pra fazer coisas simples tipo tirar `JMP` que aponta pro rótulo seguinte ou propagar constante no TAC antes de gerar o bytecode final.

## Rodando o projeto

O código fonte de teste fica em `cod.txt`, na raiz. Pra compilar e rodar:

```
javac -d out $(find src -name "*.java")
java -cp out Main
```

Se quiser testar outro arquivo, passa o caminho como argumento:

```
java -cp out Main outro_arquivo.txt
```

A saída mostra, em ordem: o código fonte, os tokens, a AST, se passou na análise semântica, o código intermediário, o bytecode e por fim o resultado da execução na VM.

## Divisão do trabalho

- Yan Leal: scanner + parser (léxica e sintática)
- Erick de Souza: análise semântica (tabela de símbolos, checagem de tipo)
- Kauan Aparecido: código intermediário, bytecode e a máquina virtual

## Conclusão

No final o compilador passa por todas as fases pedidas, da leitura do arquivo até a execução do bytecode na VM, com checagem de tipo e escopo no meio do caminho. O `cod.txt` de teste usa `while`, `if/else`, operações aritméticas e `read`/`print`, então cobre as construções principais da linguagem.
