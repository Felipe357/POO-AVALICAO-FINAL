<div align="center">

# Clube Vantagens

### Fidelidade que se transforma em benefícios.

**Cadastre. Bonifique. Resgate. Acompanhe.**

Uma aplicação desktop para gerenciar membros e recompensas, com dois planos de fidelidade, ranking de pontos e extratos de movimentação.

![Java 17+](https://img.shields.io/badge/Java-17%2B-E76F00?style=flat-square)
![Interface Swing](https://img.shields.io/badge/Interface-Swing-276578?style=flat-square)
![Dados em memória](https://img.shields.io/badge/Dados-100%25_em_RAM-25865B?style=flat-square)

[Conheça o sistema](#o-sistema-em-ação) · [Funcionalidades](#o-que-você-pode-fazer) · [Executar](#como-executar) · [Arquitetura](#arquitetura)

</div>

![Ranking de membros e extrato de movimentações](docs/imagens/02-ranking-extrato.png)

## A proposta

O Clube Vantagens reúne em uma única janela o ciclo de um programa de fidelidade: adesão de membros, acúmulo de pontos, escolha de benefícios e acompanhamento dos saldos. Cada operação atualiza a listagem, os indicadores e o histórico do membro.

Desenvolvido para a **P2 prática de Programação Orientada a Objetos**, da Fatec Mogi Mirim, com o Prof. Mestre Marcos Roberto de Moraes (Maromo). **Tema 07 — Clube de Assinaturas e Fidelidade**, com base no enunciado da atividade e nos tópicos POO_07 a POO_10.

| Integrantes |
| --- |
| Felipe Augusto Ribeiro Serra |
| Diogo Americo Cavenaghi |

## O que você pode fazer

| Recurso | Na prática |
| --- | --- |
| **Adesão ao clube** | Cadastre nome e e-mail, escolha o plano e receba um ID sequencial. |
| **Dois planos** | Essencial acumula 1 ponto por ponto-base; Premium acumula 2. |
| **Resgate de benefícios** | Troque o saldo por café especial, vale-presente ou kit exclusivo. |
| **Ranking atualizado** | Veja os maiores saldos primeiro, com desempate por ID crescente. |
| **Pesquisa rápida** | Localize membros por nome, e-mail ou ID. |
| **Extrato individual** | Consulte créditos, resgates e saldo com data e hora das operações. |
| **Indicadores do clube** | Acompanhe a quantidade de membros e os pontos em circulação. |

## O sistema em ação

As imagens mostram a aplicação Swing executada em uma tela virtual, com dados de demonstração. Os cadastros não são carregados automaticamente ao iniciar o programa.

### 01 · Adesão simples

Nome, e-mail e plano em um formulário direto. A listagem permite consultar os membros e selecionar quem receberá os pontos.

![Tela de adesão com membros cadastrados](docs/imagens/01-cadastro.png)

### 02 · Pontos, ranking e extrato

O plano Premium aplica o fator 2x no crédito. No exemplo, 150 pontos-base geram 300 pontos; após resgatar um café de 100 pontos, o membro fica com 200. O extrato apresenta cada etapa.

![Operações de pontos, ranking e histórico individual](docs/imagens/02-ranking-extrato.png)

### 03 · Validação antes do resgate

Se o saldo não cobrir o benefício, a aplicação informa o motivo em um diálogo e preserva os pontos e o histórico.

![Mensagem de saldo insuficiente ao tentar resgatar um benefício](docs/imagens/03-saldo-insuficiente.png)

## Planos e benefícios

| Plano | Multiplicador | Exemplo com 150 pontos-base |
| --- | :---: | :---: |
| Essencial | **1x** | 150 pontos |
| Premium | **2x** | 300 pontos |

| Benefício | Custo |
| --- | ---: |
| Café especial | 100 pontos |
| Vale-presente de R$ 25 | 250 pontos |
| Kit exclusivo do clube | 500 pontos |

Os planos definem o fator de acúmulo; não há cobrança de assinatura. O ranking considera o saldo disponível, portanto os resgates podem mudar a classificação.

## Como executar

Você precisa do **JDK 17 ou superior**, com `java` e `javac` no PATH, e de um ambiente gráfico de desktop. O projeto usa apenas a biblioteca padrão do Java.

**1. Clone e entre no repositório**

```bash
git clone https://github.com/Felipe357/POO-AVALICAO-FINAL.git
cd POO-AVALICAO-FINAL
```

**2. Inicie a aplicação**

Linux ou macOS:

```bash
./executar.sh
```

Windows:

```bat
executar.bat
```

Os scripts compilam as fontes em UTF-8, com compatibilidade Java 17, e abrem a janela principal. Na IDE, configure `src` como pasta de fontes e execute a classe `app.Principal`.

<details>
<summary>Compilação manual pelo terminal</summary>

Crie a pasta `out` e execute na raiz do projeto:

```bash
javac --release 17 -encoding UTF-8 -d out src/app/*.java src/controller/*.java src/exception/*.java src/interfaces/*.java src/model/*.java src/view/*.java
java -cp out app.Principal
```

</details>

> **Dados durante a execução:** os cadastros, saldos e históricos ficam somente em RAM. Ao encerrar, tudo é descartado. A pasta `out/` contém apenas os arquivos de compilação e é ignorada pelo Git.

## Arquitetura

O código separa a interface Swing, a coordenação das operações e as regras do domínio.

```text
src/
├── app/
│   └── Principal.java
├── controller/
│   └── GerenciadorClube.java
├── exception/
│   ├── CadastroInvalidoException.java
│   └── PontuacaoInsuficienteException.java
├── interfaces/
│   └── Bonificavel.java
├── model/
│   ├── Beneficio.java
│   ├── MembroClube.java
│   ├── MembroEssencial.java
│   └── MembroPremium.java
└── view/
    └── TelaClube.java
```

| Camada | Responsabilidade |
| --- | --- |
| `model` | Membros, planos, catálogo de benefícios e regras de crédito e resgate. |
| `interfaces` | Contrato `Bonificavel`, com operações de pontos e consulta de saldo por método `default`. |
| `exception` | Exceções verificadas para cadastro inválido e pontuação insuficiente. |
| `controller` | Lista de membros, IDs, pesquisa, ordenação, operações e montagem dos extratos. |
| `view` | Formulários, listagem, eventos de clique e mensagens ao usuário. |
| `app` | Inicialização na Event Dispatch Thread e abertura da janela. |

### Conceitos de POO aplicados

| Requisito | Implementação |
| --- | --- |
| **Encapsulamento** | Campos privados, acesso por métodos e alteração do saldo somente pelas operações de negócio. |
| **Herança e polimorfismo** | `MembroEssencial` e `MembroPremium` especializam `MembroClube` e definem os fatores 1x e 2x. |
| **Interface de negócio** | `MembroClube` implementa `Bonificavel`; o controlador utiliza esse contrato no acúmulo. |
| **Checked exceptions** | As duas exceções customizadas herdam diretamente de `Exception`; a tela apresenta as mensagens por `JOptionPane`. |
| **Collections e generics** | `List<MembroClube> membros = new ArrayList<>()`, sem banco ou arquivos de dados. |
| **Ordenação natural** | `Comparable<MembroClube>` ordena por ID, aplicado com `Collections.sort()`. O ranking acrescenta um `Comparator` por saldo. |
| **Identidade dos objetos** | `equals()`, `hashCode()` e `compareTo()` usam o ID imutável. |
| **Eventos Swing** | `ActionListener` nos botões e `WindowAdapter`, que implementa `WindowListener`, no fechamento. |
| **Conversão numérica** | Captura de `NumberFormatException` nos campos de ID e pontos. |

### Regras que preservam os dados

- Nome com pelo menos três caracteres e e-mail com validação básica de formato.
- E-mail único, normalizado, sem diferenciar maiúsculas de minúsculas.
- Pontos-base inteiros e positivos, com proteção contra estouro do limite de `int`.
- Resgates sem saldo suficiente não alteram o membro.
- Listas retornadas pelo controlador e pelo histórico não permitem inclusão ou remoção externa.
- Botão Sair e fechamento pelo X pedem confirmação antes de descartar a sessão.

## Registro de apoio de IA

Foi utilizado **OpenAI Codex** como apoio à implementação, à interface Swing e à documentação. Abaixo está o registro resumido dos pedidos reais desta sessão e dos resultados adotados.

<details>
<summary>Consultar pedidos e resultados</summary>

Os diálogos abaixo resumem os pedidos e as respostas da sessão; não são transcrições literais.

### 1. Criação do projeto

**Pedido:** Criar um projeto seguindo o enunciado, usando `avaliacaofinalpoo/` como referência, com uma solução diferente.

**Resposta:** Foi desenvolvido o tema 07, Clube de Assinaturas e Fidelidade, com membros Essencial e Premium, contrato de negócio, exceções customizadas, controlador e interface Swing. Os refinamentos incluíram validação de e-mail duplicado, proteção do saldo e coerência entre identidade e ordenação.

### 2. Identificação da dupla

**Pedido:** Incluir Felipe Augusto Ribeiro Serra e Diogo Americo Cavenaghi, sem RA.

**Resposta:** Os nomes foram incluídos na identificação da dupla e os campos de RA foram removidos.

### 3. Apresentação e organização da entrega

**Pedido:** Melhorar o README, incluir imagens do sistema em execução, remover os testes e organizar os commits no repositório configurado.

**Resposta:** O README foi reorganizado com apresentação do produto, funcionalidades e três imagens da aplicação. Os arquivos de teste foram removidos, o fluxo Swing foi verificado e os arquivos foram organizados em commits reais por assunto.

### 4. Formato do registro

**Pedido:** Apresentar os pedidos e resultados em formato de conversa.

**Resposta:** A tabela foi convertida em pares de pedido e resposta resumidos, preservando o histórico real do apoio utilizado.

</details>

---

<div align="center">

**Clube Vantagens · POO · Fatec Mogi Mirim**

Felipe Augusto Ribeiro Serra & Diogo Americo Cavenaghi

</div>
