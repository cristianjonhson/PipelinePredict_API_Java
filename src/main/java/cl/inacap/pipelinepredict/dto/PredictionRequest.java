package cl.inacap.pipelinepredict.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record PredictionRequest(
        @NotNull
        UUID pipelineRunId,

        @NotBlank
        String risk,

        @NotNull
        @DecimalMin("0.0")
        @DecimalMax("1.0")
        BigDecimal failureProbability,

        @NotBlank
        String predictedResult,

        String probableStage,
        String probableCause,
        String recommendation
) {
}
