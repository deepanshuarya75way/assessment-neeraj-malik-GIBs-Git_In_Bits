package com.gitinbits.ai;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    public record ChatRequest(String conversationId, String message) {}
    public record ChatResponse(String response) {}

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String response = aiChatService.chat(request.conversationId(), request.message());
        return new ChatResponse(response);
    }
}
