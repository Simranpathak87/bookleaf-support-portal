package bookleaf.supportportal.ai;

public interface AiClient {
    String complete(String systemPrompt, String userPrompt, boolean jsonMode);
}