import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
* CLASSE LABIRINTO: Essa classe representa a estrutura do labirinto em si, sendo responsável por armazenar
* todos os vértices e arestas e gerenciar as operações de conectividade entre eles usando a etsrutura de lista de adjacências,
* que é eficiente para grafos esparsos. Além disso, ela serve como "mapa" para os elementos Detento e Minotauro.
*
* SINTAXE UTILIZADA: Escolhemos a generics (<Vertice, List<Aresta>>) para type safety e collections do Java (que são o ArrayList
* e o HashMap), com o objetivo de assegurar a eficiência no acesso e manipulação de dados.
*
* BOAS PRÁTICAS: As práticas envolvidas são:
* . Encapsulamento: todos os atributos são privados, sendo acessados e modificados por getter's e setter's
* . Composição sobre Herança: O labirinto é composto por vértices e arestas, e faz uso dessas classes (Vertice e Aresta)
* . Responsabilidade: a classe foi desenvolvida apenas para representar o grafo, garantindo maior foco em suas funcionalidades
*
* LÓGICA: A lógica empregada nessa classe foi de representar o labirinto como um grafo não-direcionado ponderado usando a lista
* de adjacências. Assim, cada vértice mantém uma lista de arestas incidentes, permitindo a navegação rápida. Além disso, as arestas
* são adicionadas considerando as duas direções (bidirecionalmente), uma vez que estamos trabalhando com um grafo não-direcionado.
*
* MOTIVO DAS ESCOLHAS: Optamos por lista de adjacências por ter em mente um grafo esparso, pois:
* . Economiza memória se comparado com uma matriz
* . Iteração eficiente sobre os vizinhos, já que usa O(d(v)) para acessá-los
* . Facilidade na implementação
*
* COMPLEXIDADE: A complexidade foi calculada considerando as diferentes estruturas e métodos implementados, que são eles:
* . Espaço: O(n + m), em que n é o número de vértices e m é o número de arestas
* . Adicionar vértice: O(1), operação feita em tempo constante
* . Adicionar aresta: O(1), operação também feita em tempo constante
* . Obter vizinhos: O(1), considerando o uso do HashMap
* */

class Labirinto {
    private List<Vertice> vertices;
    private List<Aresta> arestas;
    private Map<Vertice,List<Aresta>> listaDeAdjacencias;
    private Vertice entrada;
    private Vertice saida;
    private Vertice posicaoDoMinotauro;
    private int alcance;
    private int tempoMaximo;

    public Labirinto() {
        vertices = new ArrayList<>();
        arestas = new ArrayList<>();
        listaDeAdjacencias = new HashMap<>();
    }

    /*
    * Esse metodo adicionarVertice() adiciona um vértice ao grafo, inicializando sua lista de adjacências
    * Sua complexidade é de O(1), pois temos uma operação constante com uso de HashMap
    * */

    public void adicionarVertice(Vertice V) {
        vertices.add(V);
        listaDeAdjacencias.put(V, new ArrayList<>());
    }

    /*
    * O metodo adicionarAresta() adiciona a aresta bidirecionalmente, ou seja, para cada aresta A->B, também será
    * adicionada uma aresta B->A com o mesmo custo;
    * A complexidade desse metodo é de O(1), já que faz acesso direto com HashMap e adiciona no ArrayList
     */

    public void adicionarAresta(Aresta A) {
        arestas.add(A);
        listaDeAdjacencias.get(A.getFonte()).add(A);
        listaDeAdjacencias.get(A.getDestino()).add(new Aresta(A.getDestino(), A.getFonte(), A.getCustoAresta()));
    }

    /*
    * Esse metodo List<Aresta> getArestasAdjacentes retorna a lista de arestas adjacentes a um vértice
    * Ele faz uso do getOrDefault para evitar NullPOinterException (Tratamento de Erros e Exceções)
    * Assim, sua compelxidade é de O(1), já que realiza acesso direto ao HashMap
    * */

    public List<Aresta> getArestasAdjacentes(Vertice V) {
        return listaDeAdjacencias.getOrDefault(V, new ArrayList<>());
    }


    public List<Vertice> getVertices() {
        return vertices;
    }

    public Vertice getEntrada() {
        return entrada;
    }

    public Vertice getSaida() {
        return saida;
    }

    public Vertice getPosicaoMinotauro() {
        return posicaoDoMinotauro;
    }

    public int getAlcance() {
        return alcance;
    }

    public int getTempoMaximo() {
        return tempoMaximo;
    }

    public void setEntrada(Vertice entrada) {
        this.entrada = entrada;
    }

    public void setSaida(Vertice saida) {
        this.saida = saida;
    }

    public void setPosicaoMinotauro(Vertice posicaoMinotauro) {
        this.posicaoDoMinotauro = posicaoMinotauro;
    }

    public void setAlcance(int alcance) {
        this.alcance = alcance;
    }

    public void setTempoMaximo(int tempoMaximo) {
        this.tempoMaximo = tempoMaximo;
    }

}

