/**
 * CLASSE ARESTA: é a representação da conexão entre os vértices.
 * Com relação à sintaxe do java, utilizamos uma classe simples com construtores getters e setters, e o toString personalizado para uma representação
 * mais legível.
 *
 * Com relação às boas práticas de programação, utilizamos uma classe imutável com atributos final, sem os setters
 * Além disso, usamos os getters para ter um acesso controlado e um toString descritivo
 *
 * Com relação à lógica utilizada para a construção, usamos para representar a conexão direcionada entre dois vértices com custo. Ou seja, na prática
 * é usado para um grafo não-direcionado(adicionando arestas bidimencionais).
 *
 * O motivo de nossa escolha foi porque representa uma modelagem direta do conceito de aresta ponderada, além de sua imutabilidade que evita erros
 * inconsistentes, e a simplicidade e clareza.
 *
 * A complexidade do algoritmo dessa classe, para todas as operações é O(1), pois se trata de acesso a atributos.
 */

public class Aresta {
    private  Vertice fonte;         // é o vétice de origem (final para imutabilidade)
    private Vertice destino;        // é o vértice de destino (final para imutabilidade)
    private int custoAresta;        // é o peso da aresta (final para imutabilidade)

    // método construtor:
    public Aresta(Vertice origem, Vertice destino, int peso) {
        this.fonte = origem;
        this.destino = destino;
        this.custoAresta = peso;
    }

    // aqui são os getters (ou seja, apenas leitura, imutabilidade)

    public Vertice getFonte() {
        return fonte;
    }

    public Vertice getDestino() {
        return destino;
    }

    public int getCustoAresta() {
        return custoAresta;
    }

    // Aqui temos a representação legível das arestas, no seguinte formato: V0 - V1 (5), para debugging e relatórios:

    @Override
    public String toString() {
        return fonte.getNome() + " - " + destino.getNome() + " ("
                + custoAresta + ")";
    }
}
