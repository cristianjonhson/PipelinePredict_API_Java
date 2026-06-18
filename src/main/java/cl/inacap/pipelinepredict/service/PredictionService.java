package cl.inacap.pipelinepredict.service;

import cl.inacap.pipelinepredict.dto.PredictionRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PredictionService {

    private final JdbcTemplate jdbcTemplate;

    public PredictionService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getPredictionHistory() {
        return jdbcTemplate.queryForList("SELECT * FROM pipelinepredict.vw_recent_predictions");
    }

    public Map<String, Object> savePrediction(PredictionRequest request) {
        String sql = """
                WITH active_model AS (
                    SELECT ml_model_id
                    FROM pipelinepredict.ml_models
                    WHERE is_active = TRUE
                    ORDER BY trained_at DESC NULLS LAST, created_at DESC
                    LIMIT 1
                )
                INSERT INTO pipelinepredict.predictions (
                    pipeline_run_id,
                    ml_model_id,
                    risk,
                    failure_probability,
                    predicted_result,
                    probable_stage,
                    probable_cause,
                    recommendation
                )
                SELECT
                    ?,
                    ml_model_id,
                    ?::pipelinepredict.risk_level,
                    ?,
                    ?::pipelinepredict.predicted_status,
                    ?,
                    ?,
                    ?
                FROM active_model
                RETURNING
                    prediction_id,
                    pipeline_run_id,
                    risk,
                    failure_probability,
                    predicted_result,
                    probable_stage,
                    probable_cause,
                    recommendation,
                    predicted_at
                """;

        return jdbcTemplate.queryForMap(
                sql,
                request.pipelineRunId(),
                request.risk(),
                request.failureProbability(),
                request.predictedResult(),
                request.probableStage(),
                request.probableCause(),
                request.recommendation()
        );
    }

    public boolean pipelineExists(UUID pipelineRunId) {
        Integer count = jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM pipelinepredict.pipeline_runs
                WHERE pipeline_run_id = ?
                """,
                Integer.class,
                pipelineRunId
        );
        return count != null && count > 0;
    }
}
