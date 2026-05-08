import java.util.*;
/*
CLASSE MINOTAURO: é uma inteligência de perseguição.

SINTAXE JAVA: utiliza Herança (extends Explorador), sobrescrita de métodos (@Override) e o uso de generics collections e do algoritmo Dijkistra.

BOAS PRÁTICAS DE PROGRAMAÇÃO: Herança para compartilhar o comportamento comum de Detento, delegação de algoritmos complexos para classe especializada(AlgoritmosEmGrafos), e
a separação de responsabilidades: movimento x detecção x perseguição.

LÓGICA DO ALGORITMO: Implementa FSM(Finite State Machine) com dois estados - 1. Patrulha: movimento aleatório pelos vértices; 2. Perseguição: movimento inteligente
usando caminho mínimo (Dijkistra).

MOTIVO DA ESCOLHA: o Dijkistra para perseguição garante um caminho ótimo considerando os pesos das arestas. A máquina de estados modela um comportamento
realista (detecta -> persegue). A velocidade dupla na perseguição mantém o projeto fiel às especificações.

COMPLEXIDADE: A detecção ocorre em O((n+m)logn), que é o custo do Dijkistra; a perseguição ocorre também em O((n+m)logn) por movimento (2 vértices),
e a patrulha ocorre em O(1), pois é um movimento aleatório.
 */
public class Minotauro extends Explorador {
    private int alcance;
    private boolean emPerseguicao;
    private Detento detento;
    private List<Vertice> trilhaDaPerseguicao;

    public Minotauro(Labirinto labirinto, Detento detento) {
        super(labirinto, labirinto.getPosicaoMinotauro());
        this.alcance = labirinto.getAlcance();
        this.detento = detento;
        this.emPerseguicao = false;
        this.trilhaDaPerseguicao = new ArrayList<>();
    }
/*
Máquina de estados do minotauro: Decide entre o modo patrulha ou perseguição, baseado na detecção. A complexidade desse método é de O((n+m)logn) no pior caso,
ou seja, quando persegue.
 */
    @Override
    public void mover() {
        if (devePerseguir()) {
            if (!emPerseguicao) {
                emPerseguicao = true;
                trilhaDaPerseguicao.clear();
            }
            movimentoDaPerseguicao();
        } else {
            if (emPerseguicao) {
                emPerseguicao = false; // aqui é a transição de estado, de perseguição para patrulha.
            }
            movimentosAleatorios();
        }
    }

    /*
    O algoritmo de detecção usa o dijkistra para calcular a distância mínima até o prisioneiro, compara com o alcance de percepção do Minotauro e
    possui complexidade O((n+m)logn), que é o custo do algoritmo de Dijkstra.
     */
    boolean devePerseguir() {
        Map<Vertice, Integer> distancias = AlgoritmosEmGrafos.dijkstra(labirinto, atualPosicao);
        int distancia = distancias.getOrDefault(detento.getAtualPosicao(), Integer.MAX_VALUE);
        return distancia <= alcance;
    }

    /*
    O movimento de perseguição inteligente move-se 2 vértices por rodada seguindo caminho mínimo (Dijkstra) e sua complexidade é O((n+m)logn), que é
    o custo do algoritmo de Dijkstra para caminho mínimo.
     */

    private void movimentoDaPerseguicao() {
        for (int i = 0; i < 2; i++) {
            List<Vertice> caminho = AlgoritmosEmGrafos.dijkstraCaminhoMinimo(labirinto, atualPosicao, detento.getAtualPosicao());
            if (caminho.size() > 1) {
                atualPosicao = caminho.get(1); // Próximo vértice no caminho mínimo
                caminhoPercorrido.add(atualPosicao);
                trilhaDaPerseguicao.add(atualPosicao);
            }
            if (atualPosicao.equals(detento.getAtualPosicao())) break;
        }
    }

    /*
    O movimento aleatório vai escolher aleatoriamente entre vértices adjacentes. Este é o algoritmo de patrulha. Sua complexidade é O(1) pois
    vai acontecer uma seleção aleatória em uma lista pré-computada.
     */
    private void movimentosAleatorios() {
        List<Aresta> arestasAdjacentes = labirinto.getArestasAdjacentes(atualPosicao);
        if (!arestasAdjacentes.isEmpty()) {
            Aresta arestaEscolhida = arestasAdjacentes.get(new Random().nextInt(arestasAdjacentes.size()));
            atualPosicao = arestaEscolhida.getDestino();
            caminhoPercorrido.add(atualPosicao);
        }
    }

    public boolean encontrouPrisioneiro() {
        return atualPosicao.equals(detento.getAtualPosicao());
    }

    public List<Vertice> getTrilhaDaPerseguicao() {
        return new ArrayList<>(trilhaDaPerseguicao);
    }

    public boolean estaEmPerseguicao() {
        return emPerseguicao;
    }
}