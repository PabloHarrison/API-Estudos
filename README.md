# API de Estudos

**API de Estudos para salvar registros em um banco de dados com Spring Boot.**

## Descrição

Projeto com intuito de criar registros de estudos realizados de forma personalizada.

Seu desenvolvimento foi iniciado por uma necessidade pessoal de organização dos estudos e evoluiu para uma API com autenticação e deploy em produção.

## Acesso

- API (produção): https://api-estudos-p2jq.onrender.com
- Swagger: https://api-estudos-p2jq.onrender.com/swagger-ui/index.html#/

## Status do Projeto

🚧 Em desenvolvimento contínuo — já em produção

## Pré-requisitos

- Java 21
- PostgreSQL rodando (necessário apenas para ambiente local)

## Rodando a API

1. Clonar o repositório

```bash
git clone https://github.com/PabloHarrison/API-Estudos.git
```

2. Entrar na pasta do projeto

```bash
cd DBEstudosAPI
```

3. Configurar o banco no arquivo `application.properties` (ou `.yml`):

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

4. Rodar a API

```bash
./mvnw spring-boot:run
```

A API estará disponível em:  
http://localhost:8080

## Autenticação

A API utiliza Spring Security com:

- JWT (Access Token)
- Refresh Token
- OAuth2

### Uso

Endpoints protegidos requerem envio do token no header:

```http
Authorization: Bearer {token}
```

## Endpoints

### Autenticação do Usuário

| Método | Endpoint       | Ação                     |
|--------|----------------|--------------------------|
| POST   | /auth/register | Registra usuário         |
| POST   | /auth/login    | Realiza login            |
| POST   | /auth/refresh  | Gera novo access token   |

### Categorias

| Método | Endpoint    | Ação                           |
|--------|-------------|--------------------------------|
| GET    | /categorias | Lista todas as categorias      |
| POST   | /categorias | Cria uma nova categoria        |
| GET    | /categorias/{id} | Buscar categoria pelo ID  |
| DELETE | /categorias/{id} | Deletar categoria pelo ID |
| PATCH  | /categorias/{id} | Atualizar categoria pelo ID |

### Registros

| Método | Endpoint       | Ação                           |
|--------|----------------|--------------------------------|
| GET    | /registros     | Lista todos os registros       |
| POST   | /registros     | Cria um novo registro          |
| GET    | /registros/{id}| Buscar um registro pelo ID     |
| DELETE | /registros/{id}| Deletar registro pelo ID       |
| PATCH  | /registros/{id}| Atualizar registro pelo ID     |

## Testes

- Testes automatizados implementados  
- Cobertura de código analisada com JaCoCo  

## Tecnologias usadas

- Java 21  
- Spring Boot 3.5.9  
- Spring Security  
- PostgreSQL  
- JWT  
- OAuth2  
- JaCoCo
- Mockito
