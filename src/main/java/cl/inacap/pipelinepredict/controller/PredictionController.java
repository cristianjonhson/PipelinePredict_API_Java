package cl.inacap.pipelinepredict.controller;

import cl.inacap.pipelinepredict.dto.ApiResponse;
import cl.inacap.pipelinepredict.dto.PredictionRequest;
import cl.inacap.pipelinepredict.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ml/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @GetMapping("/history")
    public ApiResponse<List<Map<String, Object>>> getPredictionHistory() {
        return ApiResponse.ok("Historial de predicciones", predictionService.getPredictionHistory());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> savePrediction(@Valid @RequestBody PredictionRequest request) {
        if (!predictionService.pipelineExists(request.pipelineRunId())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("El pipeline_run_id no existe en PostgreSQL", Map.of("pipelineRunId", request.pipelineRunId()))
            );
        }

        try {
            Map<String, Object> savedPrediction = predictionService.savePrediction(request);
            return ResponseEntity.ok(ApiResponse.ok("Predicción guardada correctamente", savedPrediction));
        } catch (EmptyResultDataAccessException ex) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.error("No existe un modelo ML activo para asociar la predicción", Map.of())
            );
        }
    }
}
