# API de Estudos

**API de Estudos para salvar em um banco de dados com Spring Boot (Ainda em desenvolvimento).**

## Descrição

Projeto com intuito de criar registros de estudos realizados.
Seu desenvolvimento foi iniciado por uma necessidade de registrar meus estudos de forma personalizada.
Foi utilizado de Java e Spring Boot para o desenvolvimento.

# Pré-requisitos
- Java 21
- PostgreSQL rodando (não precisa de dados específicos)

# Rodando a API
1. Clonar o repositório
   

    git clone https://github.com/PabloHarrison/API-Estudos.git


2. Entrar na pasta do projeto

    
    cd DBEstudosAPI


3. Configurar o banco no arquivo `application.properties` (ou `.yml`):
   

    spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_banco

    spring.datasource.username=seu_usuario

    spring.datasource.password=sua_senha


4. Rodar a API
   

    ./mvnw spring-boot:run

## Endpoints
| Método | Endpoint        | Ação                                       |
|--------|-----------------|--------------------------------------------|
| GET    | /categorias     | Lista todas as categorias pelos parâmetros |
| POST   | /categorias     | Cria uma nova categoria                    |
| GET    | /registros      | Lista todos os registros pelos parâmetros  |
| POST   | /registros      | Cria um novo registro                      |
| GET    | /registros/{id} | Retorna um registro pelo ID                |

## Tecnologias usadas

- Java 21
- Spring Boot 3.5.9
- PostgreSQL