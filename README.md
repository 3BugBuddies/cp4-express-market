# Express Market API — CheckPoint 4 (Parte I) | Java Advanced

API REST desenvolvida com Spring Boot para gerenciamento do estoque de um mercado express, como parte do CheckPoint 4 — Parte I da disciplina de Java Advanced (TDS) — FIAP.

A aplicação expõe um CRUD completo sobre a tabela `TDS_TB_mercado` do banco Oracle da FIAP, com respostas no padrão **HATEOAS** (nível 3 de maturidade de Richardson), validação de entrada, DTOs de request/response e CORS configurável.

---

## Integrantes do Grupo

| Nome | RM |
|------|----|
| Felipe Yuiti Ishii | 565339 |
| Gabriel Nogueira Peixoto | 563925 |
| Giovanna Neri dos Santos | 566154 |
| Mariana Inoue | 565834 |

**IDE utilizada:** IntelliJ IDEA

---

## Configuração do Projeto — Spring Initializr

![Spring Initializr](./assets/spring-initializr.png)

### Dependências

| Dependência | Categoria | Descrição |
|-------------|-----------|-----------|
| Spring Web | WEB | Criação de APIs REST com Spring MVC |
| Spring HATEOAS | WEB | Links de navegação nas respostas (maturidade nível 3) |
| Spring Data JPA | SQL | Persistência com JPA e Hibernate |
| Validation | I/O | Bean Validation com Hibernate Validator |
| Lombok | Developer Tools | Redução de boilerplate (getters, setters, construtores) |
| Oracle Driver | SQL | Driver JDBC para Oracle Database |
| Spring Boot DevTools | Developer Tools | LiveReload e reinicialização automática |

---

## Estrutura do Projeto

```
src/
└── main/
    ├── java/com/br/fiap/express_market/api/
    │   ├── ExpressMarketApiApplication.java   # Classe principal
    │   ├── config/
    │   │   └── CorsConfig.java               # Liberação de origens (CORS)
    │   ├── controller/
    │   │   └── ProdutoController.java        # Endpoints REST
    │   ├── dto/
    │   │   ├── ProdutoRequest.java           # Corpo do POST e do PUT
    │   │   ├── ProdutoPatchRequest.java      # Corpo do PATCH (campos opcionais)
    │   │   └── ProdutoResponse.java          # Representação devolvida pela API
    │   ├── entity/
    │   │   └── Produto.java                  # Entidade JPA
    │   ├── exception/
    │   │   └── GlobalExceptionHandler.java   # Tradução de erros de validação
    │   ├── hateoas/
    │   │   └── ProdutoModelAssembler.java    # Montagem dos links HATEOAS
    │   ├── repository/
    │   │   └── ProdutoRepository.java        # Interface de acesso a dados
    │   └── service/
    │       └── ProdutoService.java           # Regras de negócio
    └── resources/
        └── application.properties            # Configurações da aplicação
```

---

## Decisões de Arquitetura

### Por que Repository e não DAO?

O Spring Data JPA já gerencia o `EntityManager` automaticamente — ciclo de vida, transações, thread-safety. O `JpaRepository` entrega o CRUD pronto via interface, sem precisar implementar nada na mão.

DAO faria sentido se precisássemos de controle fino sobre o `EntityManager`. Aqui, o Spring cuida disso melhor do que faríamos manualmente.

### Por que DTOs em vez de expor a entidade?

A entidade é o formato do banco; o DTO é o contrato da API. Separar os dois traz três ganhos concretos:

- **`ProdutoRequest` não tem `id`.** Na criação o id vem da sequence e na atualização vem da URL, então o cliente não consegue trocar a identidade do recurso mandando um id no corpo.
- **`ProdutoPatchRequest` tem todos os campos opcionais.** É o que permite o PATCH parcial: campo nulo significa "não mexe", e as constraints `@Size`/`@Positive` ignoram nulos, validando só o que foi enviado.
- **`ProdutoResponse` controla o que sai.** Se amanhã a entidade ganhar um campo interno, ele não vaza para a API sem alguém decidir isso.

A conversão é feita de forma direta, sem camada de mapeamento intermediária: o `ProdutoService` monta a entidade a partir do request nas operações de escrita, e o `ProdutoModelAssembler` monta o `ProdutoResponse` a partir da entidade na leitura.

### Por que não usar `@Data` na entidade?

O `@Data` do Lombok gera `equals`/`hashCode` usando todos os campos. Em entidade JPA isso quebra: duas instâncias do mesmo registro deixam de ser iguais quando um campo muda, e o `hashCode` de uma entidade nova se altera depois que a sequence atribui o id — o que corrompe o comportamento dela dentro de coleções.

Por isso a entidade usa `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor` e `@Builder` de forma explícita, sem `@Data`.

### HATEOAS — maturidade nível 3

No nível 3 de Richardson a resposta não carrega só os dados: ela carrega também os links das ações possíveis sobre o recurso. O cliente navega pela API seguindo esses links, em vez de montar URLs na mão.

O `ProdutoModelAssembler` monta, para cada produto, os links `self`, `mercado`, `update`, `patch` e `delete`. O POST ainda devolve o header `Location` apontando para o recurso criado.

---

## Como Executar

### Pré-requisitos

- Java 21+
- Maven 3.9+
- Acesso à rede da FIAP (VPN ou rede local) para conectar ao Oracle

### Rodando a aplicação

```bash
# Clonar o repositório
git clone https://github.com/3BugBuddies/cp4-express-market.git

# Entrar na pasta do projeto
cd cp4-express-market

# Configurar application.properties com suas credenciais do Oracle
# (ver a seção "Configuração do Banco de Dados" abaixo)

# Executar com Maven
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8082`, conforme exigido pelo enunciado.

---

## Configuração do Banco de Dados

Banco Oracle disponibilizado pela FIAP. Configure `src/main/resources/application.properties` com suas credenciais:

```properties
spring.datasource.url=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
spring.datasource.username=<SEU_RM>
spring.datasource.password=<SUA_SENHA>
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### Print do Banco de Dados

<!-- TODO: substituir pelo print do SQL Developer com a TDS_TB_mercado populada -->
> ⏳ _Print do SQL Developer pendente._

---

## Configuração de CORS

O `CorsConfig` libera o consumo da API por front-ends hospedados em outra origem. As origens permitidas vêm de uma propriedade, o que permite abrir em desenvolvimento e restringir no deploy sem recompilar:

```properties
# Todas as origens (desenvolvimento)
cors.allowed-origins=*

# Origens específicas (produção)
cors.allowed-origins=https://meu-front.com,https://admin.meu-front.com
```

Métodos liberados: `GET`, `POST`, `PUT`, `PATCH`, `DELETE` e `OPTIONS`. O header `Location` é exposto explicitamente, senão o browser não conseguiria ler a URL do recurso criado pelo POST.

---

## Modelo de Dados

### Fluxo da Requisição

```mermaid
flowchart LR
    Cliente["Postman / Insomnia"]
    Controller["ProdutoController<br/>/mercado"]
    Service["ProdutoService"]
    Repository["ProdutoRepository<br/>Spring Data JPA"]
    Assembler["ProdutoModelAssembler<br/>links HATEOAS"]
    Oracle[("Oracle FIAP<br/>TDS_TB_mercado")]

    Cliente -->|"HTTP + JSON"| Controller
    Controller --> Service
    Service --> Repository
    Repository -->|"Hibernate / JDBC"| Oracle
    Oracle -->|"resultado"| Repository
    Controller --> Assembler
    Assembler -->|"JSON + _links"| Cliente
```

### Diagrama de Classe

```mermaid
classDiagram
    direction TB

    class ProdutoController {
        <<RestController>>
        -ProdutoService produtoService
        -ProdutoModelAssembler assembler
        +create(request) ResponseEntity
        +findAll() ResponseEntity
        +findById(id) ResponseEntity
        +update(id, request) ResponseEntity
        +patch(id, request) ResponseEntity
        +delete(id) ResponseEntity
    }

    class ProdutoService {
        <<Service>>
        -ProdutoRepository produtoRepository
        +save(request) Produto
        +findAll() List~Produto~
        +findById(id) Optional~Produto~
        +update(id, request) Optional~Produto~
        +patch(id, request) Optional~Produto~
        +deleteById(id) boolean
    }

    class ProdutoRepository {
        <<interface>>
        +save(produto) Produto
        +findAll() List~Produto~
        +findById(id) Optional~Produto~
        +delete(produto) void
    }

    class Produto {
        <<Entity>>
        -Long id
        -String nome
        -String tipo
        -String setor
        -String tamanho
        -Double preco
    }

    class ProdutoRequest {
        <<record>>
        +String nome
        +String tipo
        +String setor
        +String tamanho
        +Double preco
    }

    class ProdutoPatchRequest {
        <<record>>
        +String nome
        +String tipo
        +String setor
        +String tamanho
        +Double preco
    }

    class ProdutoResponse {
        <<record>>
        +Long id
        +String nome
        +String tipo
        +String setor
        +String tamanho
        +Double preco
    }

    class ProdutoModelAssembler {
        <<Component>>
        +toModel(produto) EntityModel
    }

    class GlobalExceptionHandler {
        <<RestControllerAdvice>>
        +handleBodyValidation(ex) ResponseEntity
        +handleEntityValidation(ex) ResponseEntity
    }

    ProdutoController --> ProdutoService : usa
    ProdutoController --> ProdutoModelAssembler : usa
    ProdutoController ..> ProdutoRequest : recebe
    ProdutoController ..> ProdutoPatchRequest : recebe
    ProdutoService --> ProdutoRepository : usa
    ProdutoService ..> Produto : constroi
    ProdutoRepository ..> Produto : persiste
    ProdutoModelAssembler ..> Produto : le
    ProdutoModelAssembler ..> ProdutoResponse : produz
    GlobalExceptionHandler ..> ProdutoController : intercepta
```

Os campos de `ProdutoRequest`, `ProdutoPatchRequest` e `ProdutoResponse` são iguais em nome, mas diferem no contrato: o request de criação exige todos preenchidos, o de PATCH aceita todos nulos, e só a resposta expõe o `id`.

### Entidade `Produto`

Tabela Oracle: `TDS_TB_mercado` | Sequence: `SQ_TDS_TB_mercado`

| Campo | Coluna | Tipo | Validações |
|-------|--------|------|-----------|
| id | id_produto | Long (PK) | Gerado por sequence |
| nome | nm_nome_produto | String | Obrigatório, 3–50 caracteres |
| tipo | tp_tipo | String | Obrigatório, 3–50 caracteres |
| setor | st_setor | String | Obrigatório, 3–50 caracteres |
| tamanho | tm_tamanho | String | Obrigatório, 2–50 caracteres |
| preco | pr_preco | Double | Obrigatório, valor positivo |

---

## Endpoints da API

Base URL: `http://localhost:8082/mercado`

| Método | Rota | Descrição | Status de Sucesso |
|--------|------|-----------|-------------------|
| `POST` | `/mercado` | Cadastrar novo produto | `201 Created` |
| `GET` | `/mercado` | Listar todos os produtos | `200 OK` |
| `GET` | `/mercado/{id}` | Buscar produto por ID | `200 OK` |
| `PUT` | `/mercado/{id}` | Substituir produto existente | `200 OK` |
| `PATCH` | `/mercado/{id}` | Atualizar campos específicos | `200 OK` |
| `DELETE` | `/mercado/{id}` | Remover produto | `204 No Content` |

Requisição em recurso inexistente devolve `404 Not Found`; corpo inválido devolve `400 Bad Request` com a lista de campos rejeitados.

---

## Testes

Todos os testes abaixo foram executados contra `http://localhost:8082`.

### Coleção pronta

O repositório traz uma coleção com os 8 cenários já montados, incluindo asserções automáticas de status, de campos e da presença dos links HATEOAS:

```
postman/express-market-api.postman_collection.json
```

**Postman:** `Import` → selecione o arquivo → rode com o **Collection Runner**.
**Insomnia:** `Import` → `From File` → selecione o mesmo arquivo.

Os requests estão numerados e devem rodar em ordem: o POST guarda o id gerado na variável `produtoId`, que os requests seguintes reaproveitam até o DELETE. A variável `baseUrl` já aponta para `http://localhost:8082` e pode ser trocada pela URL do deploy.

| # | Cenário | Esperado |
|---|---------|----------|
| 1 | POST — Criar produto | `201` + header `Location` + links HATEOAS |
| 2 | GET — Listar todos | `200` + coleção em `_embedded` |
| 3 | GET — Buscar por ID | `200` + id correspondente |
| 4 | PUT — Substituir | `200` + todos os campos trocados |
| 5 | PATCH — Atualizar parcialmente | `200` + só o campo enviado muda |
| 6 | DELETE — Remover | `204` sem corpo |
| 7 | POST — Corpo inválido | `400` + erros por campo |
| 8 | GET — Produto inexistente | `404` |

### POST — Criar produto

Cadastra um novo produto na tabela `TDS_TB_mercado` e retorna o objeto criado com o ID gerado pela sequence, além do header `Location`.

**Requisição** — `POST /mercado`

```json
{
  "nome": "Detergente Neutro 500ml",
  "tipo": "Produto de Limpeza",
  "setor": "Limpeza",
  "tamanho": "500ml",
  "preco": 3.49
}
```

**Resposta** — `201 Created`

```json
{
  "id": 1,
  "nome": "Detergente Neutro 500ml",
  "tipo": "Produto de Limpeza",
  "setor": "Limpeza",
  "tamanho": "500ml",
  "preco": 3.49,
  "_links": {
    "self":    { "href": "http://localhost:8082/mercado/1" },
    "mercado": { "href": "http://localhost:8082/mercado" },
    "update":  { "href": "http://localhost:8082/mercado/1" },
    "patch":   { "href": "http://localhost:8082/mercado/1" },
    "delete":  { "href": "http://localhost:8082/mercado/1" }
  }
}
```

<!-- TODO: print do POST no Insomnia -->
> ⏳ _Print pendente._

---

### GET — Listar todos

Retorna todos os produtos cadastrados. A coleção vem em `_embedded`, com os links de cada item preservados.

**Requisição** — `GET /mercado`

**Resposta** — `200 OK`

```json
{
  "_embedded": {
    "produtoResponseList": [
      {
        "id": 1,
        "nome": "Detergente Neutro 500ml",
        "tipo": "Produto de Limpeza",
        "setor": "Limpeza",
        "tamanho": "500ml",
        "preco": 3.49,
        "_links": {
          "self":    { "href": "http://localhost:8082/mercado/1" },
          "mercado": { "href": "http://localhost:8082/mercado" },
          "update":  { "href": "http://localhost:8082/mercado/1" },
          "patch":   { "href": "http://localhost:8082/mercado/1" },
          "delete":  { "href": "http://localhost:8082/mercado/1" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8082/mercado" }
  }
}
```

<!-- TODO: print do GET all no Insomnia -->
> ⏳ _Print pendente._

---

### GET — Buscar por ID

Retorna um produto específico. Devolve `404` se o ID não existir.

**Requisição** — `GET /mercado/1`

<!-- TODO: print do GET por id no Insomnia -->
> ⏳ _Print pendente._

---

### PUT — Substituir

Substitui **todos** os campos do produto. Todos os campos são obrigatórios no corpo. Se o ID não existir, devolve `404` — o PUT não cria registro novo.

**Requisição** — `PUT /mercado/1`

```json
{
  "nome": "Detergente Neutro 1L",
  "tipo": "Produto de Limpeza",
  "setor": "Limpeza",
  "tamanho": "1L",
  "preco": 5.99
}
```

<!-- TODO: print do PUT no Insomnia -->
> ⏳ _Print pendente._

---

### PATCH — Atualizar parcialmente

Atualiza **somente** os campos enviados. Os campos ausentes mantêm o valor já persistido — é a diferença prática para o PUT.

**Requisição** — `PATCH /mercado/1`

```json
{
  "preco": 4.79
}
```

<!-- TODO: print do PATCH no Insomnia -->
> ⏳ _Print pendente._

---

### DELETE — Remover

Remove o produto do banco pelo ID. Devolve `204 No Content` em caso de sucesso ou `404` se o ID não existir.

**Requisição** — `DELETE /mercado/1`

<!-- TODO: print do DELETE no Insomnia -->
> ⏳ _Print pendente._

---

### Erro de validação

Corpo inválido é traduzido pelo `GlobalExceptionHandler` em uma resposta legível, em vez de stacktrace.

**Requisição** — `POST /mercado`

```json
{
  "nome": "AB",
  "tipo": "Fruta",
  "setor": "Hortifruti",
  "tamanho": "1kg",
  "preco": -2.0
}
```

**Resposta** — `400 Bad Request`

```json
{
  "timestamp": "2026-08-13T22:10:31.482Z",
  "status": 400,
  "error": "Bad Request",
  "errors": {
    "nome": "O nome deve ter entre 3 e 50 caracteres",
    "preco": "O preço deve ser um valor positivo"
  }
}
```

---

## Deploy

<!-- TODO: plataforma, link público e instruções de Docker -->
> ⏳ _Deploy pendente._

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 4.1.0**
- **Spring Data JPA + Hibernate**
- **Spring Web (MVC)**
- **Spring HATEOAS**
- **Bean Validation (Hibernate Validator)**
- **Lombok**
- **Oracle Database (OJDBC 11)**
- **Maven**
