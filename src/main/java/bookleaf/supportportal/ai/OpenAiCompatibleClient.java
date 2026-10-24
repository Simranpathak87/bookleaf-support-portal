package bookleaf.supportportal.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

@Component
@ConditionalOnProperty(name = "ai.provider", havingValue = "openai")
public class OpenAiCompatibleClient implements AiClient {

    private final RestClient http;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;
    private final String apiKey;

    public OpenAiCompatibleClient(@Value("${ai.base-url}") String baseUrl,
                                  @Value("${ai.model}") String model,
                                  @Value("${ai.api-key}") String apiKey,
                                  @Value("${ai.timeout-seconds}") int timeoutSec) {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5000);
        f.setReadTimeout(timeoutSec * 1000);
        this.http = RestClient.builder().baseUrl(baseUrl).requestFactory(f).build();
        this.model = model;
        this.apiKey = apiKey;
    }

    @Override
    public String complete(String system, String user, boolean json) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new AiUnavailableException("AI_API_KEY is not configured");
        }
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("temperature", 0.2);
            body.put("max_tokens", 500);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", system),
                    Map.of("role", "user", "content", user)));
            if (json) body.put("response_format", Map.of("type", "json_object"));

            String res = http.post().uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(body))
                    .retrieve().body(String.class);
            return mapper.readTree(res).get("choices").get(0).get("message").get("content").asText();
        } catch (Exception e) {
            throw new AiUnavailableException("AI call failed: " + e.getMessage(), e);
        }
    }
}