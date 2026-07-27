# Plataforma Metaro

A **Plataforma Metaro** centraliza processos internos e ferramentas utilizadas
pelos diferentes setores da empresa. O repositório reúne a API, a interface web
e o aplicativo Android da solução.

<p align="center">
  <img width="100%" alt="Tela principal da Plataforma Metaro" src="https://github.com/user-attachments/assets/58df6278-9c77-47b2-bc77-349f601fe04f">
</p>

## Componentes

| Pasta | Aplicação | Tecnologias principais |
|---|---|---|
| [`backend`](backend) | API REST e serviços de negócio | Java 21, Spring Boot 3.4, Spring Security, JPA, Flyway, PostgreSQL, H2 e Oracle |
| [`frontend`](frontend) | Portal web responsivo | Angular 21, CoreUI 5, Angular Material, RxJS e SCSS |
| [`mobile`](mobile) | Aplicativo Android nativo | Kotlin, Android SDK 36 e WebView |

```text
portal-demo/
├── backend/    # API Spring Boot
├── frontend/   # SPA Angular
└── mobile/     # Aplicativo Android
```

O frontend e o aplicativo Android consomem a mesma API. O aplicativo móvel
encapsula o portal em uma WebView e oferece integrações nativas para arquivos,
PDFs, câmera e links externos.

## Funcionalidades

- Autenticação por JWT e autorização baseada em perfis.
- Mural eletrônico para comunicados e avisos internos.
- Dashboards com indicadores operacionais e estratégicos.
- Gerenciamento e compartilhamento de arquivos.
- Calendário corporativo e agendamento de salas.
- Configurador de produtos e apoio a propostas comerciais.
- Checklists de inspeção, conformidade e auditoria.
- Gerenciamento de tarefas, agendas e atividades.
- Integração com PostgreSQL e com o ERP em Oracle.

## Pré-requisitos

- JDK 21 para o backend.
- Node.js `20.19+`, `22.12+` ou `24+` e npm 10+ para o frontend.
- Android Studio, JDK 17+ e Android SDK 36 para o aplicativo móvel.
- Acesso à rede corporativa ou VPN para funcionalidades que consultam o ERP.

## Execução local

### 1. Backend

No Windows:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

No Linux ou macOS:

```bash
cd backend
./mvnw spring-boot:run
```

Por padrão, a aplicação usa o perfil `dev`:

- API: <http://localhost:8080>
- banco principal: H2 em memória;
- console H2: <http://localhost:8080/h2-console>
- URL JDBC do H2: `jdbc:h2:mem:portal`
- usuário do H2: `sa`
- senha do H2: vazia.

A integração com o ERP usa um segundo `DataSource`. Seus valores de
desenvolvimento ficam em `backend/src/main/resources/application-dev.properties`
e podem ser substituídos pelas variáveis:

```text
EXTERNAL_DATASOURCE_JDBC_URL
EXTERNAL_DATASOURCE_USERNAME
EXTERNAL_DATASOURCE_PASSWORD
EXTERNAL_DATASOURCE_DRIVER_CLASS_NAME
```

Reinicie a aplicação após alterar qualquer configuração de banco de dados.

### 2. Frontend

```powershell
cd frontend
npm install
npm start
```

O portal estará disponível em <http://localhost:4200> e, no ambiente de
desenvolvimento, consumirá a API em <http://localhost:8080>.

### 3. Aplicativo Android

1. Abra a pasta `mobile` no Android Studio.
2. Aguarde a sincronização do Gradle.
3. Selecione uma variante em **Build Variants**:
   - `homologacaoDebug` para testes;
   - `producaoRelease` para distribuição.
4. Conecte um dispositivo com depuração USB ou use um emulador.
5. Execute o módulo `app`.

Consulte o [`mobile/README.md`](mobile/README.md) para detalhes sobre WebView,
abertura de anexos, assinatura do APK e checklist de homologação.

## Documentação da API

Com o backend em execução:

- Swagger UI: <http://localhost:8080/swagger-ui/index.html>
- documento OpenAPI em JSON: <http://localhost:8080/v3/api-docs>

No Swagger, use o botão **Authorize** e informe o token JWT. Os endpoints estão
organizados nos grupos `core`, `modules` e `utilities`.

## Banco de dados e Flyway

O Flyway é o responsável por criar e evoluir o esquema:

```text
backend/src/main/resources/db/
├── migration/  # Migrações aplicadas em todos os ambientes
└── devdata/    # Dados de demonstração exclusivos do perfil dev
```

As migrações seguem o padrão:

```text
V<versão>__<descrição>.sql
```

Exemplo: `V2__create_audit_table.sql`.

O Hibernate não altera o banco em produção: `ddl-auto=validate` apenas verifica
se as entidades correspondem ao esquema. Em um banco preexistente, a primeira
execução pode usar `FLYWAY_BASELINE_ON_MIGRATE=true`; depois da criação do
histórico do Flyway, altere para `false`.

## Organização do backend

O backend adota organização por funcionalidade. Cada módulo concentra suas
camadas no mesmo pacote:

```text
br.com.metaro.portal
├── config
├── core
├── integration
│   └── bunny
├── modules
│   └── general
│       └── memorando
│           ├── controller
│           ├── dto
│           ├── entity
│           ├── repository
│           ├── service
│           └── util
└── util
    ├── picture
    └── video
```

Controllers tratam o contrato HTTP, services concentram os casos de uso,
repositories cuidam da persistência e DTOs representam entradas e saídas. Os
métodos devem expressar intenção, evitando nomes genéricos.

Clientes de serviços externos ficam em `integration`; entidades e serviços
reutilizáveis, como `Picture` e `Video`, ficam em `util`. Os módulos de negócio
apenas associam esses recursos aos próprios agregados e aplicam suas regras.

Mais detalhes estão em [`backend/ARCHITECTURE.md`](backend/ARCHITECTURE.md).

## Configuração do backend

As configurações sensíveis ou específicas de ambiente podem ser fornecidas por
variáveis:

| Variável | Finalidade | Padrão de desenvolvimento |
|---|---|---|
| `APP_PROFILE` | Perfil Spring ativo | `dev` |
| `CLIENT_ID` | Identificador OAuth2 | `myclientid` |
| `CLIENT_SECRET` | Segredo OAuth2 | `myclientsecret` |
| `JWT_DURATION` | Duração do token em segundos | `86400` |
| `CORS_ORIGINS` | Origens permitidas pelo CORS | `http://localhost:4200` |
| `IMAGE_PATH` | Diretório de imagens | Configuração local |
| `FLYWAY_BASELINE_ON_MIGRATE` | Adoção de banco preexistente | `true` |
| `BUNNY_STREAM_API_KEY` | Chave da API do Bunny Stream | Sem valor |
| `BUNNY_STREAM_LIBRARY_ID` | Biblioteca de vídeos do Bunny | Sem valor |
| `BUNNY_STREAM_EMBED_BASE_URL` | URL-base do player | Player oficial |

Use variáveis de ambiente ou um cofre de segredos em produção. Não adicione
senhas, tokens, chaves de API ou certificados ao repositório.

## Testes e builds

Backend:

```powershell
cd backend
.\mvnw.cmd test
```

Frontend:

```powershell
cd frontend
npm test
npm run build
```

Android no Windows:

```powershell
cd mobile
.\gradlew.bat assembleHomologacaoDebug
```

O APK de produção deve ser assinado com a chave permanente da empresa. Não
armazene o arquivo `.jks` nem sua senha no repositório.

## Segurança

- Tokens JWT concedem acesso enquanto estiverem válidos; não os compartilhe.
- O acesso ao ERP deve ficar restrito à rede corporativa ou VPN.
- O aplicativo Android ainda permite HTTP apenas para os domínios explicitamente
  configurados. A adoção de HTTPS é recomendada antes da distribuição externa.
- Segredos do frontend ficam acessíveis ao navegador e não devem ser tratados
  como credenciais confidenciais.

## Contribuições

Este projeto é de uso interno da Metaro. Mudanças devem ser revisadas por
desenvolvedores autorizados e acompanhar as convenções descritas neste arquivo e
na documentação de arquitetura.

## Licença

Projeto privado. Todos os direitos reservados à Metaro.
