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
- Endpoint POST /api/v1/brands.
- Validação da entrada do cadastro de marcas.
- Persistência de marcas no PostgreSQL.
- Migration V1 para criação da tabela brands.
- Restrição de unicidade do slug no banco.
- Tradução da violação de unicidade do slug para conflito de cadastro.
- Respostas estruturadas de erro com ProblemDetail no controller de marcas.
- Health check pelo Actuator.
- Banco separado para testes de integração.

Validação mais recente: 63 testes aprovados, sem falhas.

Ainda não implementado:

- Endpoint de criação de modelos de relógios.
- Persistência e endpoints de consulta de modelos de relógios.
- Documentação OpenAPI.
- Autenticação e autorização.
- Anúncios e demais funcionalidades do marketplace.
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
│   │   │       │   ├── exception/
│   │   │       │   │   └── BrandSlugAlreadyExistsException.java
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
│   │   │           ├── dto/
│   │   │           │   ├── BrandResponse.java
│   │   │           │   └── CreateBrandRequest.java
│   │   │           └── exception/
│   │   │               └── CatalogExceptionHandler.java
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
│       │       │   └── JpaBrandRepositoryAdapterTest.java
│       │       └── presentation/controller/
│       │           ├── BrandControllerTest.java
│       │           └── BrandControllerIntegrationTest.java
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

Contém os casos de uso, as exceções da aplicação e os contratos
necessários para executar as operações.

- CreateBrand valida a marca, consulta duplicidade de slug
  e solicita a gravação.
- CreateWatchModel valida o modelo, verifica a existência da marca,
  consulta duplicidade de referência por marca e solicita a gravação.
- ListBrands consulta e ordena as marcas pelo nome, ignorando
  diferenças entre maiúsculas e minúsculas, com desempate pelo slug.

As interfaces em port definem as operações de persistência
sem depender de uma tecnologia de banco.

BrandSlugAlreadyExistsException representa um conflito de cadastro.
Ela não conhece Spring, códigos HTTP ou detalhes do PostgreSQL.

O contrato WatchModelRepository já existe, mas sua implementação
de persistência ainda está pendente.

### Infraestrutura — infrastructure

Implementa os detalhes técnicos.

- BrandJpaEntity mapeia a tabela brands.
- SpringDataBrandRepository utiliza Spring Data JPA.
- JpaBrandRepositoryAdapter implementa o contrato BrandRepository
  e converte entre domínio e entidade JPA.
- CatalogConfiguration registra ListBrands e CreateBrand no Spring.

A gravação utiliza saveAndFlush para identificar violações
da restrição de unicidade durante a operação do adaptador.

Somente a combinação da restrição uk_brands_slug com o SQLSTATE
23505 é traduzida para BrandSlugAlreadyExistsException.
Outros erros de integridade são propagados sem essa tradução.

A causa técnica é preservada na exceção para diagnóstico.

As regras do domínio não ficam nas entidades JPA.

### Apresentação — presentation

Expõe a interface HTTP.

- BrandController recebe as requisições de listagem e cadastro.
- CreateBrandRequest define a entrada e suas validações.
- BrandResponse define o JSON retornado ao cliente.
- CatalogExceptionHandler traduz conflitos de cadastro para HTTP 409
  e utiliza o tratamento MVC de erros com ProblemDetail.

O controller delega as operações aos casos de uso e não contém
regras de negócio.

A pasta application/exception representa erros da aplicação.
A pasta presentation/exception contém sua tradução para HTTP.

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

### Fluxo do cadastro

```text
POST /api/v1/brands
    → Validação de CreateBrandRequest
    → BrandController
    → CreateBrand
    → Validação de Brand e consulta do slug
    → BrandRepository
    → JpaBrandRepositoryAdapter
    → PostgreSQL
    → BrandResponse com HTTP 201
```

O identificador UUID é gerado pelo backend.

A consulta prévia de slug permite detectar um cadastro existente.
A restrição única no PostgreSQL também protege a gravação quando
duas requisições passam pela consulta antes de qualquer inserção.

O conflito detectado pelo caso de uso ou pela restrição do banco
é representado pela mesma exceção e traduzido para HTTP 409.

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

## Endpoints de marcas

### Listar marcas

```bash
curl -i http://localhost:8080/api/v1/brands
```

Retorna HTTP 200 e uma lista JSON com id, name e slug.
Se não houver marcas, retorna [].

A listagem é ordenada pelo nome, sem diferenciar maiúsculas
e minúsculas, com desempate pelo slug.

### Cadastrar marca

O comando abaixo grava uma marca no banco local da aplicação:

```bash
curl -i -X POST http://localhost:8080/api/v1/brands \
  -H 'Content-Type: application/json' \
  -d '{"name":"Rolex","slug":"rolex"}'
```

Entrada:

- name: obrigatório e não pode conter apenas espaços.
- slug: obrigatório; aceita letras minúsculas, números e hífens
  simples entre os segmentos, como tag-heuer.

O nome é normalizado removendo espaços das extremidades.
O slug deve ser enviado no formato válido, sem espaços adicionais.

Em caso de sucesso, retorna HTTP 201 com id, name e slug.

O envio repetido de um slug já cadastrado retorna HTTP 409.

### Respostas HTTP

| Operação ou situação | Status |
|---|---|
| Listagem de marcas | 200 OK |
| Cadastro realizado | 201 Created |
| Entrada inválida | 400 Bad Request |
| Slug já cadastrado | 409 Conflict |

Os erros tratados utilizam application/problem+json.

Exemplo de resposta para slug duplicado:

```json
{
  "type": "about:blank",
  "title": "Brand slug conflict",
  "status": 409,
  "detail": "Brand slug already exists",
  "instance": "/api/v1/brands"
}
```

A resposta não expõe a causa técnica, nomes de restrições ou SQL.

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

Para remover os arquivos compilados antes de executar:

```bash
./mvnw clean test
```

Validação mais recente: 63 testes aprovados, sem falhas.

A suíte inclui:

- Regras das entidades de domínio.
- Casos de uso com repositórios simulados.
- Contrato HTTP do controller com MockMvc e casos de uso simulados.
- Respostas 201, 400 e 409.
- Inicialização do contexto Spring no perfil test.
- Gravação e consulta reais no PostgreSQL.
- Tradução da violação de unicidade do slug.
- Integração do cadastro e da listagem HTTP com o banco.

BrandControllerTest contém os testes isolados do controller.

BrandControllerIntegrationTest carrega a aplicação e utiliza
o repositório real no banco uatch_test.

Os testes do adaptador JPA e de integração do controller utilizam
transações revertidas automaticamente ao final de cada teste.

O teste de duplicidade pode registrar uma violação de chave única
no log. Essa mensagem é esperada quando o teste termina aprovado.

A violação da restrição e o conflito de um cadastro já existente
são testados. Ainda não há um teste com requisições concorrentes.

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

Na raiz do repositório, confira as alterações antes de selecioná-las:

```bash
git status --short
```

Quando todas pertencerem à mesma entrega:

```bash
git add .
```

Revise o resumo do que será registrado:

```bash
git diff --cached --stat
```

Para revisar o conteúdo completo preparado para o commit:

```bash
git diff --cached
```

Depois, crie o commit com uma mensagem descritiva e publique
a branch para abrir o Pull Request.