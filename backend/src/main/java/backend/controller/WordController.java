package backend.controller;

import backend.api.ApiResponse;
import backend.api.PageResponse;
import backend.api.WordDtos;
import backend.domain.MasteryLevel;
import backend.domain.WordLevel;
import backend.security.SecurityUtils;
import backend.service.WordService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/words")
public class WordController {
    private final WordService wordService;

    public WordController(WordService wordService) {
        this.wordService = wordService;
    }

    @GetMapping
    public ApiResponse<PageResponse<WordDtos.SummaryResponse>> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "ALL") String level,
            @RequestParam(defaultValue = "ALL") String mastery,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ApiResponse.success(wordService.list(SecurityUtils.currentUserId(), keyword,
                "ALL".equalsIgnoreCase(level) ? null : WordLevel.valueOf(level.toUpperCase()),
                "ALL".equalsIgnoreCase(mastery) ? null : MasteryLevel.valueOf(mastery.toUpperCase()), page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<WordDtos.DetailResponse> detail(@PathVariable long id) {
        return ApiResponse.success(wordService.detail(SecurityUtils.currentUserId(), id));
    }
}
