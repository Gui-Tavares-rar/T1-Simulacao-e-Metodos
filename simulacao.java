import java.util.*;

public class simulacao {
    // Configurações Globais da Simulação
    static double tempoGlobal = 0;
    static int aleatoriosUsados = 0;
    static final int LIMITE_ALEATORIOS = 100000; // Critério de parada do professor
    static RandomGenerator randomGen;

    // Estruturas de Dados para a Rede
    static Map<String, Queue> queues = new HashMap<>();
    static PriorityQueue<Event> scheduler = new PriorityQueue<>();
    static List<Transition> network = new ArrayList<>();

    public static void main(String[] args) {
        // 1. Configuração do Cenário (Baseado no diagrama T1)
        setupScenario();

        // 2. Inicialização: O primeiro cliente chega no tempo 2.0
        tempoGlobal = 0;
        scheduler.add(new Event(2.0, "EXTERNAL_ARRIVAL", null, "Q1"));

        // 3. Loop Principal da Simulação
        while (aleatoriosUsados < LIMITE_ALEATORIOS && !scheduler.isEmpty()) {
            Event currentEvent = scheduler.poll();
            
            // Atualiza o tempo acumulado nos estados das filas antes de avançar o tempo
            updateStateTimes(currentEvent.time - tempoGlobal);
            tempoGlobal = currentEvent.time;

            processEvent(currentEvent);
        }

        // 4. Exibição do Relatório Final (Conforme Captura de tela de 2026-05-04 17-41-20.png)
        printReport();
    }

    static void setupScenario() {
        // Semente para garantir repetibilidade nos testes
        randomGen = new RandomGenerator(12345L); 

        // Definição das Filas (Nome, Servidores, Capacidade, ChegadaMin, ChegadaMax, ServicoMin, ServicoMax)
        queues.put("Q1", new Queue("Q1", 1, Integer.MAX_VALUE, 2.0, 4.0, 1.0, 2.0));
        queues.put("Q2", new Queue("Q2", 2, 5, null, null, 4.0, 6.0));
        queues.put("Q3", new Queue("Q3", 2, 10, null, null, 5.0, 15.0));

        // Rede de Transições e Probabilidades (Baseado na imagem do diagrama)
        network.add(new Transition("Q1", "Q2", 0.8));
        network.add(new Transition("Q1", "Q3", 0.2));
        network.add(new Transition("Q2", "Q1", 0.3));
        network.add(new Transition("Q2", "Q3", 0.5));
        network.add(new Transition("Q2", "OUT", 0.2));
        network.add(new Transition("Q3", "Q2", 0.7));
        network.add(new Transition("Q3", "OUT", 0.3));
    }

    static void processEvent(Event e) {
        if (aleatoriosUsados >= LIMITE_ALEATORIOS) return;

        // Gerenciamento de Chegadas (Externas ou Transições Internas)
        if (e.type.endsWith("ARRIVAL")) {
            Queue q = queues.get(e.target);
            
            // Se for uma chegada EXTERNA, agenda a próxima chegada externa no sistema
            if (e.type.equals("EXTERNAL_ARRIVAL") && q.minArrival != null && aleatoriosUsados < LIMITE_ALEATORIOS) {
                double nextArrival = tempoGlobal + uniform(q.minArrival, q.maxArrival);
                scheduler.add(new Event(nextArrival, "EXTERNAL_ARRIVAL", null, q.name));
            }

            // Tenta entrar na fila
            if (q.population < q.capacity) {
                q.population++;
                // Se houver servidor livre, agenda o início do serviço (saída)
                if (q.population <= q.servers) {
                    scheduleDeparture(q);
                }
            } else {
                q.losses++; // Cliente perdido se a fila estiver cheia
            }
        } 
        // Gerenciamento de Saídas (Fim de Serviço)
        else if (e.type.equals("DEPARTURE")) {
            Queue qSource = queues.get(e.source);
            qSource.population--;
            
            // Se ainda houver pessoas esperando, o servidor atende o próximo
            if (qSource.population >= qSource.servers) {
                scheduleDeparture(qSource);
            }

            // Define o destino do cliente que acabou de sair
            if (aleatoriosUsados < LIMITE_ALEATORIOS) {
                String destination = selectDestination(qSource.name);
                if (!destination.equals("OUT")) {
                    // Transição imediata para outra fila (Chegada Interna)
                    scheduler.add(new Event(tempoGlobal, "TRANSITION_ARRIVAL", null, destination));
                }
            }
        }
    }

    static void scheduleDeparture(Queue q) {
        if (aleatoriosUsados < LIMITE_ALEATORIOS) {
            double serviceTime = tempoGlobal + uniform(q.minService, q.maxService);
            scheduler.add(new Event(serviceTime, "DEPARTURE", q.name, null));
        }
    }

    static double uniform(double min, double max) {
        aleatoriosUsados++;
        return min + (max - min) * randomGen.next();
    }

    static String selectDestination(String source) {
        double r = randomGen.next();
        aleatoriosUsados++;
        double cumulative = 0;
        for (Transition t : network) {
            if (t.source.equals(source)) {
                cumulative += t.probability;
                if (r <= cumulative) return t.target;
            }
        }
        return "OUT";
    }

    static void updateStateTimes(double duration) {
        for (Queue q : queues.values()) {
            q.stateTimes.put(q.population, q.stateTimes.getOrDefault(q.population, 0.0) + duration);
        }
    }

    static void printReport() {
        System.out.println("=== RELATÓRIO FINAL DA SIMULAÇÃO (COMPLETO) ===");
        System.out.println("Tempo Global Final: " + String.format("%.4f", tempoGlobal));
        System.out.println("Números Aleatórios Consumidos: " + aleatoriosUsados);
        System.out.println("------------------------------------------------");

        List<String> sortedQueues = new ArrayList<>(queues.keySet());
        Collections.sort(sortedQueues);

        for (String key : sortedQueues) {
            Queue q = queues.get(key);
            System.out.println("Fila: " + q.name + " (G/G/" + q.servers + "/" + (q.capacity > 1000 ? "inf" : q.capacity) + ")");
            System.out.println("Perda de clientes: " + q.losses);
            System.out.println("Distribuição de Probabilidades:");
            
            List<Integer> states = new ArrayList<>(q.stateTimes.keySet());
            Collections.sort(states);
            for (int state : states) {
                double tempoNoEstado = q.stateTimes.get(state);
                double prob = (tempoNoEstado / tempoGlobal) * 100;
                // Exibe probabilidade e Tempo Acumulado conforme pedido pelo professor
                System.out.printf("  Estado %d: %10.4f%% | Tempo Acumulado: %12.4f\n", state, prob, tempoNoEstado);
            }
            System.out.println();
        }
    }

    // --- Classes Auxiliares ---

    static class Queue {
        String name;
        int servers, capacity, population = 0, losses = 0;
        Double minArrival, maxArrival, minService, maxService;
        Map<Integer, Double> stateTimes = new HashMap<>();

        Queue(String n, int s, int c, Double minA, Double maxA, double minS, double maxS) {
            this.name = n; this.servers = s; this.capacity = c; 
            this.minArrival = minA; this.maxArrival = maxA; 
            this.minService = minS; this.maxService = maxS;
        }
    }

    static class Event implements Comparable<Event> {
        double time; 
        String type, source, target;

        Event(double time, String type, String source, String target) {
            this.time = time;
            this.type = type;
            this.source = source;
            this.target = target;
        }

        @Override public int compareTo(Event o) { return Double.compare(this.time, o.time); }
    }

    static class Transition {
        String source, target; double probability;
        Transition(String s, String t, double p) { this.source = s; this.target = t; this.probability = p; }
    }

    static class RandomGenerator {
        private long seed;
        RandomGenerator(long seed) { this.seed = seed; }
        double next() {
            // Gerador Linear Congruencial (LCG) simples para consistência
            seed = (seed * 1103515245L + 12345L) & 0x7fffffffL;
            return (double) seed / 0x80000000L;
        }
    }
}