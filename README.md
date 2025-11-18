# ToolsChallenge

![Java](https://img.shields.io/badge/Java-17-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.13-6DB33F?logo=springboot)
![Build](https://img.shields.io/badge/Build-Maven-blue?logo=apachemaven)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker)
![OpenAPI](https://img.shields.io/badge/OpenAPI-3-85ea2d?logo=openapiinitiative)
![Version](https://img.shields.io/badge/version-0.0.1--SNAPSHOT-blue)
![License](https://img.shields.io/badge/license-MIT-lightgrey)

## Descrição do Projeto

- API REST em camadas para processamento de pagamentos.
- Principais funcionalidades:
  - `POST /pagamentos`: processa pagamentos com geração de `nsu` e `codigoAutorizacao` quando autorizado
  - `GET /pagamentos/consulta` e `GET /pagamentos/consulta/{id}`: consulta paginada e por ID externo
  - `PATCH /estorno/{id}`: estorna transações previamente autorizadas
- Validações de negócio e formato:
  - Cartão: 16 dígitos OU 4 dígitos + 8–9 asteriscos + 4 dígitos
  - Data/hora: `dd/MM/yyyy HH:mm:ss`
  - Valor: `NNNN.NN` e maior que zero
  - Parcelas: inteiro positivo
  - Tipo de pagamento permitido: `AVISTA`, `PARCELADO LOJA`, `PARCELADO EMISSOR`
- Sanitização: normalização UTF-8 de campos textuais (`estabelecimento`), removendo caracteres inválidos
- Observabilidade: logs estruturados e tratamento global de erros
- Tecnologias:
  - Java 17, Spring Boot 3.3.13, Spring Web, Spring Data JPA, Bean Validation
  - H2 (memória) para desenvolvimento, Maven, Docker
  - `springdoc-openapi` para documentação

## Instruções de Instalação

- Requisitos do sistema:
  - Java 17
  - Maven 3.9+
  - Docker (opcional) e Docker Compose (opcional)
- Passo a passo para configuração do ambiente:
  1. Instale Java 17 e Maven
  2. Opcional: instale Docker Desktop
  3. Clone/copie este projeto
  4. Verifique variáveis de ambiente se necessário
- Comandos para instalação de dependências:
  - `mvn -q -DskipTests dependency:go-offline` (opcional, prefetch)
  - `mvn clean package`

## Guia de Uso

- Execução local:
  - `mvn spring-boot:run`
  - Swagger UI: `http://localhost:8080/swagger`
  - OpenAPI docs: `http://localhost:8080/api-docs`
  - H2 Console: `http://localhost:8080/h2-console` (JDBC `jdbc:h2:mem:testdb`)
  - Via JAR: `java -jar target/ToolsChallenge-0.0.1-SNAPSHOT.jar`

## API

- Endpoints:
  - `POST /pagamentos` — sucesso `201 (Created)` quando `AUTORIZADO`; `402 (Payment Required)` quando `NEGADO`
  - `GET /pagamentos/consulta` — paginação `page>=0`, `rowsPerPage` 1–100; ordenação por `id` em ordem decrescente
  - `GET /pagamentos/consulta/{id}` — 200 quando encontrado; `404 (Not Found)` quando ausente; `400 (Bad Request)` se ID inválido
  - `PATCH /estorno/{id}` — estorno de `AUTORIZADO` para `CANCELADO`; `200 (OK)` sucesso, `400 (Bad Request)` em caso inválido

- Formatos aceitos em `POST /pagamentos`:
  1. Objeto raiz `transacao`:
     ```json
     {
       "transacao": {
         "cartao": "4444*********1234",
         "id": "100023568900001",
         "descricao": { "valor": "50.00", "dataHora": "01/05/2021 18:30:00", "estabelecimento": "PetShop Mundo cão" },
         "formaPagamento": { "tipo": "AVISTA", "parcelas": "1" }
       }
     }
     ```

- Exemplos `curl`:
  - `POST` (objeto raiz `transacao`):
    ```bash
    curl -X POST http://localhost:8080/pagamentos \
      -H "Content-Type: application/json" \
      -d '{"transacao":{"cartao":"4444*********1234","id":"100023568900001","descricao":{"valor":"50.00","dataHora":"01/05/2021 18:30:00","estabelecimento":"PetShop Mundo cão"},"formaPagamento":{"tipo":"AVISTA","parcelas":"1"}}}'
    ```
  - `GET` (paginado):
    ```bash
    curl "http://localhost:8080/pagamentos/consulta?page=0&rowsPerPage=20"
    ```
  - `GET` (por ID):
    ```bash
    curl "http://localhost:8080/pagamentos/consulta/100023568900300"
    ```
  - `PATCH` (estorno):
    ```bash
    curl -X PATCH "http://localhost:8080/estorno/100023568900220"
    ```

- Respostas:
  - Sucesso `201 (Created)` em `POST /pagamentos`: inclui `nsu`, `codigoAutorizacao` e `status: AUTORIZADO`; o cartão é mascarado como `4444*********1234`
  - Erros comuns:
    - `402 (Payment Required)`: regras de negócio/validação de conteúdo
    - `400 (Bad Request)`: parâmetros de consulta inválidos ou payload malformado
    - `404 (Not Found)`: recurso não encontrado em consultas por ID
    - `500 (Internal Server Error)`: falhas internas

## Configuração

- `src/main/resources/application.yml`:
  - Porta: `8080`
  - H2 console: `/h2-console` (JDBC `jdbc:h2:mem:testdb`)
  - OpenAPI: `/api-docs`, Swagger UI: `/swagger`
  - JPA: `ddl-auto: update`, `show-sql: true`

## Estrutura do Projeto

- `controller`: `PaymentController`, `RefundController`
- `service`: `PaymentService`
- `repository`: `PaymentRepository`
- `entity`: `Payment`
- `dto`: requests/responses (`PaymentRequest`, `PaymentResponse`, `PaymentQueryResponse`)
- `validator`: `PaymentRequestValidator`
- `exception`: tratamento global (`GlobalExceptionHandler`) e exceções específicas

## Informações para Contribuição

- Padrões de código:
  - Convenções Java, nomes claros, serviços stateless, validações Bean Validation
  - Camadas: `controller`, `service`, `repository`, `entity`, `dto`, `validator`
- Processo para PRs:
  1. Crie branch a partir de `main`
  2. Execute `mvn test` e garanta sucesso
  3. Abra PR com descrição clara e referências
- Guia para reportar issues:
  - Descreva passos para reproduzir, esperado vs obtido, logs e ambiente

## Licença e Créditos

- Licença: MIT 
- Autores: Lucas Exposito Rocha

## Docker

```bash
docker build -t toolschallenge .
docker run -p 8080:8080 toolschallenge
```

## Docker Compose

```bash
docker compose up --build
```

## Qualidade e Relatórios

- `mvn verify` executa testes e gera relatórios:
  - JaCoCo: `target/site/jacoco/index.html`
  - SpotBugs: `target/site/spotbugs.html`
  - PMD: `target/site/pmd.html` e `target/site/cpd.html`

## Testes

```bash
mvn test
```