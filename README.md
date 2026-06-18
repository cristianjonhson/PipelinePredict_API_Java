# PipelinePredict IA - API Java Spring Boot MVP

MVP de la API Java Spring Boot para el proyecto académico **PipelinePredict IA**.

## Arquitectura aplicada

```text
GitHub Actions / Jenkins / GitLab CI
        |
        v
API Java Spring Boot
        |
        v
PostgreSQL
        |
        v
API Java Spring Boot expone datos vía REST
        |
        v
Python ML + FastAPI consume SOLO la API Java
        |
        v
Streamlit Dashboard
```

Regla principal del proyecto:

> Python no se conecta directamente a PostgreSQL. Toda lectura y escritura de datos pasa por la API Java Spring Boot.

## Requisitos

- Java 17
- Maven 3.9+
- Docker Desktop
- PostgreSQL client opcional (`psql`)


## Ejecución rápida solo con Docker

Esta opción levanta PostgreSQL y la API Java sin instalar Maven localmente:

```bash
docker compose -f docker-compose.full.yml up --build -d
```

Probar:

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/ml/dataset/pipelines
```

Apagar todo:

```bash
docker compose -f docker-compose.full.yml down
```

Borrar datos persistidos:

```bash
docker compose -f docker-compose.full.yml down -v
```

## 1. Levantar PostgreSQL con datos sintéticos

Desde la carpeta raíz del proyecto:

```bash
docker compose -f docker-compose.postgres.yml up -d
```

Ver logs:

```bash
docker logs pipelinepredict-postgres --tail=120
```

Probar conexión:

```bash
psql "postgresql://pipelinepredict:pipelinepredict123@127.0.0.1:5432/pipelinepredict_db"
```

Dentro de `psql`:

```sql
SET search_path TO pipelinepredict;
\dt
SELECT * FROM vw_ml_pipeline_dataset LIMIT 5;
```

## 2. Ejecutar API Java local

```bash
mvn clean spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## 3. Probar endpoints

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/ml/dataset/pipelines
curl http://localhost:8080/api/ml/dataset/failures
curl http://localhost:8080/api/ml/dataset/logs
curl http://localhost:8080/api/ml/features/pipelines
curl http://localhost:8080/api/dashboard/summary
curl http://localhost:8080/api/dashboard/failures-by-stage
curl http://localhost:8080/api/dashboard/risk-by-repository
curl http://localhost:8080/api/dashboard/recent-predictions
curl http://localhost:8080/api/ml/predictions/history
```

O ejecuta:

```bash
./scripts/test_endpoints.sh
```

## 4. Guardar predicción enviada por Python

Primero obtiene un `pipeline_run_id`:

```bash
curl -s http://localhost:8080/api/ml/dataset/pipelines
```

Luego envía una predicción:

```bash
curl -X POST http://localhost:8080/api/ml/predictions \
  -H "Content-Type: application/json" \
  -d '{
    "pipelineRunId": "REEMPLAZAR_UUID_PIPELINE",
    "risk": "HIGH",
    "failureProbability": 0.81,
    "predictedResult": "FAILED",
    "probableStage": "quality",
    "probableCause": "Error en resolución de dependencias",
    "recommendation": "Revisar versiones de librerías y configuración del archivo de dependencias."
  }'
```

Valores válidos:

- `risk`: `LOW`, `MEDIUM`, `HIGH`, `CRITICAL`
- `predictedResult`: `SUCCESS`, `FAILED`

## 5. Consumo desde Python sin conectar a PostgreSQL

```python
import requests
import pandas as pd

response = requests.get("http://localhost:8080/api/ml/dataset/pipelines", timeout=10)
response.raise_for_status()

df = pd.DataFrame(response.json()["data"])
print(df.head())
```

## 6. Apagar servicios

```bash
docker compose -f docker-compose.postgres.yml down
```

Borrar datos persistidos:

```bash
docker compose -f docker-compose.postgres.yml down -v
```

## Endpoints incluidos

| Método | Endpoint | Uso |
|---|---|---|
| GET | `/api/health` | Verificar estado de la API |
| GET | `/api/ml/dataset/pipelines` | Dataset completo para Python ML |
| GET | `/api/ml/dataset/failures` | Pipelines fallidos |
| GET | `/api/ml/dataset/logs` | Logs para análisis de texto |
| GET | `/api/ml/features/pipelines` | Features para entrenamiento |
| POST | `/api/ml/predictions` | Python envía predicción a Java |
| GET | `/api/ml/predictions/history` | Historial de predicciones |
| GET | `/api/dashboard/summary` | Resumen general |
| GET | `/api/dashboard/failures-by-stage` | Fallas por etapa |
| GET | `/api/dashboard/risk-by-repository` | Riesgo por repositorio |
| GET | `/api/dashboard/recent-predictions` | Predicciones recientes |

## Nota académica

Este MVP usa `JdbcTemplate` para acelerar el desarrollo y consumir directamente vistas SQL ya preparadas para Machine Learning. En una versión posterior se pueden agregar entidades JPA, seguridad JWT, integración real con GitHub Actions/Jenkins/GitLab CI y clientes HTTP hacia el módulo Python FastAPI.
