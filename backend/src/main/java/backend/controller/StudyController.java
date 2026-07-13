package backend.controller;

import backend.api.ApiResponse;
import backend.api.StudyDtos;
import backend.domain.StudyMode;
import backend.security.SecurityUtils;
import backend.service.StudyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/study")
public class StudyController {
    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<StudyDtos.DashboardResponse> dashboard() {
        return ApiResponse.success(studyService.dashboard(SecurityUtils.currentUserId()));
    }

    @GetMapping("/queue")
    public ApiResponse<StudyDtos.QueueResponse> queue(@RequestParam(defaultValue = "NEW") StudyMode mode) {
        return ApiResponse.success(studyService.queue(SecurityUtils.currentUserId(), mode));
    }

    @PostMapping("/answers")
    public ApiResponse<StudyDtos.AnswerResponse> answer(@Valid @RequestBody StudyDtos.AnswerRequest request) {
        return ApiResponse.success(studyService.answer(SecurityUtils.currentUserId(), request));
    }
}
