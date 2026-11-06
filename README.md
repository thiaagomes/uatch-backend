# UATCH Backend

API do marketplace brasileiro de relógios UATCH.

Responsável por API REST, contratos OpenAPI, regras de negócio,
persistência PostgreSQL, autenticação, autorização e integrações.
As funcionalidades serão implementadas gradualmente.

## Stack atual

- Java 21
- Spring Boot 4.1.2
- Maven Wrapper
- Spring Web MVC
- Bean Validation
- Spring Boot Actuator

## Executar localmente

Na pasta do repositório:

```bash
./mvnw spring-boot:run
```

Health check: http://localhost:8080/actuator/health

Para encerrar, pressione Control + C no terminal.

## Testes

```bash
./mvnw test
```

## PostgreSQL local — configuração preparada

O arquivo compose.yaml define PostgreSQL 18.6 com volume persistente
e acesso local pela porta 5432, configurável no arquivo .env.

Para preparar as configurações locais:

```bash
cp .env.example .env
```

Preencha POSTGRES_PASSWORD no .env antes de iniciar o banco.
Se o arquivo .env já existir, preserve suas configurações.

Com a máquina do Podman iniciada:

```bash
podman compose up -d postgres
```

O .env contém credenciais locais e não deve ser versionado.

### Pendência no ambiente corporativo

A inicialização do PostgreSQL ainda não foi validada: o download
da imagem foi bloqueado por falha na validação do certificado
emitido pelo Oracle Secure Web Gateway.

A aplicação ainda não possui integração com o banco.
O health check UP valida somente a aplicação nesta etapa.