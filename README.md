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


O código já vem pré-configurado com a topologia de rede apresentada no trabalho:  FilaConfiguraçãoServidoresCapacidadeChegadas (min..max)Serviço (min..max)Q1G/G/1/∞1Infinita2.0 .. 4.01.0 .. 2.0Q2G/G/2/525—4.0 .. 6.0Q3G/G/2/10210—5.0 .. 15.0Regras de Roteamento:De Q1: 80% para Q2 e 20% para Q3.  De Q2: 30% retorna para Q1, 50% vai para Q3 e 20% sai do sistema.  De Q3: 70% retorna para Q2 e 30% sai do sistema.  Primeira Chegada: Ocorre obrigatoriamente no tempo 2.0. 
