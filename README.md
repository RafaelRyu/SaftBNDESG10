```markdown
# 🚀 SAFTBNDES – Sistema de Apoio ao Financiamento Tecnológico

**Instituição:** Profa. Mestre Sirley Ambrosia Vitorio Addão  
**Data de entrega:** 15/05/2026  
**Status:** ✅ Completo (Backend + Frontend + Docker)

---

## 📋 Capa e Introdução

| Campo | Descrição |
|-------|-----------|
| **Título do Projeto** | SAFTBNDES – Sistema de Apoio ao Financiamento Tecnológico |
| **Integrantes** | *[Nome do Aluno 1]*, *[Nome do Aluno 2]*, *[Nome do Aluno 3]* |
| **Objetivo** | Transformar dados públicos de financiamentos do BNDES em **informações estratégicas** para pequenas empresas de tecnologia. O sistema lê arquivos CSV oficiais, permite consultas com filtros inteligentes, gera dashboards analíticos e simula o cadastro de novas operações (CRUD completo). |

O projeto resolve o problema de **falta de transparência e inteligência na tomada de decisão** sobre linhas de crédito. Ele oferece uma aplicação web completa que consome dados reais do Portal de Dados Abertos do BNDES, processa mais de **896 mil registros**, e entrega visualizações e consultas ágeis para análise de mercado.

---

## 🧱 Tecnologias Utilizadas

### Backend (Java + Spring Boot 3.2.5)
- **Spring Boot Starter Web** – API RESTful
- **Spring Boot Starter Data JPA** – Persistência com Hibernate
- **H2 Database** – Banco em memória para desenvolvimento ágil
- **OpenCSV 5.9** – Leitura e parsing do CSV do BNDES
- **Lombok** – Redução de boilerplate (getters, setters, builders)
- **Maven** – Gerenciamento de dependências e build

### Frontend
- **HTML5, CSS3, JavaScript (ES6+)** – Interface responsiva e interativa
- **Chart.js 4.4** – Gráficos dinâmicos (barras, pizza, rosca)
- **Node.js + Express** – Servidor local e proxy reverso para evitar CORS
- **Docker / Docker Compose** – Containerização completa

---

## ⚙️ Funcionamento do Backend (Camadas)

O backend segue a arquitetura **Controller → Service → Repository → Banco**, conforme diagrama abaixo.

```
┌──────────────┐     ┌──────────────┐     ┌────────────────┐     ┌────────────┐
│  Controller  │ --> │   Service    │ --> │   Repository   │ --> │   Banco H2 │
│  (REST)      │ <-- │  (Regras)    │ <-- │   (JPA/JPQL)   │ <-- │   (memória)│
└──────────────┘     └──────────────┘     └────────────────┘     └────────────┘
       ↑                                        ↑
       │                                        │
  Frontend (HTML/JS)                     DTOs (Data Transfer Objects)
```

### 🔹 Model (Entidade `OperacaoBNDES`)
A classe `OperacaoBNDES` é anotada com `@Entity` e mapeia todas as 30 colunas do CSV oficial do BNDES. Principais atributos:

- `idInterno` (chave primária gerada)
- `cliente`, `cpfCnpj`, `uf`, `municipio`, `municipioCodigo`
- `dataDaContratacao` (`LocalDateTime`)
- `valorDaOperacaoEmReais`, `valorDesembolsadoReais`
- `fonteDeRecursoDesembolsos`, `custoFinanceiro`, `juros`
- `prazoCarenciaMeses`, `prazoAmortizacaoMeses`
- `modalidadeDeApoio`, `formaDeApoio`, `produto`, `instrumentoFinanceiro`
- `inovacao`, `areaOperacional`, `setorCnae`, `subsetorCnae...`
- `setorBndes`, `subsetorBndes`, `porteDoCliente`, `naturezaDoCliente`
- `instituicaoFinanceiraCredenciada`, `cnpjDoAgenteFinanceiro`, `situacaoDaOperacao`

### 🔹 Repository (`OperacaoBNDESRepository`)
Interface que estende `JpaRepository` e `JpaSpecificationExecutor`:
- Métodos de busca personalizados com **Specifications** (filtros dinâmicos)
- Queries agregadas com `@Query` usando construtores de **DTOs** (ex.: `ResumoEstadoDTO`, `AgenteConcentracaoDTO`, `PorteDistribuicaoDTO`, etc.)

### 🔹 Service (`OperacaoBNDESService`)
Contém a lógica de negócio:
- **CRUD** completo: `criar()`, `atualizar()`, `deletar()`, `buscarPorId()`
- **Validações** de campos obrigatórios (cliente, UF, valor) com `IllegalArgumentException`
- **Filtro paginado** que constrói dinamicamente `Specification` com base nos parâmetros recebidos
- **Contagem** de registros usando a mesma `Specification`
- **Métodos de agregação** que chamam as queries do repositório

### 🔹 Controller (`OperacaoBNDESController`)
Expõe todos os endpoints REST documentados abaixo. Utiliza `ResponseEntity` para retornar status HTTP adequados (`200 OK`, `201 Created`, `404 Not Found`, `204 No Content`).

### 🔹 Carga de CSV (`CsvLoaderService`)
- Lê o arquivo `operacoes.csv` do classpath
- Processa linha a linha com **OpenCSV**
- Converte strings para tipos nativos (`Double`, `Integer`, `LocalDateTime`)
- Salva em **lotes de 1.000 registros** para evitar estouro de memória (heap)
- Utiliza `EntityManager.flush()` e `clear()` para liberar o contexto de persistência

### 🔹 Tratamento de Erros (`GlobalExceptionHandler`)
Centraliza exceções:
- `ResourceNotFoundException` → **404 Not Found**
- Demais exceções → **500 Internal Server Error** (com log detalhado no console)

### 🔹 Importância dos DTOs
Os **Data Transfer Objects** são essenciais para:
- **Reduzir tráfego**: retornam apenas os campos necessários para cada endpoint
- **Desacoplar** a API da entidade JPA
- **Facilitar** agregações (ex.: `SUM`, `COUNT`, `GROUP BY`) com construtores JPQL
- **Exemplos**: `ResumoEstadoDTO`, `AgenteConcentracaoDTO`, `TicketMedioDTO`, `ProdutoDTO`, etc.

---

## 🌐 Documentação da API (REST)

Base URL: `http://localhost:8080/api`

### 📊 Endpoints de Operações (CRUD + Filtros)

| Método | Rota | Descrição | Status Esperado |
|--------|------|-----------|-----------------|
| `GET` | `/operacoes` | Lista paginada com filtros opcionais (query params) | 200 OK |
| `GET` | `/operacoes/count` | Total de registros (com mesmos filtros) | 200 OK |
| `GET` | `/operacoes/{id}` | Detalhes de uma operação | 200 OK / 404 |
| `POST` | `/operacoes` | Cria nova operação | 201 Created |
| `PUT` | `/operacoes/{id}` | Atualiza operação existente | 200 OK / 404 |
| `DELETE` | `/operacoes/{id}` | Exclui operação | 204 No Content |
| `POST` | `/carga` | Recarrega dados do CSV | 200 OK |

### 📈 Endpoints de Análise (Resumos)

| Método | Rota | Descrição | Status |
|--------|------|-----------|--------|
| `GET` | `/resumo/estados` | Total financiado por UF | 200 |
| `GET` | `/resumo/agentes` | Concentração por agente financeiro | 200 |
| `GET` | `/resumo/porte` | Distribuição por porte do cliente | 200 |
| `GET` | `/resumo/vocacoes` | Vocações regionais (UF x subsetor CNAE) | 200 |
| `GET` | `/resumo/custo` | Categorização por custo financeiro | 200 |
| `GET` | `/resumo/setores` | Top setores BNDES | 200 |
| `GET` | `/resumo/natureza` | Natureza do cliente (público/privado) | 200 |
| `GET` | `/resumo/produtos` | Top produtos | 200 |
| `GET` | `/ticket-medio` | Ticket médio com filtros (porte, setor, UF, produto) | 200 |

### 🔍 Exemplo de requisição (filtro paginado)

```
GET /api/operacoes?uf=RS&porteDoCliente=MICRO&page=0&size=20
```

### 📤 Exemplo de JSON para criação (`POST /api/operacoes`)

```json
{
  "cliente": "Nova Empresa Ltda",
  "uf": "SP",
  "valorDaOperacaoEmReais": 50000.0,
  "produto": "BNDES AUTOMÁTICO",
  "inovacao": "SIM",
  "porteDoCliente": "PEQUENA",
  ...
}
```

---

## 🐳 Guia de Execução (Docker)

### Pré‑requisitos
- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

### Passos

1. Clone o repositório:
```bash
git clone <url-do-repositorio>
cd projetoBNDES
```

2. Inicie os containers:
```bash
docker-compose up --build
```

3. Aguarde a carga do CSV (ver logs do backend). Após a mensagem *"Carga automática finalizada. 896000 operações importadas."*, o sistema estará pronto.

4. Acesse:
- **Frontend:** [http://localhost:3000](http://localhost:3000)
- **Console H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
  - JDBC URL: `jdbc:h2:mem:bndesdb`
  - User: `sa` | Password: `123456`

5. Para parar:
```bash
docker-compose down
```

### Execução sem Docker (alternativa)

**Backend:**
```bash
cd demo
./mvnw spring-boot:run
```

**Frontend:**
```bash
cd frontend
npm install
node app.js
```

---

## 🧩 Estrutura de Pastas

```
projetoBNDES/
├── demo/                         # Backend Spring Boot
│   ├── src/main/java/com/example/demo/
│   │   ├── config/DataLoader.java
│   │   ├── controller/OperacaoBNDESController.java
│   │   ├── dto/*.java             # DTOs para agregações
│   │   ├── exception/*.java       # GlobalExceptionHandler
│   │   ├── model/OperacaoBNDES.java
│   │   ├── repository/OperacaoBNDESRepository.java
│   │   └── service/*.java        # CsvLoaderService, OperacaoBNDESService
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── operacoes.csv          # Arquivo de dados (896k linhas)
│   └── pom.xml
├── frontend/                     # Frontend HTML/JS + servidor Node
│   ├── index.html                # Interface completa
│   ├── app.js                    # Servidor Express com proxy
│   ├── package.json
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## 📌 Considerações Finais

- O sistema atende todos os requisitos da disciplina: **carga de CSV**, **CRUD completo**, **filtros inteligentes**, **resumos/insights**, **tratamento de erros (404/500)**, **interface gráfica com gráficos** e **documentação completa**.
- O uso de **Optional** para evitar `NullPointerException` e a adoção de `ResponseEntity` nos controllers demonstram boas práticas de programação.
- A containerização com **Docker** garante portabilidade e facilidade de implantação.

---

> **Diferencial do Projeto:** foco em pequenas empresas de tecnologia, uso de dados públicos reais, alta performance com 896k registros, e integração com Docker.

---
```
