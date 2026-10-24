package bookleaf.supportportal.controller;



import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/api/me")
    public Map<String, String> me(Authentication auth) {
        return Map.of("userId", auth.getName(), "roles", auth.getAuthorities().toString());
    }

    @GetMapping("/api/admin/ping")
    public Map<String, String> adminPing() {
        return Map.of("message", "admin ok");
    }
}