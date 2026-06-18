package cl.inacap.pipelinepredict.controller;

import cl.inacap.pipelinepredict.dto.ApiResponse;
import cl.inacap.pipelinepredict.service.MlDatasetService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ml")
public class MlDatasetController {

    private final MlDatasetService mlDatasetService;

    public MlDatasetController(MlDatasetService mlDatasetService) {
        this.mlDatasetService = mlDatasetService;
    }

    @GetMapping("/dataset/pipelines")
    public ApiResponse<List<Map<String, Object>>> getPipelineDataset() {
        return ApiResponse.ok("Dataset de pipelines para Machine Learning", mlDatasetService.getPipelineDataset());
    }

    @GetMapping("/dataset/failures")
    public ApiResponse<List<Map<String, Object>>> getFailedPipelines() {
        return ApiResponse.ok("Pipelines fallidos para entrenamiento o análisis", mlDatasetService.getFailedPipelines());
    }

    @GetMapping("/dataset/logs")
    public ApiResponse<List<Map<String, Object>>> getPipelineLogs() {
        return ApiResponse.ok("Logs de pipelines para análisis de texto", mlDatasetService.getPipelineLogs());
    }

    @GetMapping("/features/pipelines")
    public ApiResponse<List<Map<String, Object>>> getPipelineFeatures() {
        return ApiResponse.ok("Features principales para modelo ML", mlDatasetService.getPipelineFeatures());
    }
}
