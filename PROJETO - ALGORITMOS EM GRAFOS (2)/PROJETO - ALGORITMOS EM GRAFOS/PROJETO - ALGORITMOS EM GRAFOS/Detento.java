import java.util.List;
import java.util.Random;
import java.util.Stack;

/*
* CLASSE DETENTO: O objetivo dessa classe é simular o prisioneiro explorando o labirinto em busca de saída.
* Ela foi criada com base na Inteligência de Exploração com Backtracking;
*
* SINTAXE: Sua sintaxe faz uso da Herança de Explorador, e uso de Stack para o backtracking. Além disso, utiliza o Random para
* decisões aleatórias, com o controle de estado com flags.
*
* BOAS PRÁTICAS: Aqui, as boas práticas empregadas são:
* . Herança para compartilhar/herdar o comportamento de explorador
* . Stack para LIFO do backtracking
* . Controle explicito de estado (como: saidaEncontrada, tempoRestante)
* . Separação de algoritmo de movimento e regras de negócio;
*
* LÓGICA: A lógica desenvolvida para essa classe foi Implementar a exploração com o backtracking ("novelo de lã"), Priorizar
* os vértices não visitados, realizar o Backtrack quando não houverem novas opções de vértices. e seguir a Aleatoriedade na escolha
* dos vértices, com o propósito de evitar loops.
*
* ESCOLHAS NA IMPLEMENTAÇÃO: Nessa classe, o Stack foi escolhido como estrutura natural para o backtracking, além de manter a aleatoriedade
* para garantir a imprevisibilidade. Por fim, foi visado também o balance entre exploração e retorno seguro do detento.
*
* COMPLEXIDADE: A complexidade foi analisada seguindo as diferentes estruturas presentes:
* . Movimento: O(d(v)), tempo baseado no grau do vértice v
* . Backtracking: O(1), fica em tempo constante se considerar o uso do Stack
* . Memória: O(n), no pior caso (em que é necessário visitar todos os vértices)
* */

class Detento extends Explorador {
    private Random dadosAleatorios;
    private int tempoRestante;
    private Stack<Vertice> pilha;
    private boolean saidaEncontrada;
    public Detento(Labirinto labirinto, Vertice posicaoInicial) {
        super(labirinto, posicaoInicial);
        this.dadosAleatorios = new Random();
        this.tempoRestante = labirinto.getTempoMaximo(); // ou outro valor
        this.pilha = new Stack<>();
        this.pilha.push(posicaoInicial);
        this.saidaEncontrada = false;
    }

    /*
    * ALGORITMO DE MOVIMENTAÇÃO DO PRISIONEIRO
    * Nesse algoritmo, foi implementada a estratégia de exploração fazendo uso do backtracking
    * A sua complexidade é de O(d(v)), em que é considerado o grau do vértice v, ou seja, sua lista de vizinhos*/

    @Override
    public void mover() {
        if (tempoRestante <= 0 || saidaEncontrada) return;

        if (atualPosicao.equals(labirinto.getSaida())) {
            saidaEncontrada = true;
            return;
        }
        List<Vertice> vizinhosNaoVisitados = getVizinhosNaoVisitados();
        Vertice proximoVertice;
        if (!vizinhosNaoVisitados.isEmpty()) {
            proximoVertice =
                    vizinhosNaoVisitados.get(dadosAleatorios.nextInt(vizinhosNaoVisitados.size()));
            pilha.push(atualPosicao);
        } else if (!pilha.isEmpty()) {
            proximoVertice = pilha.pop();
        } else {
            return; // Não há para onde mover
        }
        atualPosicao = proximoVertice;
        caminhoPercorrido.add(proximoVertice);
        tempoRestante--;

        if (atualPosicao.equals(labirinto.getSaida())) {
            saidaEncontrada = true;
        }
    }

    /* Aqui, temos métodos de estado - getter's simples para o controle de estado */

    public boolean encontrouSaida() { return saidaEncontrada; }
    public boolean estaVivo() { return tempoRestante > 0 && !saidaEncontrada; }
    public int getTempoRestante() { return tempoRestante; }

    /* Nesse metodo batalharComMinotauro() temos a batalha com o Minotauro - em que foi pensando para ter apenas 1% de chance
    *  de vitória, nele temos a complexidade O(1), uma vez que é uma operação aleatória simples */

    public boolean batalharComMinotauro() {
        return dadosAleatorios.nextDouble() < 0.01; // 1% de chance de vitória
    }
}