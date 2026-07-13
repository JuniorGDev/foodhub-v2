# FoodHub

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.16-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-red)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%202.8.16-purple)
![Docker](https://img.shields.io/badge/Docker-Compose-blue)

REST API for restaurant management system, built with Java 21 and Spring Boot 3 following Clean Architecture principles.

## Sobre o projeto

FoodHub é um sistema de gerenciamento de restaurantes que permite:

- Cadastro e gerenciamento de usuários com diferentes perfis (ADMIN, OWNER, CUSTOMER)
- Registro de restaurantes associados a proprietários do tipo OWNER
- Classificação de restaurantes por tipos de cozinha (brasileira, italiana, japonesa, etc.)
- Gerenciamento de cardápios com itens e preços
- Controle de horários de funcionamento dos restaurantes

**Entidades principais:**

- **User**: Usuários do sistema com tipos diferenciados
- **UserType**: Tipos de usuário (ADMIN, OWNER, CUSTOMER)
- **Restaurant**: Restaurantes associados a proprietários e tipos de cozinha
- **KitchenType**: Categorias de cozinha (BRAZILIAN, ITALIAN, JAPANESE)
- **MenuItem**: Itens do cardápio de cada restaurante

## Arquitetura

O projeto segue uma arquitetura inspirada em Clean Architecture, organizando o código em camadas bem definidas:

```
application/
├── dto/           # Data Transfer Objects
├── mapper/        # Conversão entre DTOs e entidades
└── service/       # Regras de negócio

domain/
├── exception/     # Exceções de domínio
└── model/         # Entidades do domínio

infrastructure/
├── config/        # Configurações (OpenAPI, Exception Handler)
└── repository/    # Repositórios JPA

presentation/
├── controller/    # Endpoints REST
├── request/       # Objetos de requisição
└── response/      # Objetos de resposta

shared/
└── constants/     # Constantes compartilhadas
```

**Responsabilidades:**

- **application**: Orquestra casos de uso e coordena fluxos
- **domain**: Contém regras de negócio e entidades do domínio
- **infrastructure**: Implementa persistência e configurações técnicas
- **presentation**: Expõe APIs REST e trata requisições HTTP
- **shared**: Contém elementos utilitários compartilhados

## Tecnologias utilizadas

| Tecnologia | Versão |
|------------|--------|
| Java | 21 |
| Spring Boot | 3.5.16 |
| Spring Data JPA | 3.5.16 |
| PostgreSQL | 15 |
| Maven | 3.9.9 |
| SpringDoc OpenAPI | 2.8.16 |
| Bean Validation | 3.0.2 |
| Lombok | - |
| Testcontainers | 1.20.4 |
| JUnit 5 | - |
| Mockito | - |

## Funcionalidades

✔ Cadastro de usuários
✔ Cadastro de tipos de usuário
✔ Cadastro de tipos de cozinha
✔ Cadastro de restaurantes
✔ Cadastro de itens do cardápio
✔ Atualização de todas as entidades
✔ Exclusão de todas as entidades
✔ Consulta por ID e listagem geral
✔ Validação de dados com Bean Validation
✔ Documentação automática com Swagger/OpenAPI

## Regras de negócio

- **Proprietário de restaurante**: Apenas usuários com tipo OWNER podem possuir restaurantes
- **Email único**: O email do usuário deve ser único no sistema
- **Normalização de email**: Emails são automaticamente convertidos para lowercase
- **Nome de UserType**: Nomes de tipos de usuário são normalizados para UPPERCASE e não podem ser duplicados
- **Nome de KitchenType**: Nomes de tipos de cozinha são normalizados para lowercase e não podem ser duplicados
- **Preço do MenuItem**: O preço deve ser maior que zero
- **Horário de funcionamento**: O horário de abertura deve ser anterior ao horário de fechamento
- **Campos obrigatórios**: Todos os campos obrigatórios são validados via Bean Validation

## Estrutura do Projeto

```
foodhub/
├── src/
│   ├── main/
│   │   ├── java/br/com/foodhub/
│   │   │   ├── application/
│   │   │   ├── domain/
│   │   │   ├── infrastructure/
│   │   │   ├── presentation/
│   │   │   └── shared/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── seed_data.sql
│   └── test/
│       └── java/br/com/foodhub/
│           ├── application/
│           ├── domain/
│           ├── integration/
│           └── presentation/
├── .env
├── docker-compose.yml
├── Dockerfile
├── pom.xml
└── mvnw
```

## Banco de Dados

O projeto utiliza PostgreSQL como banco de dados relacional.

**Configuração padrão:**

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/foodhub
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Propriedades configuráveis via variáveis de ambiente:**

- `DB_HOST`: Host do banco (padrão: localhost)
- `DB_PORT`: Porta do banco (padrão: 5432)
- `DB_NAME`: Nome do banco (padrão: foodhub)
- `DB_USER`: Usuário do banco (padrão: postgres)
- `DB_PASSWORD`: Senha do banco (padrão: postgres)

## Seed Data

O arquivo `src/main/resources/seed_data.sql` popula o banco com dados iniciais:

**User Types criados:**
- ADMIN
- OWNER
- CUSTOMER

**Kitchen Types criados:**
- BRAZILIAN
- ITALIAN
- JAPANESE

**Usuários de exemplo:**
- admin@foodhub.com (ADMIN)
- owner@foodhub.com (OWNER)
- customer@foodhub.com (CUSTOMER)

**Senha padrão:** `123456`

**Restaurantes de exemplo:**
- Bella Napoli (cozinha italiana)
- Sushi House (cozinha japonesa)

**Itens do cardápio de exemplo:**
- Pizza Margherita
- Sushi Combo

O script é executado automaticamente na inicialização da aplicação.

## Executando o Projeto

**Pré-requisitos:**
- Java 21 instalado
- PostgreSQL 15 rodando
- Maven 3.9+

**Passos:**

1. Clone o repositório:
```bash
git clone <repository-url>
cd foodhub
```

2. Configure o banco de dados PostgreSQL ou utilize o Docker Compose (veja seção Docker)

3. Altere as configurações em `application.properties` se necessário

4. Execute o projeto:
```bash
./mvnw spring-boot:run
```

Ou compile e execute:
```bash
./mvnw clean package
java -jar target/foodhub-0.0.1-SNAPSHOT.jar
```

A API estará disponível em `http://localhost:8080`

## Docker

Para subir a aplicação com PostgreSQL usando Docker Compose:

1. Configure as variáveis de ambiente no arquivo `.env`:
```env
DB_HOST=postgres
DB_PORT=5432
DB_NAME=foodhub
DB_USER=postgres
DB_PASSWORD=postgres
```

2. Execute o Docker Compose:
```bash
docker-compose up -d
```

3. Acesse a API em `http://localhost:8080`

Para parar os containers:
```bash
docker-compose down
```

## Swagger

A documentação interativa da API está disponível via Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

A especificação OpenAPI em JSON:
```
http://localhost:8080/v3/api-docs
```

## Exemplos de Requisição

**Criar usuário:**
```bash
POST /api/v1/users
Content-Type: application/json

{
  "name": "Geová Junior",
  "email": "junior@foodhub.com",
  "password": "StrongPassword123",
  "address": "123 Main Street, São Paulo - SP",
  "userTypeId": "22222222-2222-2222-2222-222222222222"
}
```

**Criar restaurante:**
```bash
POST /api/v1/restaurants
Content-Type: application/json

{
  "name": "Pizzaria Napoli",
  "kitchenTypeId": "55555555-5555-5555-5555-555555555555",
  "address": "Av. Paulista, 1000 - São Paulo/SP",
  "openingTime": "08:00",
  "closingTime": "22:00",
  "ownerId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"
}
```

**Criar menu item:**
```bash
POST /api/v1/menu-items
Content-Type: application/json

{
  "name": "Cheeseburger",
  "description": "Beef burger with cheddar cheese, lettuce and tomato",
  "price": 29.90,
  "availableOnlyInRestaurant": true,
  "imagePath": "/images/menu-items/cheeseburger.png",
  "restaurantId": "dddddddd-dddd-dddd-dddd-dddddddddddd"
}
```

## Testes

O projeto possui uma suíte completa de testes organizados em três categorias:

**Testes Unitários:**
- Testes de domínio (`domain/model/`)
- Testes de serviço (`application/service/`)
- Testes isolados com Mockito

**Testes de Controller:**
- Testes dos endpoints REST (`presentation/controller/`)
- Validação de requisições e respostas
- Uso de MockMvc

**Testes de Integração:**
- Testes end-to-end com banco de dados real (`integration/`)
- Uso de Testcontainers para PostgreSQL
- Validação de fluxos completos

**Executar todos os testes:**
```bash
./mvnw clean test
```

**Executar apenas testes unitários:**
```bash
./mvnw test -Dtest=*Test
```

**Executar apenas testes de integração:**
```bash
./mvnw test -Dtest=*IntegrationTest
```

## Autores

- **Junior Geová**  
  GitHub: https://github.com/JuniorGDev

- **Wesley Batista**  
  GitHub: https://github.com/wesleysbdev
