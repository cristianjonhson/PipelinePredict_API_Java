package cl.inacap.pipelinepredict.controller;

import cl.inacap.pipelinepredict.dto.ApiResponse;
import cl.inacap.pipelinepredict.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ApiResponse<List<Map<String, Object>>> getSummary() {
        return ApiResponse.ok("Resumen general del dashboard", dashboardService.getSummary());
    }

    @GetMapping("/failures-by-stage")
    public ApiResponse<List<Map<String, Object>>> getFailuresByStage() {
        return ApiResponse.ok("Fallas agrupadas por etapa", dashboardService.getFailuresByStage());
    }

    @GetMapping("/risk-by-repository")
    public ApiResponse<List<Map<String, Object>>> getRiskByRepository() {
        return ApiResponse.ok("Riesgo agrupado por repositorio", dashboardService.getRiskByRepository());
    }

    @GetMapping("/recent-predictions")
    public ApiResponse<List<Map<String, Object>>> getRecentPredictions() {
        return ApiResponse.ok("Predicciones recientes", dashboardService.getRecentPredictions());
    }
}
