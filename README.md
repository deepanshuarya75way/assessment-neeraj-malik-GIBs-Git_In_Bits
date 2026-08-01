# Git in Bits (GIBs) — Engineering Intelligence Platform

> A full-stack GitHub organization analytics platform that combines deterministic GitHub data, AI-powered engineering reports, and a modern dark-themed dashboard UI.

---

## Table of Contents

1. [What This Project Does](#1-what-this-project-does)
2. [System Architecture](#2-system-architecture)
3. [Tech Stack](#3-tech-stack)
4. [Project Structure](#4-project-structure)
5. [Backend Deep Dive](#5-backend-deep-dive)
6. [Frontend Deep Dive](#6-frontend-deep-dive)
7. [Data Flow — End to End](#7-data-flow--end-to-end)
8. [Prerequisites](#8-prerequisites)
9. [Environment Variables](#9-environment-variables)
10. [Running Locally](#10-running-locally)
11. [Key API Endpoints](#11-key-api-endpoints)
12. [Known Limitations & TODOs](#12-known-limitations--todos)

---

## 1. What This Project Does

Git in Bits is a **GitHub organization intelligence dashboard**. It answers the questions that GitHub's native UI cannot:

- **What did the whole team actually do this week?** (Not just a list of commits)
- **Which developer is most efficient? Who is a bottleneck?**
- **Where are the CI/CD failures concentrated?**
- **What is the organization's overall engineering health?**

It does this by:
1. Authenticating with GitHub via OAuth2
2. Syncing all organization data (commits, PRs, issues, workflows, reviews) into a local MongoDB database
3. Building deterministic evidence from that data (counts, timelines, workstreams)
4. Feeding that evidence into a Groq-hosted LLM (Llama 3.3 70B) to generate a structured, readable AI briefing
5. Presenting everything in a real-time React dashboard

---

## 2. System Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        BROWSER (User)                           │
│                                                                 │
│   React 19 + Vite + TailwindCSS 4 + TanStack Query             │
│   Port: 5173 (dev)                                              │
└───────────────────────┬─────────────────────────────────────────┘
                        │  HTTP (axios, cookie session)
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│                  SPRING BOOT BACKEND                            │
│                                                                 │
│   Port: 9090                                                    │
│                                                                 │
│  ┌─────────────┐   ┌──────────────┐   ┌────────────────────┐   │
│  │ Controllers │──▶│   Services   │──▶│  GitHub API Client │   │
│  │  (REST API) │   │ (Business    │   │  (RestClient-based)│   │
│  └─────────────┘   │  Logic)      │   └────────┬───────────┘   │
│                    └──────┬───────┘            │               │
│                           │                    │  api.github.com│
│  ┌────────────────────┐   │   ┌─────────────┐  │               │
│  │  AI Module         │   │   │  MongoDB    │◀─┘               │
│  │  (AiDashboard      │◀──┴──▶│  Persistence│                  │
│  │   Service via Groq)│       │  (Documents)│                  │
│  └────────────────────┘       └─────────────┘                  │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐    │
│  │  Analysis Engine (analysis/ package)                    │    │
│  │  Repository Health Scorers, Branch Scorers, Dev Scorers │    │
│  └────────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│                  MONGODB (Docker)                               │
│                                                                 │
│   Port: 27017                                                   │
│   Database: gitinbits                                           │
│                                                                 │
│   Collections:                                                  │
│   • commits          • pull_requests      • issues              │
│   • workflow_runs    • reviews            • organization_summary│
└─────────────────────────────────────────────────────────────────┘
                        │
┌───────────────────────▼─────────────────────────────────────────┐
│              EXTERNAL SERVICES                                  │
│                                                                 │
│   • api.github.com   — Source of all repository data           │
│   • api.groq.com     — LLM inference (Llama 3.3 70B)           │
└─────────────────────────────────────────────────────────────────┘
```

### Authentication Flow

```
User clicks "Login with GitHub"
        │
        ▼
GitHub OAuth2 Authorization (github.com/login/oauth/authorize)
        │
        ▼ (callback with auth code)
Spring Security OAuth2 Client
        │  exchanges code for access_token
        ▼
User session created (HTTP-only cookie, SameSite=Lax)
        │
        ▼
All subsequent GitHub API calls use the OAuth token
of the authenticated user — no server-level PAT required.
```

---

## 3. Tech Stack

### Backend
| Layer | Technology | Version |
|---|---|---|
| Runtime | Java | 21 LTS |
| Framework | Spring Boot | 3.4.3 |
| Security | Spring Security + OAuth2 Client | 6.4 |
| Database | Spring Data MongoDB | Latest |
| AI Integration | Spring AI (OpenAI-compatible, pointed at Groq) | 1.0.0-M6 |
| HTTP Client | Spring RestClient | (Spring 6.2 built-in) |
| Caching | Caffeine (in-memory) | Latest |
| Build | Maven | 3.x |
| Env Vars | spring-dotenv | 4.0.0 |
| Lombok | **Not used** — removed (incompatible with JDK 24+) | — |

### Frontend
| Layer | Technology | Version |
|---|---|---|
| Framework | React | 19 |
| Build Tool | Vite | 8 |
| Styling | TailwindCSS | 4.x |
| State/Data | TanStack Query (React Query) | 5 |
| HTTP | Axios | 1.x |
| Charts | Recharts | 2.x |
| Icons | Lucide React | Latest |
| Markdown | react-markdown + remark-gfm | Latest |
| Routing | React Router DOM | 7 |

### Infrastructure
| Component | Technology |
|---|---|
| Database | MongoDB (Docker) |
| LLM Provider | Groq Cloud (llama-3.3-70b-versatile) |
| Auth Provider | GitHub OAuth App |

---

## 4. Project Structure

```
GIBs dummy/                        ← Monorepo root
├── backend/                       ← Spring Boot application
│   └── src/main/java/com/gitinbits/
│       ├── GitInBitsApplication.java   ← Spring Boot entry point
│       ├── ai/                         ← AI report generation
│       ├── analysis/                   ← Repository health scoring engine
│       │   ├── branch/                 ← Branch health scorers
│       │   ├── developer/              ← Developer efficiency scorers
│       │   ├── organization/           ← Org-level aggregations
│       │   ├── repository/             ← Per-repo health analysis
│       │   └── team/                   ← Team analytics
│       ├── client/                     ← GitHub API HTTP clients
│       │   └── github/raw/             ← Raw response POJOs from GitHub
│       ├── config/                     ← Spring configs (Security, CORS, Cache)
│       ├── controller/                 ← REST API controllers
│       │   ├── dashboard/              ← Dashboard endpoints (main)
│       │   └── analysis/              ← Repository analysis endpoints
│       ├── dto/                        ← Data Transfer Objects
│       │   ├── context/                ← GitHubContext (who is calling, which org)
│       │   └── response/               ← API response shapes
│       ├── exception/                  ← GlobalExceptionHandler
│       ├── logging/                    ← AOP request/response logging
│       ├── persistence/
│       │   ├── document/               ← MongoDB document POJOs
│       │   └── repository/             ← Spring Data Mongo repositories
│       ├── provider/                   ← Token/auth providers
│       ├── service/                    ← Core business logic
│       │   ├── github/                 ← GitHub-specific service calls
│       │   ├── DeveloperEvidenceService.java   ← Gathers per-dev metrics
│       │   └── OrganizationEvidenceService.java ← Gathers org-level metrics
│       └── sync/                       ← RepositorySynchronizer (sync GitHub → MongoDB)
│
├── frontend/                      ← React + Vite application
│   └── src/
│       ├── api/                        ← All API calls + TanStack Query hooks
│       │   ├── client.ts               ← Axios instance (baseURL, credentials)
│       │   ├── queries.ts              ← General queries (repos, teams, etc.)
│       │   └── dashboardService.ts     ← Dashboard-specific queries & mutations
│       ├── components/
│       │   ├── ai/
│       │   │   ├── AIReportMarkdown.tsx  ← Custom Markdown renderer (emoji→icons)
│       │   │   └── AiManagerChat.tsx     ← Floating AI chat widget
│       │   ├── common/                   ← EntityHeader, MissingCapabilityCard
│       │   ├── dashboard/
│       │   │   ├── KpiSummaryRow.tsx    ← 6-tile KPI grid (real data)
│       │   │   └── TrendChart.tsx       ← Recharts area chart (mock data)
│       │   └── ui/                       ← Card, Spinner, Badge primitives
│       ├── context/
│       │   └── DataSourceContext.tsx   ← Global state: selected org/user
│       ├── layouts/                    ← App shell, sidebar, nav
│       ├── pages/
│       │   ├── Dashboard.tsx           ← Overview page (KPIs + AI Briefing)
│       │   ├── DeveloperDirectory.tsx  ← Per-developer analysis page
│       │   ├── Repositories.tsx        ← Repository list + health scores
│       │   └── ...                     ← Other pages
│       └── routes/                     ← React Router route definitions
│
├── docker-compose.yml             ← MongoDB container definition
├── .gitignore
└── README.md
```

---

## 5. Backend Deep Dive

### 5.1 Data Sync Flow (`sync/RepositorySynchronizer.java`)

This is the **engine** of the backend. When a user triggers an action (or on demand), the synchronizer:
1. Calls the GitHub API to fetch the latest commits, PRs, issues, workflow runs, and reviews for a given repository
2. Stores them as documents in MongoDB
3. Is designed to be **idempotent** — re-running it doesn't create duplicates

```
syncIfNeeded(context, "owner/repo")
     │
     ├── fetchCommits() → save to CommitDoc[]
     ├── fetchPullRequests() → save to PullRequestDoc[]
     ├── fetchIssues() → save to IssueDoc[]
     ├── fetchWorkflowRuns() → save to WorkflowRunDoc[]
     └── fetchReviews() → save to ReviewDoc[]
```

### 5.2 Evidence Services

There are two evidence services that **read from MongoDB** (never call GitHub directly):

**`OrganizationEvidenceService`** — produces:
- `activeWorkstreams` — open PRs + recent commit messages (what is being worked on)
- `recentlyCompleted` — merged PRs (what shipped)
- `needsAttention` — CI failures + stale PRs (what is broken)
- Counts: `totalCommits`, `totalPrsMerged`, `totalIssuesClosed`, `totalWorkflowFailures`, `activePrCount`

**`DeveloperEvidenceService`** — produces (per developer):
- `commitCount`, `totalAdditions`, `totalDeletions`
- `prsOpened`, `prsMerged`, `avgMergeTime`
- `reviewsConducted`, `workflowFailures`, `workflowSuccesses`

Both services accept a `since` / `until` `Instant` range, making them fully time-window aware.

### 5.3 AI Module (`ai/AiDashboardService.java`)

The AI module takes the evidence output and formats it into a structured prompt:
1. Calls `OrganizationEvidenceService.gatherEvidence()` to build the context
2. Constructs a detailed prompt instructing the LLM to act as an **Engineering Intelligence Analyst**
3. Sends it to Groq (Llama 3.3 70B) via Spring AI's `ChatClient`
4. Saves the result as an `OrganizationSummaryDoc` in MongoDB (keyed by `owner + timeframe`)
5. This cached result is served instantly on subsequent requests — the LLM is only re-invoked when the user explicitly clicks "Generate Briefing"

### 5.4 Analysis Engine (`analysis/`)

A separate, domain-rich engine for scoring repository and developer health. It has:
- **Scorers**: `BranchHealthScorer`, `ReviewHealthScorer`, `CommitHealthScorer`, etc.
- **Finding Generators**: Produce typed `HealthFinding` objects with severity levels
- **Context Builders**: Assemble all raw data into an `RepositoryAnalysisContext`

This engine feeds the **Repositories page** with per-repo health scores.

### 5.5 Security Configuration

- GitHub OAuth2 handles authentication — no username/password ever touches the app
- Sessions use **HTTP-only cookies** with `SameSite=Lax` for localhost cross-port safety
- All `/api/**` endpoints require authentication
- The GitHub API is called using the **user's own OAuth token** — the app never uses a hardcoded PAT

---

## 6. Frontend Deep Dive

### 6.1 Data Fetching (`api/`)

All data fetching is done via **TanStack Query** hooks. The pattern is:
- `useXxx()` hooks in `dashboardService.ts` and `queries.ts` handle caching, loading states, and refetching automatically
- The Axios instance in `client.ts` is pre-configured with `withCredentials: true` so the session cookie is always sent

### 6.2 Key Pages

| Page | What it shows |
|---|---|
| `Dashboard.tsx` | KPI row (6 real metrics) + Activity trend chart + AI Briefing card + Evidence panels |
| `DeveloperDirectory.tsx` | List of developers by commit count; clicking one loads an AI efficiency report |
| `Repositories.tsx` | List of all org repos with health scores from the analysis engine |

### 6.3 `AIReportMarkdown.tsx` — Custom Markdown Renderer

The AI returns its reports in Markdown. This component intercepts specific emoji markers the AI is instructed to use and **replaces them with Lucide icon chips** before rendering:

| AI outputs | Frontend renders |
|---|---|
| `## 🚨 Overall Status` | Blue `Activity` icon chip |
| `## 🟢 Going Well` | Emerald `CheckCircle2` icon chip |
| `## 🟠 Needs Attention` | Amber `AlertTriangle` icon chip |
| `## 📊 Metrics` | Blue `BarChart3` icon chip |
| `## 💡 Recommendation` | Indigo `Lightbulb` icon chip |

### 6.4 Global State — `DataSourceContext`

The currently selected GitHub organization is held in `DataSourceContext`. All pages read `sourceValue` from this context. When the user switches org (via the "Change Source" button), every TanStack Query cache key changes, and all data re-fetches automatically.

---

## 7. Data Flow — End to End

Here is what happens when a user logs in and views the Overview dashboard:

```
1. User logs in via GitHub OAuth
   └── Spring Security exchanges code → stores session cookie

2. Frontend loads Dashboard.tsx
   └── useOrganizationEvidence(owner, timeframe) fires
       └── GET /api/dashboard/organization/evidence?owner=X&timeframe=30_days
           └── Backend: syncAllReposAsync(owner) [background thread]
               └── For each repo → RepositorySynchronizer.syncIfNeeded()
                   └── GitHub API → MongoDB (commits, PRs, issues, etc.)
           └── OrganizationEvidenceService.gatherEvidence() reads MongoDB
           └── Returns: { totalCommits, totalPrsMerged, activePrCount, ... }

3. KpiSummaryRow renders with real counts from step 2

4. useOrganizationSummary(owner, timeframe) fires
   └── GET /api/dashboard/summary?owner=X&timeframe=30_days
       └── MongoDB → OrganizationSummaryDoc (cached AI report)
       └── Returns summaryText (Markdown from LLM)

5. AI Briefing card renders with AIReportMarkdown component
   └── Emoji markers in Markdown → Lucide icon chips

6. User clicks "Generate Briefing"
   └── POST /api/dashboard/summary/generate
       └── AiDashboardService.generateOrgBrief(owner, timeframe)
           └── Gathers fresh evidence from MongoDB
           └── Sends prompt to Groq (Llama 3.3 70B)
           └── Saves result to OrganizationSummaryDoc in MongoDB
   └── Frontend refetches summary → card updates
```

---

## 8. Prerequisites

Make sure you have the following installed:

| Tool | Version | Purpose |
|---|---|---|
| Java JDK | 21+ | Backend runtime |
| Maven | 3.8+ | Backend build tool |
| Node.js | 18+ | Frontend runtime |
| Docker Desktop | Latest | Run MongoDB |
| Git | Any | Version control |

You also need accounts/apps on:
- **GitHub** — to create an OAuth App (for login)
- **Groq Cloud** — to get a free API key for LLM inference ([console.groq.com](https://console.groq.com))

---

## 9. Environment Variables

The backend reads all secrets from a `.env` file in the `backend/` directory (loaded automatically by `spring-dotenv`).

**Create `backend/.env`:**

```env
# GitHub OAuth App credentials
# Create at: https://github.com/settings/developers → OAuth Apps → New
# Homepage URL: http://localhost:5173
# Callback URL: http://localhost:9090/login/oauth2/code/github
GITHUB_CLIENT_ID=your_github_oauth_app_client_id
GITHUB_CLIENT_SECRET=your_github_oauth_app_client_secret

# Groq API Key
# Get for free at: https://console.groq.com/keys
GROQ_API_KEY=your_groq_api_key_here
```

> **Important:** `backend/.env` is listed in `.gitignore`. Never commit this file.

---

## 10. Running Locally

### Step 1: Start MongoDB

```bash
# From the project root
docker-compose up -d
```

This starts MongoDB on port `27017` with a persistent data volume (`gitinbits-mongo-data`).

### Step 2: Start the Backend

```bash
cd backend
mvn spring-boot:run
```

Backend starts at **http://localhost:9090**

### Step 3: Start the Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend starts at **http://localhost:5173**

### Step 4: Open the App

1. Go to **http://localhost:5173**
2. Click **Login with GitHub**
3. Authorize the app — you'll be redirected back and logged in
4. Select your organization from the dropdown
5. The dashboard begins syncing data from GitHub in the background

---

## 11. Key API Endpoints

All endpoints require an authenticated session (cookie).

### Dashboard
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary?owner=X&timeframe=30_days` | Fetch cached AI briefing |
| `POST` | `/api/dashboard/summary/generate?owner=X&timeframe=30_days` | Regenerate AI briefing |
| `GET` | `/api/dashboard/organization/evidence?owner=X&timeframe=30_days` | Fetch org KPI data (commits, PRs, etc.) |
| `GET` | `/api/dashboard/developers?owner=X` | List all developers by commit activity |
| `GET` | `/api/dashboard/developers/{name}/evidence?owner=X&timeframe=30_days` | Per-developer metrics |
| `GET` | `/api/dashboard/developers/{name}/ai-report?owner=X` | AI efficiency report for one developer |

### Repositories & Analysis
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/repos?owner=X` | List all org repositories |
| `GET` | `/api/repos/{owner}/{repo}/analysis` | Repository health score report |
| `GET` | `/api/teams?owner=X` | List all org teams |

### Auth
| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/auth/me` | Get current authenticated user |
| `GET` | `/login/oauth2/code/github` | GitHub OAuth callback (handled by Spring) |
| `POST` | `/logout` | End session |

---

## 12. Known Limitations & TODOs

| Area | Issue | Notes |
|---|---|---|
| **Trend Chart** | Still uses mock data | Needs a time-series endpoint on the backend |
| **Real-time sync** | Sync is triggered on page load (background thread) — not a true webhook | Could be replaced with GitHub webhooks for true real-time |
| **Rate Limiting** | No explicit GitHub API rate limit handling | GitHub allows 5,000 req/hr for OAuth tokens — unlikely to hit for small orgs |
| **No Tests** | Zero unit or integration tests | Good first contribution area for the TL |
| **AI Caching** | Briefing is cached in MongoDB with no TTL — only refreshed on demand | Consider adding a scheduled nightly job |
| **Deployment** | Not configured for production | Needs HTTPS, `SameSite=None` cookie config, and environment-specific `application.yml` profiles |

---

## Contributing

1. Clone the repository
2. Create a feature branch: `git checkout -b feat/your-feature-name`
3. Make your changes
4. Commit: `git commit -m "feat: description of change"`
5. Push: `git push origin feat/your-feature-name`
6. Open a Pull Request against `master`
