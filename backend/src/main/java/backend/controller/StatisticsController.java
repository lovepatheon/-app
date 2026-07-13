package backend.controller;

import backend.api.ApiResponse;
import backend.api.StatisticsDtos;
import backend.security.SecurityUtils;
import backend.service.StatisticsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public ApiResponse<StatisticsDtos.OverviewResponse> overview(@RequestParam(defaultValue = "7D") String range) {
        return ApiResponse.success(statisticsService.overview(SecurityUtils.currentUserId(), range.toUpperCase()));
    }
}
