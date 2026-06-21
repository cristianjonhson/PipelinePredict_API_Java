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

---

## Requisitos

- Java 17
- Maven 3.9+
- Docker Desktop
- PostgreSQL client opcional (`psql`)
- `curl` para pruebas rápidas

---

## Archivos Docker incluidos

El proyecto incluye tres formas de ejecución con Docker:

| Archivo | Qué levanta | Cuándo usarlo |
|---|---|---|
| `Dockerfile` | Solo construye la imagen de la API Java | Cuando quieres compilar y ejecutar manualmente el contenedor de la API |
| `docker-compose.full.yml` | PostgreSQL + API Java | Opción recomendada para probar todo rápido |
| `docker-compose.postgres.yml` | Solo PostgreSQL con datos sintéticos | Cuando quieres ejecutar la API local con Maven |
| `docker-compose.yml` | Solo API Java en Docker | Cuando PostgreSQL ya está levantado fuera de ese compose |

---

# Opción A: ejecución completa con Docker Compose

Esta es la opción recomendada para probar el MVP completo sin instalar Maven localmente.

Levanta PostgreSQL, crea la base de datos, carga scripts SQL y construye la API Java usando el `Dockerfile`.

```bash
docker compose -f docker-compose.full.yml up --build -d
```

Ver contenedores:

```bash
docker ps
```

Ver logs de PostgreSQL:

```bash
docker logs pipelinepredict-postgres --tail=120
```

Ver logs de la API:

```bash
docker logs pipelinepredict-api --tail=120
```

Probar API:

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/ml/dataset/pipelines
curl http://localhost:8080/api/dashboard/summary
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

Apagar servicios:

```bash
docker compose -f docker-compose.full.yml down
```

Apagar y borrar datos persistidos:

```bash
docker compose -f docker-compose.full.yml down -v
```

Reconstruir desde cero:

```bash
docker compose -f docker-compose.full.yml down -v
docker compose -f docker-compose.full.yml up --build -d
```

---

# Opción B: levantar solo PostgreSQL y ejecutar Java localmente

Usa esta opción si quieres desarrollar con Maven desde tu máquina.

## 1. Levantar PostgreSQL

```bash
docker compose -f docker-compose.postgres.yml up -d
```

Este compose carga automáticamente:

```text
db/init/01_schema_pipelinepredict.sql
db/init/02_seed_pipelinepredict.sql
db/init/03_views_queries.sql
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

Salir de `psql`:

```sql
\q
```

## 2. Ejecutar API Java con Maven

```bash
mvn clean spring-boot:run
```

La API queda disponible en:

```text
http://localhost:8080
```

Probar:

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/ml/dataset/pipelines
```

Apagar solo PostgreSQL:

```bash
docker compose -f docker-compose.postgres.yml down
```

Borrar datos persistidos:

```bash
docker compose -f docker-compose.postgres.yml down -v
```

---

# Opción C: construir y ejecutar solo el Dockerfile de la API

Usa esta opción si PostgreSQL ya está levantado previamente, por ejemplo con:

```bash
docker compose -f docker-compose.postgres.yml up -d
```

## 1. Construir imagen Docker de la API

```bash
docker build -t pipelinepredict-api:local .
```

## 2. Ejecutar contenedor de la API

En macOS y Windows Docker Desktop, la API puede conectarse a PostgreSQL del host usando `host.docker.internal`:

```bash
docker run --rm --name pipelinepredict-api \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/pipelinepredict_db?currentSchema=pipelinepredict" \
  -e SPRING_DATASOURCE_USERNAME="pipelinepredict" \
  -e SPRING_DATASOURCE_PASSWORD="pipelinepredict123" \
  pipelinepredict-api:local
```

Probar:

```bash
curl http://localhost:8080/api/health
```

Detener el contenedor si lo ejecutaste en segundo plano:

```bash
docker stop pipelinepredict-api
```

En Linux puede ser necesario agregar el host gateway:

```bash
docker run --rm --name pipelinepredict-api \
  --add-host=host.docker.internal:host-gateway \
  -p 8080:8080 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://host.docker.internal:5432/pipelinepredict_db?currentSchema=pipelinepredict" \
  -e SPRING_DATASOURCE_USERNAME="pipelinepredict" \
  -e SPRING_DATASOURCE_PASSWORD="pipelinepredict123" \
  pipelinepredict-api:local
```

---

# Opción D: ejecutar solo la API con docker-compose.yml

Este archivo levanta solo el contenedor de la API Java y asume que PostgreSQL ya está disponible en `127.0.0.1:5432` desde la máquina host.

Primero levanta PostgreSQL:

```bash
docker compose -f docker-compose.postgres.yml up -d
```

Luego levanta solo la API:

```bash
docker compose up --build -d
```

Probar:

```bash
curl http://localhost:8080/api/health
curl http://localhost:8080/api/ml/dataset/pipelines
```

Ver logs:

```bash
docker logs pipelinepredict-api --tail=120
```

Apagar solo la API:

```bash
docker compose down
```

Apagar PostgreSQL:

```bash
docker compose -f docker-compose.postgres.yml down
```

---

## Datos de conexión PostgreSQL

```text
Host local: 127.0.0.1
Puerto: 5432
Base de datos: pipelinepredict_db
Usuario: pipelinepredict
Password: pipelinepredict123
Schema: pipelinepredict
```

Conexión con `psql`:

```bash
psql "postgresql://pipelinepredict:pipelinepredict123@127.0.0.1:5432/pipelinepredict_db"
```

Listar tablas:

```sql
SET search_path TO pipelinepredict;
\dt
```

Consultar dataset ML:

```sql
SELECT * FROM vw_ml_pipeline_dataset LIMIT 5;
```

---

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

---

## Probar endpoints

Puedes probarlos uno por uno:

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

O ejecuta el script:

```bash
chmod +x scripts/test_endpoints.sh
./scripts/test_endpoints.sh
```

---

## Guardar predicción enviada por Python

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

---

## Consumo desde Python sin conectar a PostgreSQL

Python debe consumir solo la API Java:

```python
import requests
import pandas as pd

response = requests.get("http://localhost:8080/api/ml/dataset/pipelines", timeout=10)
response.raise_for_status()

df = pd.DataFrame(response.json()["data"])
print(df.head())
```

Ejecutar ejemplo incluido:

```bash
python examples/python/consume_java_api.py
```

Enviar predicción desde Python:

```bash
python examples/python/send_prediction_to_java.py
```

---

## Comandos útiles de Docker

Ver contenedores:

```bash
docker ps
```

Ver todos los contenedores, incluso detenidos:

```bash
docker ps -a
```

Ver logs PostgreSQL:

```bash
docker logs pipelinepredict-postgres --tail=120
```

Ver logs API:

```bash
docker logs pipelinepredict-api --tail=120
```

Entrar a PostgreSQL dentro del contenedor:

```bash
docker exec -it pipelinepredict-postgres psql -U pipelinepredict -d pipelinepredict_db
```

Listar roles:

```bash
docker exec -it pipelinepredict-postgres psql -U pipelinepredict -d pipelinepredict_db -c "\\du"
```

Listar tablas:

```bash
docker exec -it pipelinepredict-postgres psql -U pipelinepredict -d pipelinepredict_db -c "\\dt pipelinepredict.*"
```

Borrar todo y reconstruir:

```bash
docker compose -f docker-compose.full.yml down -v
docker compose -f docker-compose.full.yml up --build -d
```

---

## Solución de problemas frecuentes

### 1. El puerto 5432 está ocupado

Verifica si tienes otro PostgreSQL ejecutándose:

```bash
lsof -i :5432
```

Detén el servicio local o cambia el puerto en el compose, por ejemplo:

```yaml
ports:
  - "5433:5432"
```

Si cambias a `5433`, actualiza la URL local:

```text
jdbc:postgresql://127.0.0.1:5433/pipelinepredict_db?currentSchema=pipelinepredict
```

### 2. Error `role pipelinepredict does not exist`

Borra el volumen y reconstruye la BD:

```bash
docker compose -f docker-compose.full.yml down -v
docker compose -f docker-compose.full.yml up --build -d
```

### 3. Error de conexión desde API a PostgreSQL

Si usas `docker-compose.full.yml`, la API debe conectarse con el host interno `postgres`:

```text
jdbc:postgresql://postgres:5432/pipelinepredict_db?currentSchema=pipelinepredict
```

Si ejecutas API en Docker y PostgreSQL en tu host, usa:

```text
jdbc:postgresql://host.docker.internal:5432/pipelinepredict_db?currentSchema=pipelinepredict
```

Si ejecutas API local con Maven y PostgreSQL en Docker, usa:

```text
jdbc:postgresql://127.0.0.1:5432/pipelinepredict_db?currentSchema=pipelinepredict
```

### 4. Warning `version is obsolete`

Docker Compose puede mostrar un warning si un archivo tiene `version`. No bloquea la ejecución. En este proyecto los compose actuales no requieren esa propiedad.

---

## Nota académica

Este MVP usa `JdbcTemplate` para acelerar el desarrollo y consumir directamente vistas SQL ya preparadas para Machine Learning. En una versión posterior se pueden agregar entidades JPA, seguridad JWT, integración real con GitHub Actions/Jenkins/GitLab CI, Terraform + MiniStack y comunicación HTTP hacia el módulo Python FastAPI.
