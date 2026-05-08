import java.util.*;

/*
CLASSE ALGORITMOSEMGRAFOS: Implementa os algoritmos vistos na disciplina.

SINTAXE DO JAVA: utilizamos métodos estáticos(utility class), PriorityQueue com comparador personalizado, generics extensivos e os algoritmos
clássicos de grafos.

BOAS PRÁTICAS DE PROGRAMAÇÃO: O utility class, que são métodos estáticos para operações algorítmicas; a separação de concerns, ou seja,
algoritmos separados da lógica de negócio; e a classe interna para encapsular estado do algoritmo (VerticeDistancia).

LÓGICA DOS ALGORITMOS: O Dijkstra é um algoritmo guloso para caminhos mínimos em grafos ponderados, o BFS é a busca em largura para a conectividade e
caminhos não ponderados, e a resconstrução de caminho é um backtracking usando mapa de predecessores.

MOTIVO DE ESCOLHA: o dijkstra é ótimo para grafos com pesos não-negativos (labirinto), o BFS é eficiente para a verificação da conectividade e o
PriotityQueue é uma implementação eficiente da min-heap necessária para o Dijkstra.

COMPLEXIDADE:
     1. Dijkstra: possui complexidade O((n+m)logn) com PriorityQueue binária;
     2. BFS: O(n+m), pois explora todos os vértices e arestas;
     3. Reconstrução de caminho: O(n) no pior caso.
 */

public class AlgoritmosEmGrafos {

    /*
    O algoritmo de dijkstra serve para caminhos mínimos com pesos, sua lógica consiste em um algoritmo guloso que expande progressivamente
    a fronteira de vértices com menor distância conhecida e utiliza o relaxamento de arestas para atualizar as distâncias.
    Sua complexidade é O((n+m)logn), pois realiza n operações de extrair da heap de mínimo + m operações de decrease-key.
     */
        public static Map<Vertice, Integer> dijkstra(Labirinto labirinto, Vertice origem) {
            Map<Vertice, Integer> distancias = new HashMap<>();
            PriorityQueue<VerticeDistancia> fila = new PriorityQueue<>();
            //Inicialização de todas as distâncias como infinito, exceto a origem:
            for (Vertice v : labirinto.getVertices()) {
                distancias.put(v, Integer.MAX_VALUE);
            }
            distancias.put(origem, 0);
            fila.add(new VerticeDistancia(origem, 0));
            //Processamento que sempre irá expandir o vértice com menor distância:
            while (!fila.isEmpty()) {
                VerticeDistancia atual = fila.poll();
                Vertice verticeAtual = atual.vertice;
                // Relaxamento de arestas que vai atualizar as distâncias dos vizinhos.
                for (Aresta aresta : labirinto.getArestasAdjacentes(verticeAtual)) {
                    Vertice vizinho = aresta.getDestino();
                    int novaDistancia = distancias.get(verticeAtual) + aresta.getCustoAresta();
                    // se encontrou um caminho melhor, atualiza e reinsere na fila
                    if (novaDistancia < distancias.get(vizinho)) {
                        distancias.put(vizinho, novaDistancia);
                        fila.add(new VerticeDistancia(vizinho, novaDistancia));
                    }
                }
            }
            return distancias;
        }

        /*
        * BFS (Breadth-First-Search): Encontrar o caminho mínimo sem pesos;
        *
        * LÓGICA: A lógica empregada é a exploração em largura que garante encontrar caminho com menor número de arestas.
        * Desse modo, se torna ideal para grafos não-ponderados, ou com o custo uniforme (mesmo peso para todas as arestas)
        *
        * COMPLEXIDADE: A complexidade da BFS é de O(n + m), já que cada vértice e cada aresta são visitados uma vez*/

        public static List<Vertice> bfsCaminhoMinimo(Labirinto labirinto, Vertice origem, Vertice destino) {
            Map<Vertice, Vertice> predecessores = new HashMap<>();
            Queue<Vertice> fila = new LinkedList<>();
            Set<Vertice> visitados = new HashSet<>();

            fila.add(origem);
            visitados.add(origem);
            predecessores.put(origem, null);

            while (!fila.isEmpty()) {
                Vertice atual = fila.poll();

                if (atual.equals(destino)) break; // Aqui, temos que o destino foi encontrado

                // Nesse laço for, todos vizinhos não visitados serão expandidos
                for (Aresta aresta : labirinto.getArestasAdjacentes(atual)) {
                    Vertice vizinho = aresta.getDestino();
                    if (!visitados.contains(vizinho)) {
                        visitados.add(vizinho);
                        predecessores.put(vizinho, atual); // Nessa linha, haverá o registro do predecessor
                        fila.add(vizinho);
                    }
                }
            }

            return reconstruirCaminho(predecessores, destino);
        }

        /*
        Dijkstra com reconstrução de caminho é uma versão do dijkstra que retorna a sequência de vértices do caminho mínimo e sua complexidade
        é O((n+m)logn), mesma complexidade dijkstra básico.
         */
        public static List<Vertice> dijkstraCaminhoMinimo(Labirinto labirinto, Vertice origem, Vertice destino) {
            Map<Vertice, Integer> distancias = new HashMap<>();
            Map<Vertice, Vertice> predecessores = new HashMap<>();
            PriorityQueue<VerticeDistancia> fila = new PriorityQueue<>();

            for (Vertice v : labirinto.getVertices()) {
                distancias.put(v, Integer.MAX_VALUE);
                predecessores.put(v, null);
            }
            distancias.put(origem, 0);
            fila.add(new VerticeDistancia(origem, 0));

            while (!fila.isEmpty()) {
                VerticeDistancia atual = fila.poll();
                Vertice verticeAtual = atual.vertice;

                if (verticeAtual.equals(destino)) break; // é uma otimização para quando encontra um destino.

                for (Aresta aresta : labirinto.getArestasAdjacentes(verticeAtual)) {
                    Vertice vizinho = aresta.getDestino();
                    int novaDistancia = distancias.get(verticeAtual) + aresta.getCustoAresta();

                    if (novaDistancia < distancias.get(vizinho)) {
                        distancias.put(vizinho, novaDistancia);
                        predecessores.put(vizinho, verticeAtual); // Registra predecessor.
                        fila.add(new VerticeDistancia(vizinho, novaDistancia));
                    }
                }
            }

            return reconstruirCaminho(predecessores, destino);
        }

        /* Metodo reconstruirCaminho();
        * RECONSTRUÇÃO DE CAMINHO: Esse metodo utiliza backtracking a partir do destino
        * LÓGICA: Segue chain de predecessores do destino até a origem, logo depois reverte a lista para obter
        * caminho na ordem correta
        * COMPLEXIDADE: O metodo possui como complexidade O(n), no pior caso o caminho percorre todos os vértices*/

        private static List<Vertice> reconstruirCaminho(Map<Vertice, Vertice> predecessores, Vertice destino) {
            List<Vertice> caminho = new ArrayList<>();
            Vertice atual = destino; // Aqui temos o Backtracking: do destino até a origem (predecessor nulo)

            while (atual != null) {
                caminho.add(0, atual); /* Nessa linha, o metodo insere no inicio para manter a seguinte ordem:
                                                origem->destino */

                atual = predecessores.get(atual);
            }

            return caminho.size() > 1 ? caminho : new ArrayList<>(); // Retorna vazio se não há caminho
        }

        /*
        * CLASSE INTERNA VERTICEDISTANCIA
        * Nessa classe há a tuple personalizada para a PriorityQueue (Fila de Prioridade) do Djikstra, além disso, implementa
        * Comparable para ordenação por distância (Heap de Mínimo)
        * Seu principal objetivo é viabilizar a otimização do algoritmo de Djikstra.
        * */

        private static class VerticeDistancia implements Comparable<VerticeDistancia> {
            Vertice vertice;
            int distancia;

            VerticeDistancia(Vertice vertice, int distancia) {
                this.vertice = vertice;
                this.distancia = distancia;
            }
            @Override
            public int compareTo(VerticeDistancia other) {
                return Integer.compare(this.distancia, other.distancia); // Ordenação Crescente
            }
        }
}

