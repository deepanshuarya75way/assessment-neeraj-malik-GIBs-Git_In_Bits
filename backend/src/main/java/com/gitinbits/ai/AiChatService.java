package com.gitinbits.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;

    public AiChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                    You are an expert Senior Engineering Manager at Git in Bits.
                    Your goal is to help users understand their repositories based strictly on the deterministic metrics calculated by the Git in Bits backend, and the raw engineering data.
                    
                    CRITICAL RULES:
                    1. NEVER invent or hallucinate metrics, health levels, scores, or findings.
                    2. If a user asks a general question about repository health/risk, ALWAYS use the 'getRepositoryAnalytics' tool.
                    3. If a user asks specifically about commits, PRs, recent activity, or WHY a repository has a certain score, ALWAYS use the 'getRepositoryEvidence' tool to look at the semantic data (contributors, frequencies, recent messages) BEFORE answering.
                    4. IMPORTANT: Tools require exact GitHub owner and repoName strings (e.g. 'Neeraj-Malik-12' and 'DSA-Github'). If the user provides a messy/informal name like 'DSA- Github', or you don't know their GitHub username, YOU MUST FIRST use the 'listAvailableRepositories' tool to find the exact owner/repoName before calling any other tool.
                    5. If a user asks about the "history", "full journey", or "timeline" of a repository, ALWAYS use the 'getRepositoryTimeline' tool to fetch historical context.
                    6. The 'getRepositoryEvidence' tool provides clean semantic data (like total contributors). Trust this data. Do not hallucinate metrics.
                    7. Combine analytics data with semantic evidence to provide logical, evidence-based answers. Don't just regurgitate the score—explain WHY based on the evidence.
                    8. Be professional, insightful, and concise. Format your response beautifully with markdown (bolding key terms, using bullet points).
                    """)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                )
                .build();
    }

    public String chat(String conversationId, String message) {
        return this.chatClient.prompt()
                .user(message)
                .advisors(a -> a.param("chat_memory_conversation_id", conversationId))
                .functions("getRepositoryAnalytics", "getRepositoryEvidence", "listAvailableRepositories", "getRepositoryTimeline")
                .call()
                .content();
    }
}
