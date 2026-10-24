package bookleaf.supportportal.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.*;
import bookleaf.supportportal.ai.DraftService;

@RestController
@RequestMapping("/api/admin/tickets")
public class AdminAiController {

    private final DraftService draftService;

    public AdminAiController(DraftService draftService) {
        this.draftService = draftService;
    }

    @GetMapping("/{id}/ai-draft")
    public Map<String, String> draft(@PathVariable Long id) {
        return Map.of("draft", draftService.getOrCreateDraft(id, false));
    }

    @PostMapping("/{id}/ai-draft/regenerate")
    public Map<String, String> regenerate(@PathVariable Long id) {
        return Map.of("draft", draftService.getOrCreateDraft(id, true));
    }
}