# BookLeaf Author Support & Communication Portal

Live demo: https://bookleaf-support-portal-zodq.onrender.com
(Free hosting: the first request after idle can take up to a minute.)

## Test credentials
| Role | Email | Password |
|---|---|---|
| Admin | admin@bookleaf.com | Admin@123 |
| Author | priya.sharma@email.com | Author@123 |
| Author (book in production) | sneha.kulkarni@email.com | Author@123 |

All 10 authors from the sample dataset use the password `Author@123`.

## Tech stack
Spring Boot 4 (Java 17), PostgreSQL, Spring Security + JWT, plain HTML/JS frontend served by Spring, polling for live updates, Docker on Render.

## Run locally
1. Install JDK 17, Maven, PostgreSQL. Create a database: `CREATE DATABASE bookleaf;`
2. Optional environment variables: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `AI_PROVIDER`, `AI_BASE_URL`, `AI_MODEL`, `AI_API_KEY`.
3. Local AI: install Ollama and run `ollama pull phi3:mini` (default). Hosted AI: set `AI_PROVIDER=openai` with an OpenAI-compatible `AI_BASE_URL`, `AI_MODEL` and `AI_API_KEY`.
4. Run `./mvnw spring-boot:run`, open http://localhost:8080. Sample data is seeded on first start.

## Architecture
- Layered: controller -> service -> repository -> entity, with DTO records and a global exception handler.
- JWT auth filter + role-based rules: `/api/admin/**` is ADMIN only, `/api/books` and `/api/tickets` are AUTHOR only. Authors can only read their own data; other authors' ticket IDs return 404.
- Single `users` table (role AUTHOR/ADMIN). Books keep nullable fields for books still in production.
- Real-time: the frontend polls every 5 seconds (simple and reliable on free hosting; WebSocket/SSE is the next step).

## AI integration
- **Provider-agnostic:** an `AiClient` interface with two implementations (Ollama, OpenAI-compatible). Switched by one property.
- **Two calls, cost-aware:** (1) on ticket creation, a small background call returns JSON `{category, priority}` using only the subject and description (no knowledge base). (2) The draft is generated only when an admin asks for it, and is cached on the ticket.
- **Draft context:** only the one relevant book record and the one relevant knowledge-base section for the ticket category, plus the tone rules. No full KB and no ticket history.
- **Accuracy:** real numbers (pending royalty, last payout, status, payout deadline, Rs 1,000 threshold) are computed in code and passed to the model, so it does not invent them. ISBN issues are forced to at least HIGH priority.
- **Graceful degradation:** if the AI is down or returns bad JSON, the ticket is still created with a keyword-based category and `aiStatus=FALLBACK`. The draft endpoint returns 503 and the admin writes the reply manually.
- **Security:** API keys come from environment variables only and never reach the frontend.
- **Model choice:** local phi3:mini for development (free, no key); a hosted small model (e.g. Llama 3.1 8B on Groq, or gpt-4o-mini / Claude Haiku) for the live demo, chosen for low cost and latency on short support tasks.

## API (all JSON, `Authorization: Bearer <token>`)
| Method | Path | Role | Purpose |
|---|---|---|---|
| POST | /api/auth/login | public | Login, returns JWT |
| GET | /api/books | AUTHOR | My books |
| POST | /api/tickets | AUTHOR | Create ticket |
| GET | /api/tickets | AUTHOR | My tickets with replies |
| GET | /api/tickets/{id} | AUTHOR | One of my tickets |
| GET | /api/admin/tickets | ADMIN | Queue; filters: status, category, priority, from, to |
| GET | /api/admin/tickets/{id} | ADMIN | Ticket detail with notes |
| PATCH | /api/admin/tickets/{id}/status | ADMIN | Update status |
| PATCH | /api/admin/tickets/{id}/category | ADMIN | Override AI category |
| PATCH | /api/admin/tickets/{id}/priority | ADMIN | Override AI priority |
| POST | /api/admin/tickets/{id}/assign-to-me | ADMIN | Assign to myself |
| POST | /api/admin/tickets/{id}/notes | ADMIN | Add internal note (hidden from author) |
| POST | /api/admin/tickets/{id}/replies | ADMIN | Send reply to author |
| GET | /api/admin/tickets/{id}/ai-draft | ADMIN | Get (cached) AI draft |
| POST | /api/admin/tickets/{id}/ai-draft/regenerate | ADMIN | Regenerate AI draft |

Errors use `{ "error": "..." }`; validation errors add a `fields` map. Status codes: 400, 401, 403, 404, 503.

## Known limitations / next steps
- Polling instead of WebSocket/SSE; file attachment is UI-only (filename stored).
- Queue filtering and sorting happen in memory; production needs DB queries and pagination.
- No rate limiting, refresh tokens or password reset; JWT is kept in localStorage.
- AI classification accuracy depends on the model; no evaluation set yet.
- No automated tests; free hosting sleeps when idle.