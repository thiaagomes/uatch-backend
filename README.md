# UATCH Backend

API do marketplace brasileiro de relógios UATCH.

Responsável pelas regras de negócio, API REST, persistência,
autenticação, autorização e integrações do produto.
As funcionalidades serão implementadas gradualmente.

## Stack

- Java 21
- Spring Boot 4.1.2
- Maven Wrapper
- Spring Web MVC
- Bean Validation
- Spring Boot Actuator
- Spring Data JPA e Hibernate
- PostgreSQL 18.6
- Flyway
- JUnit e Mockito
- Podman e Compose para desenvolvimento local

As versões das dependências são gerenciadas pelo Spring Boot
e pelo Maven Wrapper.

## Estado atual

Implementado:

- Entidades de domínio Brand e WatchModel.
- Casos de uso de criação de marcas e modelos de relógios.
- Caso de uso de listagem de marcas, ordenada por nome.
- Endpoint GET /api/v1/brands.
- Persistência de marcas no PostgreSQL.
- Migration V1 para criação da tabela brands.
- Restrição de unicidade do slug no banco.
- Health check pelo Actuator.
- Banco separado para testes de integração.

Validação mais recente: 57 testes aprovados, sem falhas.

Ainda não implementado:

- Endpoints de criação de marcas e modelos.
- Persistência e endpoints de consulta de modelos de relógios.
- Tratamento padronizado de erros da API.
- Documentação OpenAPI.
- Autenticação, anúncios e demais funcionalidades do marketplace.
- Implantação na OCI.

## Arquitetura

O backend segue uma arquitetura de monólito modular, organizado
por domínios de negócio.

O primeiro módulo é catalog. Dentro dele, o código está separado
em domínio, aplicação, infraestrutura e apresentação.

Os módulos serão criados conforme as funcionalidades evoluírem.
Não utilizamos microsserviços nesta etapa.

### Estrutura do projeto

```text
uatch-backend/
├── .mvn/wrapper/
├── compose.yaml
├── pom.xml
├── mvnw
├── mvnw.cmd
├── .env.example
├── src/
│   ├── main/
│   │   ├── java/com/uatch/
│   │   │   ├── UatchBackendApplication.java
│   │   │   └── catalog/
│   │   │       ├── domain/
│   │   │       │   ├── Brand.java
│   │   │       │   └── WatchModel.java
│   │   │       ├── application/
│   │   │       │   ├── port/
│   │   │       │   │   ├── BrandRepository.java
│   │   │       │   │   └── WatchModelRepository.java
│   │   │       │   └── usecase/
│   │   │       │       ├── CreateBrand.java
│   │   │       │       ├── CreateWatchModel.java
│   │   │       │       └── ListBrands.java
│   │   │       ├── infrastructure/
│   │   │       │   ├── configuration/
│   │   │       │   │   └── CatalogConfiguration.java
│   │   │       │   └── persistence/
│   │   │       │       ├── entity/
│   │   │       │       │   └── BrandJpaEntity.java
│   │   │       │       └── repository/
│   │   │       │           ├── SpringDataBrandRepository.java
│   │   │       │           └── JpaBrandRepositoryAdapter.java
│   │   │       └── presentation/
│   │   │           ├── controller/
│   │   │           │   └── BrandController.java
│   │   │           └── dto/
│   │   │               └── BrandResponse.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/
│   │           └── V1__create_brands.sql
│   └── test/
│       ├── java/com/uatch/
│       │   ├── UatchBackendApplicationTests.java
│       │   └── catalog/
│       │       ├── domain/
│       │       ├── application/usecase/
│       │       ├── infrastructure/persistence/repository/
│       │       └── presentation/controller/
│       └── resources/
│           └── application-test.yaml
└── README.md
```

A pasta target/ é gerada pelo Maven, ignorada pelo Git e não deve
ser editada manualmente.

### Domínio — domain

Contém as entidades e suas regras de validade.

Brand representa uma marca do catálogo.
WatchModel representa um modelo de relógio associado a uma marca.

O domínio não depende de Spring, JPA ou PostgreSQL.

Um modelo do catálogo não é um anúncio: preço, vendedor, condição
e fotografias de um exemplar pertencerão ao domínio de anúncios.

### Aplicação — application

Contém os casos de uso e os contratos necessários para executá-los.

Os casos de uso coordenam as operações:

- CreateBrand valida a marca, consulta duplicidade de slug
  e solicita a gravação.
- CreateWatchModel valida o modelo, verifica a existência da marca,
  consulta duplicidade de referência por marca e solicita a gravação.
- ListBrands consulta e ordena as marcas.

As interfaces em port definem as operações de persistência
sem depender de uma tecnologia de banco.

O contrato WatchModelRepository já existe, mas sua implementação
de persistência ainda está pendente.

### Infraestrutura — infrastructure

Implementa os detalhes técnicos.

- BrandJpaEntity mapeia a tabela brands.
- SpringDataBrandRepository utiliza Spring Data JPA.
- JpaBrandRepositoryAdapter implementa o contrato BrandRepository
  e converte entre domínio e entidade JPA.
- CatalogConfiguration registra o caso de uso ListBrands no Spring.

As regras do domínio não ficam nas entidades JPA.

### Apresentação — presentation

Expõe a interface HTTP.

- BrandController recebe as requisições de listagem.
- BrandResponse define o JSON retornado ao cliente.

O controller delega a operação ao caso de uso e não contém
regras de negócio.

### Fluxo da listagem

```text
GET /api/v1/brands
    → BrandController
    → ListBrands
    → BrandRepository (contrato)
    → JpaBrandRepositoryAdapter (implementação)
    → SpringDataBrandRepository
    → PostgreSQL
```

Na resposta, os registros são convertidos para o domínio,
ordenados pelo caso de uso e transformados em BrandResponse.

### Evolução do banco

O Flyway é responsável pela criação e evolução das tabelas.

As migrations ficam em src/main/resources/db/migration e seguem
o formato V1__descricao.sql, com dois underscores após a versão.

Depois de aplicada e compartilhada, uma migration não deve ser
reescrita. Alterações posteriores devem usar uma nova migration.

O Hibernate está configurado com ddl-auto: validate:
ele verifica o mapeamento, mas não cria nem altera tabelas.

## Pré-requisitos locais

- Java 21.
- Podman com máquina virtual configurada no macOS.
- Provedor Compose disponível.
- Porta 5432 disponível para PostgreSQL.
- Porta 8080 disponível para a API.

Não é necessário instalar Maven globalmente: utilize ./mvnw.

## Configuração local

Execute os comandos a partir da raiz deste repositório.

Se ainda não existir um arquivo .env:

```bash
cp .env.example .env
```

Se o .env já existir, preserve seu conteúdo.

Preencha POSTGRES_PASSWORD com uma senha exclusiva para o ambiente
local. A configuração padrão utiliza:

- Banco da aplicação: uatch.
- Usuário: uatch.
- Porta: 5432.
- Host utilizado pelo backend: localhost.

O Spring importa o .env como arquivo de propriedades.
Mantenha o formato CHAVE=valor, sem o prefixo export.
Para a configuração inicial, utilize senha com letras e números,
sem aspas.

O .env não deve ser versionado. O .env.example deve permanecer
sem senha preenchida.

## Iniciar o PostgreSQL

Se a máquina do Podman estiver parada:

```bash
podman machine start podman-machine-default
```

Inicie o banco:

```bash
podman compose up -d postgres
```

Confira o estado:

```bash
podman ps --filter name=uatch-backend_postgres_1
```

O estado esperado é healthy.

O Compose publica o banco apenas em 127.0.0.1.
Os dados ficam em volume persistente.

O download da imagem foi validado após a troca de rede.
No ambiente corporativo, houve falha de validação de certificado
emitido pelo Oracle Secure Web Gateway. Se o problema reaparecer,
verifique o ambiente de rede e certificados, preservando
a validação HTTPS.

## Executar a API

Com o PostgreSQL ligado e o .env configurado:

```bash
./mvnw spring-boot:run
```

Na inicialização, o Flyway aplica as migrations pendentes
ao banco da aplicação.

O terminal permanece ocupado enquanto a aplicação estiver rodando.
Para encerrá-la, pressione Control + C.

Em outro terminal, consulte o health check:

```bash
curl -i http://localhost:8080/actuator/health
```

Consulte as marcas:

```bash
curl -i http://localhost:8080/api/v1/brands
```

O endpoint retorna HTTP 200 e uma lista JSON com id, name e slug.
Se não houver marcas, retorna [].

## Preparar o banco de testes

Os testes que iniciam o Spring utilizam o perfil test e o banco
separado uatch_test.

Com a configuração padrão de usuário, crie esse banco uma única vez:

```bash
podman exec -it uatch-backend_postgres_1 psql -h 127.0.0.1 -U uatch -d postgres -W -c "CREATE DATABASE uatch_test;"
```

Digite a senha local quando solicitado.
Se o banco já existir, não é necessário recriá-lo.

O perfil de teste reaproveita as credenciais da configuração
principal e aponta para uatch_test. O Flyway prepara suas tabelas.

Não configure esse perfil para apontar a um banco de produção.

## Executar os testes

Mantenha o PostgreSQL ligado e o banco uatch_test preparado:

```bash
./mvnw test
```

A suíte inclui:

- Regras das entidades de domínio.
- Casos de uso com repositórios simulados.
- Contrato HTTP do controller com MockMvc.
- Inicialização do contexto Spring no perfil test.
- Gravação e consulta reais no PostgreSQL.
- Rejeição de slug duplicado pela restrição do banco.

Os testes do adaptador JPA utilizam transações revertidas
automaticamente ao final de cada teste.

O teste de duplicidade pode registrar uma violação de chave única
no log. Essa mensagem é esperada quando o teste termina aprovado.

## Encerrar o ambiente

Pare o backend com Control + C no terminal onde ele está rodando.

Pare o PostgreSQL:

```bash
podman compose stop postgres
```

Se não houver outros trabalhos utilizando a VM, desligue-a:

```bash
podman machine stop podman-machine-default
```

Esses comandos preservam os dados.
Não utilize down -v quando precisar manter o volume do banco.

## Git

- Desenvolver alterações em branches próprias.
- Utilizar commits pequenos com Conventional Commits.
- Revisar e integrar alterações por Pull Request.
- Atualizar a main local após o merge.
- Nunca versionar senhas, tokens ou credenciais.