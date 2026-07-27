# Organizacao da aplicacao

O projeto segue uma organizacao **por funcionalidade**. Cada modulo de negocio
deve concentrar suas camadas no mesmo pacote:

```text
br.com.metaro.portal
|-- config
|-- integration
|   `-- bunny
|-- core
|   |-- controller
|   |-- dto
|   |-- entities
|   |-- repositories
|   `-- services
|-- modules
|   `-- general
|       `-- memorando
|           |-- controller
|           |-- dto
|           |-- entity
|           |-- repository
|           |-- service
|           `-- util
`-- util
    |-- erp
    |-- picture
    `-- video
```

## Convencoes

- Pacotes usam letras minusculas e nomes no singular.
- Controllers apenas traduzem HTTP para chamadas da camada de servico.
- Services concentram casos de uso e limites transacionais.
- Repositories tratam exclusivamente da persistencia.
- DTOs representam contratos de entrada e saida; entidades JPA nao devem ser
  expostas diretamente pela API.
- Objetos de transferencia sao classes DTO; a aplicacao nao utiliza a palavra-chave record.
- Metodos descrevem a intencao: `listMemorandos`, `getMemorando`,
  `createMemorando` e `returnMemorandoToDraft`, em vez de nomes genericos ou
  ambiguos.

O modulo `memorando` foi normalizado como referencia para a migracao gradual
dos demais modulos. Essa abordagem evita uma alteracao massiva de pacotes sem
ganho funcional imediato.

## Integracoes e recursos compartilhados

- `integration/bunny` contem apenas o cliente HTTP e as propriedades do Bunny
  Stream. Esse pacote nao conhece modulos de negocio.
- `util/video` concentra entidade, repositorio, ciclo de vida e DTOs
  reutilizaveis de video.
- Modulos como `stepFlow` associam um `Video` ao proprio agregado e aplicam
  suas regras de autorizacao. A entidade compartilhada nao referencia classes
  dos modulos consumidores.
- `util/picture` cumpre o mesmo papel para imagens e deve ser evoluido
  gradualmente para reduzir referencias reversas aos modulos.
- `util/erp` e a unica camada que conhece o segundo `DataSource`, SQL do
  Oracle e DTOs retornados pelo ERP. `stepFlow` mantem apenas a regra local de
  saldo produzido; `memorando` consome a consulta compartilhada de linhas do
  pedido.

## E-mail

O perfil `dev` usa `localhost:1025` por padrao e espera um capturador SMTP
local. Para enviar mensagens reais, configure as variaveis abaixo antes de
iniciar a aplicacao:

- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME` e `MAIL_PASSWORD`.
- `MAIL_SMTP_AUTH`, `MAIL_SMTP_SSL` e `MAIL_SMTP_STARTTLS`.
- `MAIL_CONNECTION_TIMEOUT`, `MAIL_READ_TIMEOUT` e `MAIL_WRITE_TIMEOUT`.

Falhas sao registradas por destinatario e nao interrompem as notificacoes dos
demais usuarios envolvidos no memorando.

## Banco de dados

O Flyway e o unico responsavel por alterar o esquema. Em producao, o Hibernate
usa `ddl-auto=validate` e falha cedo quando as entidades divergem do banco. No
perfil `dev`, a validacao fica desabilitada porque o H2 reporta tipos temporais
de forma diferente do PostgreSQL; mesmo assim, o Hibernate nunca cria tabelas.

- `db/migration`: migracoes de esquema aplicadas em todos os ambientes.
- `db/devdata`: dados de demonstracao aplicados apenas com o perfil `dev`.

Para um banco ja existente, a primeira inicializacao usa
`FLYWAY_BASELINE_ON_MIGRATE=true`. Depois que a tabela de historico for criada,
configure essa variavel como `false`.

## OpenAPI

- Swagger UI: `/swagger-ui.html`
- Documento OpenAPI: `/v3/api-docs`
- Grupos: `core`, `modules` e `utilities`

Use o botao **Authorize** do Swagger UI para informar um token JWT.
