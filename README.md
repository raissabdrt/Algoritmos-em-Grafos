# Labirinto de Creta — Simulação com Algoritmos em Grafos

Simulação do mito grego do Minotauro implementada em Java, modelando o labirinto como um **grafo ponderado não-direcionado**. O projeto demonstra na prática algoritmos clássicos de grafos como Dijkstra, BFS e Backtracking, aplicados a um cenário de perseguição e fuga.

---

## Sobre o Projeto

Um **Prisioneiro (Detento)** tenta escapar do labirinto usando uma estratégia de exploração com backtracking (o famoso "novelo de lã"). O **Minotauro** patrulha o labirinto aleatoriamente e, ao detectar o prisioneiro dentro de seu alcance de percepção, passa a persegui-lo pelo caminho mínimo calculado via Dijkstra. A simulação termina quando o prisioneiro escapa, é capturado, ou o tempo máximo se esgota.

---

## Estrutura de Classes

```
├── Vertice.java            # Nó do grafo (vértice do labirinto)
├── Aresta.java             # Conexão ponderada entre dois vértices
├── Labirinto.java          # Grafo principal — lista de adjacências
├── Explorador.java         # Classe abstrata base para entidades móveis
├── Detento.java            # Prisioneiro — exploração com backtracking
├── Minotauro.java          # Perseguidor — FSM com patrulha e perseguição
├── AlgoritmosEmGrafos.java # Dijkstra, BFS e reconstrução de caminho
└── Main.java               # Orquestrador — leitura, simulação e relatório
```

### Diagrama de Herança

```
Explorador (abstrata)
├── Detento      → backtracking com Stack
└── Minotauro    → FSM (patrulha / perseguição via Dijkstra)
```

---

## Algoritmos Implementados

### Dijkstra — `AlgoritmosEmGrafos.dijkstra()` e `dijkstraCaminhoMinimo()`
Usado pelo Minotauro para calcular a distância até o prisioneiro e para traçar o caminho mínimo durante a perseguição.

- **Complexidade:** O((V + E) log V) com `PriorityQueue`
- **Estruturas:** `HashMap` para distâncias, `PriorityQueue` (min-heap), `Map` de predecessores

### BFS — `AlgoritmosEmGrafos.bfsCaminhoMinimo()`
Busca em largura para encontrar caminho com menor número de arestas (útil em grafos não ponderados ou com custo uniforme).

- **Complexidade:** O(V + E)
- **Estruturas:** `Queue` (LinkedList), `HashSet` de visitados, `Map` de predecessores

### Backtracking com Stack — `Detento.mover()`
Estratégia de exploração do prisioneiro que simula o "novelo de lã" da mitologia grega. Prioriza vértices não visitados e faz retrocesso quando não há saída.

- **Complexidade:** O(d(v)) por movimento, O(V) de memória no pior caso
- **Estrutura:** `Stack<Vertice>` para o histórico de retrocesso

---

## Lógica da Simulação

A simulação é executada em turnos. A cada turno:

1. O **Detento** se move: escolhe aleatoriamente um vizinho não visitado ou retrocede pelo Stack.
2. O **Minotauro** verifica se o prisioneiro está dentro do seu alcance (via Dijkstra).
   - Se sim: entra em modo de **perseguição** — move-se 2 vértices por turno pelo caminho mínimo.
   - Se não: **patrulha** — move-se aleatoriamente.

### Possíveis desfechos

| Resultado | Condição |
|---|---|
| Prisioneiro escapou | Chega ao vértice de saída dentro do tempo |
| Capturado pelo Minotauro | Minotauro alcança o mesmo vértice (99% de chance de derrota) |
| Prisioneiro vence o Minotauro | 1% de chance ao ser encontrado |
| Morte por tempo esgotado | Tempo máximo de passos atingido |

---

## Formato do Arquivo de Entrada

O labirinto é definido por um arquivo `.txt` com o seguinte formato:

```
<número de vértices>
<número de arestas>
<vértice_u> <vértice_v> <peso>   ← repetir para cada aresta
...
<id do vértice de entrada>
<id do vértice de saída>
<id da posição inicial do Minotauro>
<alcance de percepção do Minotauro>
<tempo máximo de passos>
```

### Exemplo (`labirinto.txt`)

```
5
6
0 1 1
0 2 2
1 2 1
1 3 3
2 4 2
3 4 1
0
4
2
3
20
```

Nesse exemplo: 5 vértices (V0–V4), 6 arestas, entrada em V0, saída em V4, Minotauro começa em V2 com alcance de percepção 3 e tempo limite de 20 passos.

---

## Como Executar

### Pré-requisitos
- Java 11 ou superior

### Compilação

```bash
javac *.java
```

### Execução

```bash
java Main labirinto.txt
```

### Saída esperada

No terminal será exibido um resumo da simulação. Um relatório detalhado é salvo automaticamente em arquivo com nome no formato:

```
relatorio_simulacao_labirinto_<timestamp>.txt
```

---

## Complexidade Geral

| Operação | Complexidade Temporal | Complexidade Espacial |
|---|---|---|
| Leitura do arquivo | O(V + E) | O(V + E) |
| Representação do grafo | — | O(V + E) |
| Movimento do Detento | O(d(v)) por turno | O(V) |
| Detecção pelo Minotauro | O((V+E) log V) | O(V) |
| Perseguição (Dijkstra) | O((V+E) log V) | O(V) |
| Simulação completa | O(T · (V+E) log V) | O(V + E) |
| Geração de relatório | O(V + E) | O(V + E) |

> **T** = número de turnos da simulação

---

## Decisões de Design

**Lista de adjacências** foi escolhida para representar o grafo por ser eficiente para grafos esparsos (labirintos), consumindo O(V + E) de memória e permitindo iteração rápida sobre vizinhos.

**Herança entre `Detento` e `Minotauro`** a partir de `Explorador` elimina duplicação de código e define um contrato claro para entidades móveis.

**`AlgoritmosEmGrafos` como utility class** (métodos estáticos) mantém os algoritmos desacoplados da lógica de negócio, facilitando testes e reuso.

**Imutabilidade** em `Vertice` e `Aresta` (atributos sem setters) previne estados inconsistentes nos algoritmos.

---

## Autores

Projeto desenvolvido para a disciplina de **Algoritmos em Grafos**.
