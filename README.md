# BFF — desafioTecnico

## Posição na Arquitetura

```
Frontend → [BFF] → Backend → PostgreSQL
```

O BFF é o intermediário entre Frontend e Backend. Autentica o usuário, emite JWT, valida tokens nas rotas protegidas e assina todas as chamadas ao Backend com um service token.

## Stack

- Java 21 / Spring Boot 3.2 / Maven
- jjwt 0.12.6

## Responsabilidades

- Autenticar usuário (`POST /api/auth/login`) e emitir JWT de usuário
- Validar JWT do usuário nas rotas protegidas (JwtFilter)
- Assinar toda requisição ao Backend com um service token JWT de curta duração
- Buscar endereço pelo CEP diretamente via ViaCEP (`GET /api/address/{cep}`)
- Configurar CORS para o domínio do Frontend

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/login` | Pública | Autentica por login, retorna JWT + dados da pessoa |
| `POST` | `/api/persons` | Pública | Cadastra nova pessoa |
| `GET` | `/api/persons` | JWT usuário | Lista todas as pessoas |
| `GET` | `/api/persons/{id}` | JWT usuário | Busca pessoa por ID |
| `GET` | `/api/address/{cep}` | Pública | Busca endereço no ViaCEP |

## Segurança (JWT)

**Dois tokens distintos:**

- **Token de usuário** — emitido no login, validade 8h, enviado pelo Frontend como `Authorization: Bearer`
- **Service token** — gerado a cada chamada ao Backend, validade 30s, enviado como `X-Service-Token`

Ambos são JWT assinados com HMAC-SHA256 usando segredos separados.

## Hospedagem

Produção: **Railway** — deploy automático via GitHub Actions no push para `main`.

## Como Rodar Localmente

> Comece pelo projeto **Case-Tecnico** (migration) que sobe banco e backend.

```bash
# 1. Sobe banco, migrations e backend
cd Case-Tecnico && docker compose up postgres migrations backend

# 2. Sobe o BFF
cd case-tecnico-bff && mvn spring-boot:run
```

Disponível em `http://localhost:3001`

## Variáveis de Ambiente

| Variável | Descrição | Padrão local |
|---|---|---|
| `PORT` | Porta do servidor | `3001` |
| `BACKEND_URL` | URL do Backend | `http://localhost:8080` |
| `FRONTEND_ORIGIN` | Origem do Frontend (CORS) | `http://localhost:5173` |
| `JWT_SECRET` | Segredo para tokens de usuário (mín. 32 chars) | padrão inseguro |
| `SERVICE_TOKEN_SECRET` | Segredo compartilhado com o Backend (mín. 32 chars) | padrão inseguro |

Copie `.env.example` para `.env` e preencha os segredos antes de rodar em produção.
