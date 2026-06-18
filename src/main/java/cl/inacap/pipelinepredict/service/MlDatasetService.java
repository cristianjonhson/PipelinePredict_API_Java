package cl.inacap.pipelinepredict.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MlDatasetService {

    private final JdbcTemplate jdbcTemplate;

    public MlDatasetService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getPipelineDataset() {
        String sql = """
                SELECT *
                FROM pipelinepredict.vw_ml_pipeline_dataset
                ORDER BY repository_name, branch_name
                """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getFailedPipelines() {
        String sql = """
                SELECT *
                FROM pipelinepredict.vw_ml_pipeline_dataset
                WHERE target_failed = 1
                ORDER BY repository_name, failed_stage
                """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getPipelineLogs() {
        String sql = """
                SELECT
                    pr.pipeline_run_id,
                    r.name AS repository_name,
                    ps.stage_name,
                    pl.level,
                    pl.message,
                    pl.logged_at
                FROM pipelinepredict.pipeline_logs pl
                JOIN pipelinepredict.pipeline_runs pr ON pr.pipeline_run_id = pl.pipeline_run_id
                JOIN pipelinepredict.repositories r ON r.repository_id = pr.repository_id
                LEFT JOIN pipelinepredict.pipeline_stages ps ON ps.pipeline_stage_id = pl.pipeline_stage_id
                ORDER BY pl.logged_at DESC
                """;
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getPipelineFeatures() {
        String sql = """
                SELECT
                    pipeline_run_id,
                    repository_name,
                    branch_name,
                    technology_name,
                    provider,
                    workflow_name,
                    trigger_event,
                    duration_seconds,
                    commit_count,
                    files_changed,
                    previous_run_status,
                    coverage_percent,
                    bugs,
                    vulnerabilities_count,
                    code_smells,
                    duplicated_lines_density,
                    security_hotspots,
                    quality_gate_status,
                    failed_stage,
                    error_text,
                    target_failed
                FROM pipelinepredict.vw_ml_pipeline_dataset
                ORDER BY repository_name
                """;
        return jdbcTemplate.queryForList(sql);
    }
}
