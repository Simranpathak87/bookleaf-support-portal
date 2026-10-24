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
@ConditionalOnProperty(name = "ai.provider", havingValue = "ollama", matchIfMissing = true)
public class OllamaAiClient implements AiClient {

    private final RestClient http;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;

    public OllamaAiClient(@Value("${ai.base-url}") String baseUrl,
                          @Value("${ai.model}") String model,
                          @Value("${ai.timeout-seconds}") int timeoutSec) {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(5000);
        f.setReadTimeout(timeoutSec * 1000);
        this.http = RestClient.builder().baseUrl(baseUrl).requestFactory(f).build();
        this.model = model;
    }

    @Override
    public String complete(String system, String user, boolean json) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("stream", false);
            body.put("messages", List.of(
                    Map.of("role", "system", "content", system),
                    Map.of("role", "user", "content", user)));
            body.put("options", Map.of("temperature", 0.2));
            if (json) body.put("format", "json");

            String res = http.post().uri("/api/chat")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(mapper.writeValueAsString(body))
                    .retrieve().body(String.class);
            return mapper.readTree(res).get("message").get("content").asText();
        } catch (Exception e) {
            throw new AiUnavailableException("Ollama call failed: " + e.getMessage(), e);
        }
    }
}