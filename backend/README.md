# Git in Bits — Backend PoC

A **Spring Boot 3.4.x / Java 21** backend that authenticates users via **GitHub OAuth2** and exposes GitHub repository metadata through clean REST APIs.

This is a Proof of Concept to validate whether GitHub's public API exposes sufficient data for the future Git in Bits platform. The architecture is intentionally designed to evolve into the full platform (Normalization → Analysis → AI → Visualization) without major refactoring.

---

## Prerequisites

- **Java 21** (LTS)
- **Maven 3.9+**
- A **GitHub OAuth App** (see setup below)

---

## GitHub OAuth App Setup

Before you can run the backend, you must create a GitHub OAuth App:

1. Go to [https://github.com/settings/developers](https://github.com/settings/developers)
2. Click **"New OAuth App"**
3. Fill in the fields:

   | Field | Value |
   |---|---|
   | Application name | `Git in Bits PoC` |
   | Homepage URL | `http://localhost:5174` |
   | Authorization callback URL | **`http://localhost:9090/login/oauth2/code/github`** |

4. Click **"Register application"**
5. Copy the **Client ID**
6. Click **"Generate a new client secret"** and copy the **Client Secret**

---

## Environment Setup

Copy `.env.example` to `.env` and fill in your credentials:

```bash
cp .env.example .env
```

Edit `.env`:

```env
GITHUB_CLIENT_ID=your_client_id_here
GITHUB_CLIENT_SECRET=your_client_secret_here
```

> **Never commit `.env` to version control.**

---

## Running the Backend

### Option 1 — Using environment variables directly

**Windows PowerShell:**
```powershell
$env:GITHUB_CLIENT_ID="your_client_id"; $env:GITHUB_CLIENT_SECRET="your_client_secret"; mvn spring-boot:run
```

**Windows CMD:**
```cmd
set GITHUB_CLIENT_ID=your_client_id && set GITHUB_CLIENT_SECRET=your_client_secret && mvn spring-boot:run
```

**macOS / Linux:**
```bash
GITHUB_CLIENT_ID=your_client_id GITHUB_CLIENT_SECRET=your_client_secret mvn spring-boot:run
```

### Option 2 — Using application-local.yml

Create `src/main/resources/application-local.yml`:
```yaml
github-client-id: your_client_id
github-client-secret: your_client_secret
```
Then run with: `mvn spring-boot:run -Dspring-boot.run.profiles=local`

The backend starts on **http://localhost:9090**.

---

## Authentication Flow

```
1. Frontend → GET http://localhost:9090/oauth2/authorization/github
   ↓ Browser redirected to GitHub login page

2. User logs in on GitHub
   ↓ GitHub redirects to http://localhost:9090/login/oauth2/code/github

3. Spring Security exchanges code for access token
   ↓ Token stored in-memory (no database needed)

4. Browser redirected to http://localhost:5174/auth/callback
   ↓ Session cookie (JSESSIONID) is set

5. Frontend calls GET /api/auth/me  →  { login, name, avatarUrl, ... }

6. Frontend calls GET /api/auth/orgs  →  [ { login, description, ... }, ... ]

7. User selects organization from the list

8. All subsequent API calls include: X-GitHub-Org: <org-login>
```

---

## API Reference

All endpoints below **require an active session** (established via GitHub OAuth login).
All endpoints except `/api/auth/*` require the **`X-GitHub-Org` header**.

### Auth

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/auth/me` | Authenticated GitHub user profile |
| `GET` | `/api/auth/orgs` | User's GitHub organizations (for org picker) |
| `POST` | `/api/auth/logout` | Invalidates session and redirects to frontend |

**Trigger login:**
```
GET http://localhost:9090/oauth2/authorization/github
```

### Organization

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/org` | Full organization details |

### Repositories

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/repos` | All repositories in the org |
| `GET` | `/api/repos/{repo}` | Single repository metadata |
| `GET` | `/api/repos/{repo}/branches` | All branches |
| `GET` | `/api/repos/{repo}/commits` | All commits (default branch) |
| `GET` | `/api/repos/{repo}/pulls` | All pull requests (all states) |
| `GET` | `/api/repos/{repo}/pulls/{number}/reviews` | All reviews for a PR |
| `GET` | `/api/repos/{repo}/pulls/{number}/comments` | All inline review comments |
| `GET` | `/api/repos/{repo}/issues` | All issues (PRs excluded) |
| `GET` | `/api/repos/{repo}/contributors` | All contributors |
| `GET` | `/api/repos/{repo}/releases` | All releases |

### Teams

| Method | Path | Description |
|---|---|---|
| `GET` | `/api/teams` | All teams with members and repo access |

### Example Request

```bash
# After logging in via browser
curl -b cookies.txt \
     -H "X-GitHub-Org: my-org" \
     http://localhost:9090/api/repos
```

### Error Response Format

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found: /repos/my-org/nonexistent-repo",
  "path": "/api/repos/nonexistent-repo",
  "timestamp": "2026-07-24T11:22:00Z"
}
```

---

## Project Structure

```
src/main/java/com/gitinbits/
├── GitInBitsApplication.java
├── config/
│   ├── SecurityConfig.java        # OAuth2 login, CSRF, session, logout
│   ├── RestClientConfig.java      # GitHub RestClient with default headers
│   ├── CorsConfig.java            # CORS for localhost:5174 only
│   └── GitHubProperties.java      # @ConfigurationProperties for github.*
├── controller/
│   ├── AuthController.java        # /api/auth/*
│   ├── OrgController.java         # /api/org
│   ├── RepoController.java        # /api/repos/**
│   └── TeamController.java        # /api/teams
├── service/github/
│   ├── GitHubAuthService.java
│   ├── GitHubOrgService.java
│   ├── GitHubRepoService.java
│   └── GitHubTeamService.java
├── client/github/
│   ├── GitHubClient.java          # Interface (sole GitHub API gateway)
│   ├── GitHubClientImpl.java      # RestClient impl + token retrieval + error mapping
│   ├── pagination/
│   │   └── GitHubPaginator.java   # Reusable Link-header page fetcher
│   └── raw/                       # Internal GitHub JSON response records
│       └── Raw*.java
├── dto/
│   ├── context/OrgContext.java
│   └── response/
│       ├── auth/, org/, repo/, team/, error/
│       └── *.java                 # Public DTOs
├── exception/
│   ├── GitHubAuthException.java
│   ├── GitHubRateLimitException.java
│   ├── GitHubNotFoundException.java
│   ├── GitHubApiException.java
│   └── GlobalExceptionHandler.java
├── logging/
│   └── LoggingAspect.java        # AOP request/response logging
└── provider/
    └── package-info.java          # Reserved for future providers (GitLab, etc.)
```

---

## Architecture Notes

### Why Spring Boot 3.4.x (not 4.x)?

Spring Boot 4.1.0 was released June 2026 — only weeks before this PoC was built. It is not yet widely enterprise-adopted. Spring Boot 3.4.x (GA November 2024) with Java 21 LTS is the most battle-tested combination available and fully meets the platform requirements.

### Pagination

All list endpoints transparently fetch **all pages** from GitHub. GitHub paginates at 30 items/page by default and signals additional pages via the `Link` response header:

```
Link: <https://api.github.com/orgs/my-org/repos?page=2>; rel="next", ...
```

The `GitHubPaginator` follows the `rel="next"` chain until exhausted. For large organizations, this may take time — this is acceptable for a PoC validating data completeness.

### Future Evolution

The architecture supports adding new layers without major refactoring:

```
Controller → Service → [Future: NormalizationService] → GitHubClient → GitHub API
                     → [Future: AnalysisService]
                     → [Future: AIService]
```

Additional providers (GitLab, Bitbucket) plug in via the `provider/` package following the `GitHubClient` interface pattern.

---

## Build

```bash
mvn clean package -DskipTests
```

```bash
java -jar target/gitinbits-poc-0.0.1-SNAPSHOT.jar \
  --GITHUB_CLIENT_ID=xxx \
  --GITHUB_CLIENT_SECRET=yyy
```
