# Eventos

Este é um projeto de aplicação web para gerenciamento de eventos, desenvolvido como parte do curso de Análise e Desenvolvimento de Sistemas (ADS) - Web 2.

## Descrição

A aplicação "Eventos" é uma plataforma web construída com Spring Boot que permite o gerenciamento de eventos. Utiliza PostgreSQL como banco de dados, Thymeleaf para templates de interface, e inclui autenticação e autorização com Spring Security.

## Tecnologias Utilizadas

- **Java 25**
- **Spring Boot 4.0.0-SNAPSHOT**
- **Spring Data JPA** para persistência de dados
- **Spring Security** para autenticação e autorização
- **Thymeleaf** para templates HTML
- **PostgreSQL** como banco de dados
- **Lombok** para redução de boilerplate
- **Maven** para gerenciamento de dependências
- **Docker** para containerização do banco de dados

## Pré-requisitos

- Java 25 ou superior
- Maven 3.6+
- Docker e Docker Compose (para executar o banco de dados)

## Como Executar

1. **Clone o repositório** (se aplicável) e navegue até a pasta do projeto.

2. **Inicie o banco de dados PostgreSQL** usando Docker Compose:

   ```bash
   docker-compose up -d
   ```

3. **Execute a aplicação** com Maven:

   ```bash
   ./mvnw spring-boot:run
   ```

   Ou, se estiver no Windows:

   ```bash
   mvnw.cmd spring-boot:run
   ```

4. **Acesse a aplicação** no navegador em `http://localhost:8080`.

## Configuração

A configuração da aplicação está localizada em `src/main/resources/application.properties`. As principais configurações incluem:

- Porta do servidor: 8080
- URL do banco de dados PostgreSQL: `jdbc:postgresql://localhost:5432/plataforma_eventos`
- Credenciais do banco: usuário `user`, senha `password`
- JPA: Hibernate com DDL auto-update e logs SQL habilitados

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── com/web/eventos/
│   │       ├── EventosApplication.java          # Classe principal
│   │       ├── config/                          # Configurações da aplicação
│   │       ├── controllers/                     # Controladores REST/Web
│   │       ├── dtos/                            # Objetos de Transferência de Dados
│   │       ├── entities/                        # Entidades JPA
│   │       ├── repositories/                    # Repositórios de dados
│   │       └── services/                        # Lógica de negócio
│   └── resources/
│       ├── application.properties               # Configurações
│       ├── static/                              # Arquivos estáticos (CSS, JS, imagens)
│       └── templates/                           # Templates Thymeleaf
└── test/
    └── java/
        └── com/web/eventos/
            └── EventosApplicationTests.java     # Testes unitários
```

## Desenvolvimento

Para desenvolvimento, o Spring Boot DevTools está configurado para recarregamento automático.

### Executando Testes

```bash
./mvnw test
```

## Contribuição

Este é um projeto educacional. Para contribuições, siga as melhores práticas de desenvolvimento Java e Spring Boot.

## Licença

Este projeto é para fins educacionais e não possui licença específica.
