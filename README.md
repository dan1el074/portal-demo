# Plataforma Metaro

A **Plataforma Metaro** é uma solução web corporativa desenvolvida para centralizar processos internos e ferramentas utilizadas por diferentes setores da empresa **Metaro**. Este projeto integra uma API desenvolvida em **Java com Spring Boot** e um frontend moderno em **Angular**, fornecendo uma base escalável, segura e modular para o uso diário de colaboradores.

## Visão Geral

A plataforma foi concebida para servir como uma central de informações e utilitários, com funcionalidades específicas por área, e controle de acesso baseado em perfis de usuários.

<br>

<img width="1918" height="945" alt="Captura de tela 2026-02-13 120414" src="https://github.com/user-attachments/assets/58df6278-9c77-47b2-bc77-349f601fe04f" />

<br>

## Funcionalidades

### Funcionalidades Gerais
- Mural eletrônico para comunicados e avisos internos.
- Dashboards com indicadores operacionais e estratégicos.
- Gerenciamento e compartilhamento de arquivos.
- Calendário corporativo com agendamento de eventos e salas de reunião.

### Funcionalidades por Setor
- **Comercial**: Configurador de produtos para elaboração de propostas comerciais.
- **Qualidade**: Checklists para inspeções, conformidade e auditorias internas.
- **Administrativo e Equipes**: Ferramenta de gerenciamento de tarefas (TODO), agendas e atividades.

## Arquitetura da Solução

A plataforma segue uma arquitetura de separação de responsabilidades entre backend e frontend, com comunicação por meio de APIs REST.

<br>

![450972810-0c24b013-6199-4e2a-a61e-787292d3323a (1)](https://github.com/user-attachments/assets/f243b4f5-5e92-43b9-adcc-627e3727474f)

### Backend (API)
- Java 21
- Spring Boot
- Spring Security (JWT e controle de perfis)
- JPA / Hibernate
- Banco de dados relacional (PostgreSQL)
- Banco de dados em memória (Redis)
- Documentação da API via Swagger

### Frontend
- Angular 17+
- RxJS
- JWT AuthGuard
- Design responsivo com SCSS

<br>

![preview-login](https://github.com/user-attachments/assets/037fef6e-bd49-4429-b3d1-d8f6f2c63e46)

## Autenticação e Controle de Acesso

A segurança da aplicação é baseada em tokens JWT, com autenticação por usuário e perfis hierárquicos (Administrador, Comercial, Qualidade, Colaborador, entre outros). Cada módulo e funcionalidade respeita regras específicas de autorização e visibilidade.

# Contribuições
Este projeto é de uso interno e exclusivo da Metaro. Contribuições podem ser realizadas por desenvolvedores autorizados, seguindo as diretrizes internas de versionamento e revisão de código.

# Licença
Projeto privado. Todos os direitos reservados à Metaro.
