package cl.inacap.pipelinepredict.controller;

import cl.inacap.pipelinepredict.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public ApiResponse<Map<String, Object>> health() {
        return ApiResponse.ok(Map.of(
                "service", "pipelinepredict-api",
                "status", "UP",
                "timestamp", LocalDateTime.now()
        ));
    }
}
