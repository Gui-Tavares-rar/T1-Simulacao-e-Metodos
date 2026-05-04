# T1 - Simulador de Rede de Filas
Este projeto consiste em um simulador de eventos discretos (DES) para redes de filas genéricas, desenvolvido para a disciplina de Simulação e Métodos Analíticos. O sistema é capaz de modelar topologias complexas com roteamento probabilístico, utilizando um escalonador de eventos baseado em PriorityQueue e um gerador de números pseudoaleatórios LCG.

## Como compilar

No terminal, dentro da pasta onde o arquivo está salvo:

```bash
javac simulacao.java
```

---

## Como executar

```bash
java simulacao
```

O simulador processa a rede até atingir o limite rigoroso de 100.000 números aleatórios consumidos. Ao final, gera um relatório detalhado no terminal contendo:  

Tempo Global Final: O tempo total transcorrido no sistema.  

Perda de Clientes: Contagem de clientes que não conseguiram entrar em filas com capacidade limitada (Q2 e Q3).  

Distribuição de Probabilidades: Porcentagem de tempo que cada fila passou em cada estado (população de 0 a N).  

Tempos Acumulados: Tempo exato de permanência em cada estado, conforme solicitado no enunciado.

---

## Cenário de Validação

| Fila | Configuração | Servidores | Capacidade | Chegadas | Serviço |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Q1** | G/G/1/∞ | 1 | Infinita | 2.0 .. 4.0 | 1.0 .. 2.0 |
| **Q2** | G/G/2/5 | 2 | 5 | — | 4.0 .. 6.0 |
| **Q3** | G/G/2/10 | 2 | 10 | — | 5.0 .. 15.0 |

### **Regras de Roteamento:**
*   **De Q1:** 80% para Q2 e 20% para Q3.
*   **De Q2:** 30% retorna para Q1, 50% vai para Q3 e 20% sai do sistema.
*   **De Q3:** 70% retorna para Q2 e 30% sai do sistema.

> **Observação:** O primeiro cliente chega obrigatoriamente no tempo **2.0**, conforme exigido pelo enunciado.
