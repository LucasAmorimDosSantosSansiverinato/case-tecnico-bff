# BFF — desafioTecnico

## Arquitetura

```
Frontend → [BFF] → Backend → PostgreSQL
```

Intermediário entre Frontend e Backend. O Backend não é acessível diretamente.

---

## Stack

- Java 21 / Spring Boot 3.2 / Maven
- jjwt 0.12.6

---

## Por que BFF?

- O Backend fica isolado — só o BFF sabe o endereço e o `SERVICE_TOKEN_SECRET`
- CORS fica aqui, o Backend não precisa se preocupar com isso
- No futuro é fácil adicionar um backoffice: novo BFF com permissões diferentes, mesmo Backend

**Clerk (não usado):** simplificaria muito o auth, mas não estava nas techs da vaga.

---

## JWT duplo

**Token de usuário** (8h): emitido no login, enviado pelo Frontend em rotas protegidas (`Authorization: Bearer`).

**Service token** (30s): gerado pelo BFF a cada chamada ao Backend (`X-Service-Token`). Validade curta intencional — mesmo interceptado, expira rápido.

---

## Endpoints

| Método | Rota | Auth | Descrição |
|---|---|---|---|
| `POST` | `/api/auth/login` | Pública | Autentica, retorna JWT + dados |
| `POST` | `/api/persons` | Pública | Cadastra pessoa |
| `GET` | `/api/persons` | JWT usuário | Lista todas |
| `GET` | `/api/persons/{id}` | JWT usuário | Busca por ID |
| `GET` | `/api/address/{cep}` | Pública | Consulta ViaCEP |

---

## Hospedagem

Render — deploy automático no push para `main`.
