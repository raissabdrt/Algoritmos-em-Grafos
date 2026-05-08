import java.util.Objects;

/*
* CLASSE VÉRTICE - Representação de Nó do grafo
*
* SINTAXE JAVA: Override de equals(), hashCode() e toString() seguindo contrato Java para correta funcionalidade
* em collections.
*
* BOAS PRÁTICAS UTILIZADAS:
* . Override consistente de equals() e hashCode() para uso em HashMap / HashSet
* . toString() informativo para debugging e relatórios
* . Imutabiliddae de facto: atributos final após construção
*
* LÓGICA:
* . Representa posição única no labirinto. Identidade não definida pelo ID.
* . Nome formatado para melhor legibilidade em saídas
*
* MOTIVO DA ESCOLHA:
* . Classe simples e focada em representar o vértice no labirinto
* . hashCode() / equals() corretos para o funcionamento dos algoritmos
*
* COMPLEXIDADE:
* . Todas as operações são feitas em tempo constante, O(1), uma vez que temos acesso direto aos atributos.
* */

public class Vertice {
    private int id;      // Identificador único para definir a identidade do vértice
    private String nome; // Nome para exibição ao usuário

    public Vertice(int id) {
        this.id = id;
        this.nome = "V" + id; // Formatação de nomeclatura
    }

    /* GETTER'S - encapsulamento sem setter (visando garantir a imutabilidade) */

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    /*
    * EQUALS E HASHCODE
    * Aqui, eles são fundamentais para o funcionamento correto em HashMap e HashSet
    * Além disso, verifica se dois vértices são iguais se possuem o mesmo id */

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Vertice vertice = (Vertice) o;
        return id == vertice.id && Objects.equals(nome, vertice.nome);
    }

    /* HASHCODE CONSISTENTE COM EQUALS
    * Ele é necessário para a distribuição consistente em HashMap
    * Também faz uso de Objects.hash() para garantir uma implementação mais robusta*/

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    /* TOSTRING INFORMATIVO
    *Tem como objetivo retornar o nome formatado para debugging e relatórios
     */

    @Override
    public String toString() {
        return nome;
    }
}
