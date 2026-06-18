package cl.inacap.pipelinepredict.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> getSummary() {
        return jdbcTemplate.queryForList("SELECT * FROM pipelinepredict.vw_dashboard_summary");
    }

    public List<Map<String, Object>> getFailuresByStage() {
        return jdbcTemplate.queryForList("SELECT * FROM pipelinepredict.vw_failures_by_stage");
    }

    public List<Map<String, Object>> getRiskByRepository() {
        return jdbcTemplate.queryForList("SELECT * FROM pipelinepredict.vw_risk_by_repository");
    }

    public List<Map<String, Object>> getRecentPredictions() {
        return jdbcTemplate.queryForList("SELECT * FROM pipelinepredict.vw_recent_predictions");
    }
}
