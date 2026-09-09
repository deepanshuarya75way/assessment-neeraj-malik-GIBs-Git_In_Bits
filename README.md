# Git in Bits (GIBs) 🚀

> An Engineering Intelligence Platform that transforms raw GitHub data into AI-powered insights, measuring organization health, developer efficiency, and CI/CD reliability.


---

## 📖 Overview
**Git in Bits** is a full-stack dashboard designed to answer the questions GitHub's native UI cannot:
- Who are the most efficient developers and where are the bottlenecks?
- What are the recurring CI/CD failures across the organization?
- What did the engineering team *actually* accomplish this week?

By syncing organization data into MongoDB and feeding deterministic metrics into an LLM (openai/gpt-oss-120b via Groq), Git in Bits generates real-time, actionable engineering executive briefings.

## ✨ Key Features
- **AI Executive Briefing:** Automatically generates a comprehensive Markdown report summarizing organization activity, active workstreams, and areas needing attention.
- **Developer Efficiency Metrics:** Tracks individual developer impact (PRs merged, review cycles, commit additions/deletions) using robust identity resolution across Git and GitHub.
- **Automated Data Syncing:** Uses GitHub's ultra-fast GraphQL API to bulk-fetch commits, PRs, and reviews efficiently.
- **Repository Health Scoring:** A dedicated analysis engine that grades repositories based on branch protection, PR hygiene, and CI/CD stability.

## 🛠 Tech Stack
- **Frontend:** React 19, Vite, TailwindCSS 4, TanStack Query, Recharts
- **Backend:** Java 21 LTS, Spring Boot 3.4, Spring Security (OAuth2), Spring AI
- **Database:** MongoDB
- **External APIs:** GitHub GraphQL API, Groq Cloud (openai/gpt-oss-120b)

## 🏗 Architecture Flow
1. **Auth:** Users log in via GitHub OAuth2. A secure session HTTP-only cookie is established.
2. **Sync:** The backend silently fetches the authenticated user's organization data using GitHub's GraphQL API and persists it to MongoDB.
3. **Analysis:** The `DeveloperEvidenceService` and `OrganizationEvidenceService` aggregate raw documents into deterministic KPIs.
4. **AI Generation:** The KPIs are injected into a prompt and sent to Groq's Llama model to generate the markdown briefing.
5. **Presentation:** The React frontend caches responses via TanStack Query and renders the data in a responsive dark-themed dashboard.

## 🚀 Quick Start (Local Development)

### Prerequisites
- Java 21+, Node.js 18+, Docker Desktop
- GitHub OAuth App (Client ID & Secret)
- Groq API Key

### 1. Start Database
```bash
docker-compose up -d
```

### 2. Start Backend
Create `backend/.env` with `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`, and `GROQ_API_KEY`.
```bash
cd backend
mvn spring-boot:run
```

### 3. Start Frontend
```bash
cd frontend
npm install
npm run dev
```

Visit `http://localhost:5173` to explore the dashboard.
