import java.util.ArrayList;
import java.util.List;
/*
 A Classe Explorador é a classe base abstrata pra as entidades móveis, ou seja, aquele que vai explorar o labirinto.

 SINTAXE JAVA: utilizamos classe abstrata, métodos abstratos, protected para herança, e composição com Labirinto para acesso ao ambiente.

 BOAS PRÁTICAS DE PROGRAMAÇÃO: uma classe abstrata para definir interface comum, Template Method Pattern, que é o método concreto getVizinhosNaoVisitados(),
 protected para acesso controlado nas subclasses, e composição com Labirinto para acesso ao ambiente.

 LÓGICA: define contrato comum para todas as entidade móveeis no labirinto, mantém estados de posição e histórico de movimento, fornece um algoritmo concredto para
 a obtenção de vizinhos não visitados.

 MOTIVO DE ESCOLHA: evita duplicação de código entre Detento e Minotauro, define uma interface clara para entidades móveis, permite extensibilidade para
 novos tipos de exploradores, separa comportamento comum (histórico) de específico (movimento).

 COMPLEXIDADE: getVisinhosNaoVisitados possui complexidade O(d(v)), ou seja, O(grau(v)), pois verifica todos os vizinhos. Já os outros métodos
 possuem complexidade O(1), uma vez que possuimos acesso direto a todos os atributos.
 */
abstract class Explorador {
    protected Vertice atualPosicao;                     // é o estado atual (protected para subclasses)
    protected Labirinto labirinto;                      // é o ambiente (composição)
    protected List<Vertice> caminhoPercorrido;          // hitórico de movimento

    // método construtor da classe:

    public Explorador(Labirinto labirinto, Vertice posicaoInicio) {
        this.labirinto = labirinto;
        this.atualPosicao = posicaoInicio;
        this.caminhoPercorrido = new ArrayList<>();
        caminhoPercorrido.add(posicaoInicio); // Inicia histórico com posição inicial
    }

    /*
    Esse é o método abstrato - template method pattern. Cada subclasse irá implementar suas própria lógica de movimento.

     */
    public abstract void mover();
    public Vertice getAtualPosicao() { return atualPosicao; }
    public List<Vertice> getCaminhoPercorrido() { return caminhoPercorrido; }
    /*
    Esse é o algoritmo para obter vizinhos não visitados. Sua complexidade é O(grau(v)) ou O(d(v)), pois irá percorrer todos os vizinhos.
    Esse algoritmo utiliza contains() que é O(n), mas n é pequeno (histórico local)
     */
    protected List<Vertice> getVizinhosNaoVisitados() {
        List<Vertice> vizinhos = new ArrayList<>();
        for (Aresta aresta : labirinto.getArestasAdjacentes(atualPosicao)) {
            Vertice vizinho = aresta.getDestino();
            if (!caminhoPercorrido.contains(vizinho)) {
                vizinhos.add(vizinho);
            }
        }
        return vizinhos;
    }
}
