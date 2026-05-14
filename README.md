
# 🚀 SAFTBNDES – Sistema de Apoio ao Financiamento Tecnológico

![Status](https://img.shields.io/badge/Status-Em_Desenvolvimento-success?style=for-the-badge)
![Backend](https://img.shields.io/badge/Backend-Spring%20Boot%203.2-brightgreen?style=for-the-badge&logo=springboot)
![Frontend](https://img.shields.io/badge/Frontend-Vanilla%20JS%20%2B%20Chart.js%20+%20Node.js-blue?style=for-the-badge&logo=javascript)
![Docker](https://img.shields.io/badge/Deployment-Docker-blue?style=for-the-badge&logo=docker)

O **SAFTBNDES** é uma plataforma desenvolvida para facilitar o acesso a dados de financiamento público. O sistema processa dados reais do **Portal de Dados Abertos do BNDES**, transformando mais de **896 mil registros** em dashboards analíticos e consultas inteligentes para micro e pequenas empresas.
>**Nota**: Os dados são referentes as **operações indiretas automáticas** do BNDES, visto que são o principal meio de obtenção de crédito utilizado pelas empresas e negócios.

---

## 📌 Sumário
* [Introdução](#-introdução)
* [Tecnologias Utilizadas](#-tecnologias-utilizadas)
* [Arquitetura e Camadas](#-arquitetura-e-camadas)
* [Documentação da API](#-documentação-da-api)
* [Guia de Execução (Docker)](#-guia-de-execução-docker)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Considerações Finais](#-considerações-finais)

---

## 📋 Introdução

| Informações Gerais | Detalhes |
|:--- |:--- |
| **Instituição** | Fatec Zona Leste |
| **Data de Entrega** | 15/05/2026 |
| **Integrantes** | Gustavo Amorim e Rafael Ryu |
| **Objetivo** | Transformar dados brutos (CSV) em inteligência de mercado e CRUD operacional. |

> **O Problema:** A falta de informação e dados excessivos dificulta a tomada de decisão.  
> **A Solução:** Uma aplicação Web robusta com processamento em lote, filtros dinâmicos e visualização de dados ágil para facilitar a análise dos dados e tomada de decisão.

---

## 🧱 Tecnologias Utilizadas

### 💻 Backend
* **Java 17 & Spring Boot 3.2.5**
* **Spring Data JPA** (Persistência e Queries complexas)
* **H2 Database** (Banco SQL em memória de alta performance)
* **OpenCSV 5.9** (Parsing eficiente de arquivos grandes)
* **Lombok** (Produtividade no código Java)

### 🎨 Frontend
* **Vanilla Stack:** HTML5, CSS3, JavaScript (ES6+)
* **Chart.js 4.4:** Dashboards dinâmicos (Pizza, Barras, Rosca)
* **Node.js + Express:** Servidor de arquivos estáticos.

### 🐋 Infraestrutura
* **Docker & Docker Compose** (Containerização total do ecossistema)


## ⚙️ Arquitetura e Camadas

O backend segue o padrão de arquitetura em camadas para garantir escalabilidade e manutenção.


    A[Frontend] --> B[Controller]
    B --> C[Service]
    C --> D[Repository]
    D --> E[(H2 Database)]
    C -.-> F[DTOs]

### 🔹 Destaques da Implementação

* **Model (`OperacaoBNDES`)**: Mapeamento completo de 30 colunas do CSV oficial.
* **Filtros Dinâmicos**: Uso de `JpaSpecificationExecutor` para buscas complexas (UF, porte, setor).
* **Performance**: Carga de CSV em lotes de 1.000 registros com `EntityManager.flush()` para evitar estouro de memória.
* **DTOs**: Objetos específicos para reduzir o tráfego de dados e facilitar agregações SQL (`SUM`, `COUNT`, `AVG`).

---

## 🌐 Documentação da API (REST)

**Base URL:** `http://localhost:8080/api`

### 📊 Operações (CRUD & Filtros)

| Método | Rota | Função | Status |
| --- | --- | --- | --- |
| `GET` | `/operacoes` | Lista paginada com filtros dinâmicos | `200` |
| `GET` | `/operacoes/count` | Totalizador de registros sob filtros | `200` |
| `POST` | `/operacoes` | Cadastro de nova operação | `210` |
| `PUT` | `/operacoes/{id}` | Atualização de dados | `200/404` |
| `DELETE` | `/operacoes/{id}` | Remoção física da operação | `204` |

### 📈 Inteligência (Analytics)

| Método | Rota | Insight Gerado | Status |
| --- | --- | --- | --- |
| `GET` | `/resumo/estados` | Distribuição de financiamento por UF | `200` |
| `GET` | `/resumo/agentes` | Maiores agentes financeiros (Bancos) | `200` |
| `GET` | `/resumo/porte` | Crédito por tamanho da empresa | `200` |
| `GET` | `/ticket-medio` | Cálculo de média ponderada filtrada | `200` |

---

## 🐳 Guia de Execução (Docker)

Siga os passos abaixo para subir o ambiente completo em segundos:

1. **Clonar e Acessar:**
```bash
git clone https://github.com/RafaelRyu/SaftBNDESG10.git
cd SaftBNDESG10
```


2. **Subir Containers:**
```bash
docker-compose up --build

```


3. **Acessar o Sistema:**
* **Dashboard Web:** [http://localhost:3000](https://www.google.com/search?q=http://localhost:3000)
* **API / H2 Console:** [http://localhost:8080/h2-console](https://www.google.com/search?q=http://localhost:8080/h2-console)
* *JDBC URL:* `jdbc:h2:mem:bndesdb`
* *User:* `sa`
* *Pass:* `123456`





> 💡 **Nota:** O sistema levará alguns segundos na primeira execução para processar as 896 mil linhas do arquivo `operacoes.csv`. Acompanhe o progresso nos logs do terminal.

> 💡 **Nota:** Os nomes e senhas mostrados são para teste. Você pode (e deve) mudar o usuário e senha ao rodar a aplicação para que outros usuários não acessem sua aplicação sem permissão.

---

## 🧩 Estrutura do Projeto

```text
projetoBNDES/
├── demo/                       # ☕ Backend Spring Boot
│   ├── src/main/java/          # Código fonte Java
│   ├── src/main/resources/     # CSV e application.properties
│   └── pom.xml                 # Dependências (JPA, OpenCSV e Drivers)
├── frontend/                   # 🌐 Frontend Web
│   ├── index.html              # UI Principal
│   ├── app.js                  # Servidor Node.js
│   └── Dockerfile              # Setup Docker Front
├── docker-compose.yml          # Orquestrador de serviços
└── README.md                   # Documentação

```

---

## 📌 Considerações Finais

* **Robustez**: Tratamento global de exceções para erros 404 e 500.
* **Boas Práticas**: Uso intensivo de `Optional`, `ResponseEntity` e Injeção de Dependência.
* **Escalabilidade**: O backend pode ser conectado em outros bancos SQL (PostgreSQL/MySQL) com alterações mínimas no arquivo `properties` (visto que o H2 é um banco SQL).

---

**Desenvolvido como projeto acadêmico para Análise de Dados e Padrões de Projeto.**

```

```
