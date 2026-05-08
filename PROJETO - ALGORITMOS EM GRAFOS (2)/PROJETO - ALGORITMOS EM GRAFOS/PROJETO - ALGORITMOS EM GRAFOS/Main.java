import java.io.*;
import java.nio.file.*;
import java.util.*;

/*
CLASSE MAIN: Essa classe irá orquestrar a Simulação e irá gerar o relatório final da execução.
SINTAXE JAVA: Entrada por linha de comando (args), leitura de arquivos, manipulação de strings, formatação de relatórios,
tratamento de exceções.
BOAS PRÁTICAS UTILIZADAS:
. Separação clara de fases: leitura -> execução -> relatório
. Tratamento robusto de erros com mensagens informativas
. Validação de entrada
. Separação de saída (console e arquivo)
. Métodos curtos e especializados
LÓGICA DA SIMULAÇÃO: A lógica aqui empregada é dividida nos seguintes passos:
1) Leitura: Parse do arquivo de entrada e validação
2) Execução: Loop de turnos com movimentos alternados
3) Relatório: Geração de analise em arquivo separado
MOTIVO DAS ESCOLHAS:
. Arquivo de entrada: flexibilidade e competência especificada nas orientações do projeto
. Validação rigorosa: evitar erros na simulação
. Relatório: análise separada sem poluir o terminal
COMPLEXIDADE: Para calcular a complexidade foram analisadas as seguintes estruturas:
. Leitura: O(n + m), já que processa todos os vértices e arestas
. Simulação: O(T *(n + m)*logn), T turnos * custo dos algoritmos
. Relatórios: O(n + m), percorre estruturas para coleta de dados
 */

public class Main {
    private Labirinto labirinto;
    private Detento detento;
    private Minotauro minotauro;
    private int tempoMaximo;
    private boolean deteccaoOcorrida;
    private int momentoDeteccao;
    private boolean encontroOcorreu;
    private int momentoEncontro;
    private int passosSimulacao;
    private String arquivoEntrada;

    private static final double CHANCE_VITORIA_DETENTO = 0.01;

    /* Metodo Principal - Ponto de Entrada
    * Aqui, os argumentos serão validados e a simulação se iniciará*/

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: java Main <arquivo_labirinto>");
            System.out.println("Exemplo: java Main labirinto.txt");
            System.exit(1);
        }

        Main simulacao = new Main();
        simulacao.executarSimulacaoComArquivo(args[0]);
    }

    public void executarSimulacaoComArquivo(String arquivoEntrada) {
        this.arquivoEntrada = arquivoEntrada;

        if (lerEValidarArquivoEntrada(arquivoEntrada)) {
            executarSimulacaoCompleta();
        } else {
            System.err.println("Falha ao carregar arquivo de entrada.");
            System.exit(1);
        }
    }

    private void executarSimulacaoCompleta() {
        executarSimulacaoPassoAPasso();

        gerarRelatorioCompleto();

        exibirResumoNoConsole();
    }

    /* Leitura e Validação do Arquivo de Entrada
    * Complexidade: O(n + m), processa todas as linhas do arquivo
    * Implementa parser customizado com validações específicas
    * */

    private boolean lerEValidarArquivoEntrada(String arquivoEntrada) {
        try (BufferedReader br = new BufferedReader(new FileReader(arquivoEntrada))) {

            // Leitura sequencial seguindo formato especifico
            int n = Integer.parseInt(br.readLine().trim());
            int m = Integer.parseInt(br.readLine().trim());

            // Validações de integridade
            if (n <= 0 || m < 0) {
                System.err.println("Erro: Número de vértices ou arestas inválido.");
                return false;
            }

            labirinto = new Labirinto();

            // Criação dos vértices
            for (int i = 0; i < n; i++) {
                labirinto.adicionarVertice(new Vertice(i));
            }

            // Processamento das arestas
            for (int i = 0; i < m; i++) {
                String[] linha = br.readLine().trim().split("\\s+");
                if (linha.length != 3) {
                    System.err.println("Erro: Formato de aresta inválido na linha " + (i + 3));
                    return false;
                }

                int u = Integer.parseInt(linha[0]);
                int v = Integer.parseInt(linha[1]);
                int peso = Integer.parseInt(linha[2]);

                // Validações de domínio
                if (u < 0 || u >= n || v < 0 || v >= n) {
                    System.err.println("Erro: Vértice inválido na aresta " + i);
                    return false;
                }
                if (peso <= 0) {
                    System.err.println("Erro: Peso da aresta deve ser positivo");
                    return false;
                }

                Vertice verticeU = encontrarVerticePorId(u);
                Vertice verticeV = encontrarVerticePorId(v);
                labirinto.adicionarAresta(new Aresta(verticeU, verticeV, peso));
            }

            // Leitura dos parâmetros de configuração
            int entradaId = Integer.parseInt(br.readLine().trim());
            int saidaId = Integer.parseInt(br.readLine().trim());
            int minotauroId = Integer.parseInt(br.readLine().trim());
            int alcance = Integer.parseInt(br.readLine().trim());
            tempoMaximo = Integer.parseInt(br.readLine().trim());

            // Validações Semânticas
            if (entradaId == saidaId) {
                System.err.println("Erro: Entrada e saída devem ser diferentes");
                return false;
            }
            if (entradaId == minotauroId || saidaId == minotauroId) {
                System.err.println("Erro: Minotauro não pode estar na entrada ou saída");
                return false;
            }
            if (alcance <= 0 || tempoMaximo <= 0) {
                System.err.println("Erro: Alcance e tempo máximo devem ser positivos");
                return false;
            }

            // Configuração final do labirinto
            Vertice entrada = encontrarVerticePorId(entradaId);
            Vertice saida = encontrarVerticePorId(saidaId);
            Vertice posMinotauro = encontrarVerticePorId(minotauroId);

            if (entrada == null || saida == null || posMinotauro == null) {
                System.err.println("Erro: Vértices de configuração não encontrados");
                return false;
            }

            labirinto.setEntrada(entrada);
            labirinto.setSaida(saida);
            labirinto.setPosicaoMinotauro(posMinotauro);
            labirinto.setAlcance(alcance);
            labirinto.setTempoMaximo(tempoMaximo);

            // Criação dos personagens
            detento = new Detento(labirinto, entrada);
            minotauro = new Minotauro(labirinto, detento);

            System.out.println("Arquivo carregado: " + arquivoEntrada);
            System.out.println("- Vértices: " + n + ", Arestas: " + m);
            System.out.println("- Entrada: " + entrada + ", Saída: " + saida);
            System.out.println("- Minotauro: " + posMinotauro + ", Alcance: " + alcance);
            System.out.println("- Tempo máximo: " + tempoMaximo + "\n");

            return true;

        } catch (IOException e) {
            System.err.println("Erro de E/S: " + e.getMessage());
            return false;
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            return false;
        }
    }

    /* Loop Principal de Simulação
    * Esse loop irá executar turnos até condição de término
    * Complexidade: O(T * (n + m)*logn), em que T é o número de turnos, m é o número de vértices e n é o número de arestas
    * */

    private void executarSimulacaoPassoAPasso() {
        deteccaoOcorrida = false;
        encontroOcorreu = false;
        momentoDeteccao = -1;
        momentoEncontro = -1;
        passosSimulacao = 0;

        System.out.println("=== EXECUÇÃO DA SIMULAÇÃO ===");

        // Loop de turnos: condições de término múltiplas
        while (passosSimulacao < tempoMaximo &&
                detento.estaVivo() &&
                !detento.encontrouSaida() &&
                !encontroOcorreu) {

            passosSimulacao++;

            // Fase de detecção
            if (!deteccaoOcorrida && minotauro.devePerseguir()) {
                deteccaoOcorrida = true;
                momentoDeteccao = passosSimulacao;
                System.out.println("Passo " + passosSimulacao + ": Minotauro detectou o prisioneiro!");
            }

            // Movimento do Prisioneiro
            Vertice posicaoAnteriorDetento = detento.getAtualPosicao();
            detento.mover();
            Vertice novaPosicaoDetento = detento.getAtualPosicao();

            if (!posicaoAnteriorDetento.equals(novaPosicaoDetento)) {
                System.out.println("Passo " + passosSimulacao + ": Prisioneiro " +
                        posicaoAnteriorDetento + " → " + novaPosicaoDetento);
            }

            // Verificação de vitória
            if (detento.encontrouSaida()) {
                System.out.println("Passo " + passosSimulacao + ": Prisioneiro encontrou a saída!");
                break;
            }

            // Movimento do Minotauro
            Vertice posicaoAnteriorMinotauro = minotauro.getAtualPosicao();
            minotauro.mover();
            Vertice novaPosicaoMinotauro = minotauro.getAtualPosicao();

            if (!posicaoAnteriorMinotauro.equals(novaPosicaoMinotauro)) {
                String modo = deteccaoOcorrida ? "perseguição" : "patrulha";
                System.out.println("Passo " + passosSimulacao + ": Minotauro (" + modo + ") " + posicaoAnteriorMinotauro + " → " + novaPosicaoMinotauro);
            }

            // Verificação de encontro e batalha
            if (minotauro.encontrouPrisioneiro()) {
                encontroOcorreu = true;
                momentoEncontro = passosSimulacao;
                System.out.println("Passo " + passosSimulacao + ": ENCONTRO - Minotauro alcançou o prisioneiro!");

                if (detento.batalharComMinotauro()) {
                    System.out.println("Passo " + passosSimulacao + ": PRISIONEIRO VENCEU A BATALHA! (1% de chance)");
                } else {
                    System.out.println("Passo " + passosSimulacao + ": Prisioneiro foi derrotado");
                }
            }

            // Verificar tempo
            if (detento.getTempoRestante() <= 0) {
                System.out.println("Passo " + passosSimulacao + ": Tempo esgotou!");
            }
        }

        System.out.println("=== FIM DA SIMULAÇÃO ===\n");
    }

    /*
    esse é o método para gerar relatório completo e detalhado da simulação.
    Com relação à sintaxe do java, o método faz o uso intensivo de StringBuilder para a eficiência na concatenação e
    formatação do texto com repeat() e quebras de linha, streams para cálculos, e estruturas condicionais para análise nos resultados.
    Referente a boas práticas de programação, usamos o StringBuilder para uma construção eficiente de string grandes, formatação consistente
    e legível do relatório, separação clara de seções com delimitadores visuais, cálculos em tempo real baseados no estado de simulaçãp e análise completa de todos
    os aspectos relevantes.
    Com relação à lógica do algoritmo, ele irá construir um relatório estruturado em 5 seções principais:
    1. Cabeçalho e informações do labirinto;
    2. Resultado final da simulação;
    3. Métricas de tempo e execução;
    4. Análise do caminho percorrido;
    5. Eficiência dos algoritmos utilizados.
    O motivo de nossa escolha para representá-lo assim foi deixar o relatório mais abrangente, atendendo a todos os requisitos do projeto,
    com uma formatação clara que facilita a análise dos resultados e Timestamp no nome do arquivo, que permite múltiplas execuções, bem como uma seção
    de eficiência, que demonstra o conhecimento teórico.
    A complexidade desse algoritmo é O(n+m), pois percorre estruturas para coletar dados, a complexidade de seu espaço é O(n+m), uma vez que o relatório
    cresce com o tamanho do labirinto.
     */

    private void gerarRelatorioCompleto() {
        StringBuilder relatorio = new StringBuilder();
        // esse é o cabeçalho e informações do labirinto, essa seção cria um cabeçalho visualmente destacado para a organização do relatório:
        // SEÇÃO 1:
        relatorio.append("=".repeat(60)).append("\n");
        relatorio.append("RELATÓRIO FINAL DA SIMULAÇÃO - LABIRINTO DE CRETA\n");
        relatorio.append("=".repeat(60)).append("\n\n");
        // São informações fundamentais do labirinto carregado:
        // esse dados irão permitir reproduzir exatamente a simulação executada.
        relatorio.append("INFORMAÇÕES DO LABIRINTO:\n");
        relatorio.append("- Arquivo de entrada: ").append(arquivoEntrada).append("\n");
        relatorio.append("- Vértices: ").append(labirinto.getVertices().size()).append("\n");
        // aqui realizamos um cálculo inteligente do número de arestas somando graus e dividindo por 2(grafo não-direcionado):
        relatorio.append("- Arestas: ").append(labirinto.getVertices().stream().mapToInt(v -> labirinto.getArestasAdjacentes(v).size()).sum() / 2).append("\n");
        relatorio.append("- Entrada: ").append(labirinto.getEntrada()).append("\n");
        relatorio.append("- Saída: ").append(labirinto.getSaida()).append("\n");
        relatorio.append("- Posição inicial do Minotauro: ").append(labirinto.getPosicaoMinotauro()).append("\n");
        relatorio.append("- Alcance de percepção: ").append(labirinto.getAlcance()).append("\n");
        relatorio.append("- Tempo máximo: ").append(tempoMaximo).append("\n\n");

        // SEÇÃO 2: aqui teremos uma análise completa dos possíveis desfechos baseados no estado final, e cada condição irá representar um cenário
        // distinto da mitologia.
        relatorio.append("RESULTADO FINAL:\n");
        if (detento.encontrouSaida()) {
            // este é o cenário ideal, no qual o prisioneiro encontra a saída dentro do tempo.
            relatorio.append("O PRISIONEIRO ESCAPOU DO LABIRINTO!\n");
        } else if (encontroOcorreu && !detento.batalharComMinotauro()) {
            // este é o cenário mais provável, pois o encontro com o minotauro ocasiona a derrota em 99% dos casos.
            relatorio.append("O PRISIONEIRO FOI CAPTURADO E MORREU PELO MINOTAURO!\n");
        } else if (encontroOcorreu && detento.batalharComMinotauro()) {
            // este é um cenário raro, no qual o detento tem uma vitória contra o minotauro, o que só tem 1% de chance de acontecer.
            relatorio.append("O PRISIONEIRO VENCEU O MINOTAURO E ESCAPOU!\n");
        } else if (detento.getTempoRestante() <= 0) {
            // nesse cenário de falha por recursos se trata do tempo esgotado.
            relatorio.append("O PRISIONEIRO MORREU DE FOME!\n");
        } else {
            // esse cenário simula o interrompimento por um motivo que não foi previsto.
            relatorio.append("O PRISIONEIRO NÃO CONSEGUIU ESCAPAR!\n");
        }

        /*
        SEÇÃO 3: aqui mostramos as métricas quantitativas para a análise de desempenho:
         */
        relatorio.append("\nINFORMAÇÕES DE TEMPO:\n");
        relatorio.append("- Tempo máximo: ").append(tempoMaximo).append("\n");
        relatorio.append("- Tempo restante: ").append(detento.getTempoRestante()).append("\n");
        relatorio.append("- Passos executados: ").append(passosSimulacao).append("\n");

        /*
        SEÇÃO 4: reconstituiremos completamente a rota de exploração, o método nos permite analisar a eficácia da estratégia de Backtracking.
         */
        relatorio.append("\nCAMINHO DO PRISIONEIRO:\n");
        List<Vertice> caminhoDetento = detento.getCaminhoPercorrido();
        if (!caminhoDetento.isEmpty()) {
            // essa é a formatação do caminho com quebras a cada 8 vértices para legibilidade:
            for (int i = 0; i < caminhoDetento.size(); i++) {
                relatorio.append(caminhoDetento.get(i).getNome());
                if (i < caminhoDetento.size() - 1) relatorio.append(" → ");
                if ((i + 1) % 8 == 0) relatorio.append("\n");
            }
            relatorio.append("\n- Total de vértices visitados: ").append(caminhoDetento.size()).append("\n");
        }

        /*
        SEÇÃO 5: aqui teremos os detalhes sobre o comportamento do Minotauro durante a simulação:
         */
        relatorio.append("\nINFORMAÇÕES DA PERSEGUIÇÃO:\n");
        if (deteccaoOcorrida) {
            // aqui, houve detecção, então iremos analisar os desdobramentos:
            relatorio.append("- Detecção ocorreu no passo: ").append(momentoDeteccao).append("\n");
            if (encontroOcorreu) {
                // aqui significa que a perseguição do minotauro foi bem sucedida.
                relatorio.append("- Encontro ocorreu no passo: ").append(momentoEncontro).append("\n");
                relatorio.append("- Duração da perseguição: ").append(momentoEncontro - momentoDeteccao).append(" passos\n");
            } else {
                // aqui significa que a perseguição ocorreu, porém não resultou em um encontro do detento com o minotauro.
                relatorio.append("- Perseguição em andamento (não houve encontro)\n");
            }
            // aqui teremos a reconstituição do caminho do minotauro durante a perseguição:
            List<Vertice> trilhaPerseguicao = minotauro.getTrilhaDaPerseguicao();
            if (!trilhaPerseguicao.isEmpty()) {
                relatorio.append("- Caminho do Minotauro durante perseguição: ");
                for (int i = 0; i < trilhaPerseguicao.size(); i++) {
                    relatorio.append(trilhaPerseguicao.get(i).getNome());
                    if (i < trilhaPerseguicao.size() - 1) relatorio.append(" → ");
                }
                relatorio.append("\n");
            }
        } else {
            // aqui significa que o minotauro nunca chegou a detectar o prisioneiro:
            relatorio.append("- Minotauro não detectou o prisioneiro\n");
        }

        /*
        SEÇÃO 6: esse método se trata da demonstração do conhecimento teórico sobre complexidade algorítmica, essencial para atender aos critérios
        deste projeto.
         */
        relatorio.append("\n").append("=".repeat(60)).append("\n");
        relatorio.append("ANÁLISE DE EFICIÊNCIA DOS ALGORITMOS\n");
        relatorio.append("=".repeat(60)).append("\n\n");

        relatorio.append("ALGORITMOS IMPLEMENTADOS:\n");
        relatorio.append("1. DIJKSTRA - O((V+E)logV) - Perseguição inteligente do Minotauro\n");
        relatorio.append("2. Estratégia de Backtracking - Exploração com novelo de lã do Prisioneiro\n");
        relatorio.append("3. BFS/DFS - Verificação de conectividade e caminhos\n\n");

        relatorio.append("EFICIÊNCIA DA SOLUÇÃO:\n");
        relatorio.append("- Complexidade temporal: O((V+E)logV) para perseguição\n");
        relatorio.append("- Complexidade espacial: O(V+E) para representação do grafo\n");
        relatorio.append("- Escalabilidade: Adequada para labirintos de tamanho considerável\n");
        relatorio.append("- Uso ótimo de estruturas: PriorityQueue, HashMap, Stack\n");
        // ele finaliza gerando o arquivo de relatório:
        salvarRelatorioEmArquivo(relatorio.toString());
    }

    /*
    Esse método irá tratar da persistência do relatório em arquivo.
    Ele irá gerar um nome único baseado no arquivo de entrada e timestamp atual, depois salva o conteúdo em arquivo texto com modificações padrão.
    A sua complexidade é O(1), para operações de entrada e saída que são geralmente constantes e a complexidade de seu espaço é O(n), pois n será
    o tamanho do relatório.
     */

    private void salvarRelatorioEmArquivo(String conteudoRelatorio) {
        try {
            // aqui ele vai gerar um nome único para o arquivo: relatorio_simulacao_<arquivo>_<timestamp>.txt e remove a extensão .txt do arquivo
            // original para evitar duplicações:
            String nomeArquivo = "relatorio_simulacao_" +
                    arquivoEntrada.replace(".txt", "") + "_" +
                    System.currentTimeMillis() + ".txt";

            Files.writeString(Path.of(nomeArquivo), conteudoRelatorio);
            System.out.println("Relatório detalhado salvo em: " + nomeArquivo);
        } catch (IOException e) {
            System.err.println("Erro ao salvar relatório: " + e.getMessage());
        }
    }

    /*
    Esse método se trata da apresentação de um resumo executivo, ou seja, um resumo da execução.
    Ele irá apresentar uma versão resumida dos resultados mais importantes e focar na clareza e no impacto visual para o usuário.
    Sua complexidade de tempo é O(1), pois ele realiza operações constantes de formatação e O(1) para complexidade de espaço, pois a saída
    possui um tamanho fixo.
     */
    private void exibirResumoNoConsole() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("RESUMO DA SIMULAÇÃO");
        System.out.println("=".repeat(50));
        // aqui é a análise do resultado final, gerando uma versão resumida do relatório completo:
        if (detento.encontrouSaida()) {
            System.out.println("PRISIONEIRO ESCAPOU COM SUCESSO!");
        } else if (encontroOcorreu && detento.batalharComMinotauro()) {
            System.out.println("PRISIONEIRO VENCEU O MINOTAURO E ESCAPOU!");
        } else if (encontroOcorreu) {
            System.out.println("PRISIONEIRO CAPTURADO PELO MINOTAURO!");
        } else if (detento.getTempoRestante() <= 0) {
            System.out.println("TEMPO ESGOTADO - PRISIONEIRO MORREU DE FOME!");
        } else {
            System.out.println("SITUAÇÃO INDETERMINADA");
        }
        // aqui apresentamos as estatísticas para uma análise rápida:
        System.out.println("\nESTATÍSTICAS:");
        System.out.println("Passos executados: " + passosSimulacao + "/" + tempoMaximo);
        System.out.println("Tempo restante: " + detento.getTempoRestante());
        System.out.println("Detecção: " + (deteccaoOcorrida ? "Sim (passo " + momentoDeteccao + ")" : "Não"));
        if (encontroOcorreu) {
            System.out.println("Encontro: Sim (passo " + momentoEncontro + ")");
        }
        System.out.println("Vértices visitados: " + detento.getCaminhoPercorrido().size());
        // aqui temos o direcionamento para uma análise detalhada:
        System.out.println("\nRelatório completo salvo em arquivo separado!");
    }

    /*
    Esse método irá realizar uma busca linear por ID em lista de vértices e retornar a primeira ocorrência encontrada.
    Sua complexidade de tempo é de O(n), pois se trata de uma busca linear no pior caso e sua complexidade de espaço é
    O(1), uma vez que não usa estruturas auxiliares.
     */
    private Vertice encontrarVerticePorId(int id) {
        for (Vertice v : labirinto.getVertices()) {
            if (v.getId() == id) return v;
        }
        return null;
    }
}