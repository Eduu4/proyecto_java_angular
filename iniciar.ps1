# =========================================================
# Script de arranque para Windows PowerShell
# Levanta Docker + espera PostgreSQL + inicia JHipster
# =========================================================

Write-Host "Levantando contenedores Docker..."
docker-compose up -d

# Esperar a que PostgreSQL esté listo
Write-Host "Esperando a que PostgreSQL esté listo..."
$maxTries = 30
$try = 0
$ready = $false

while (-not $ready -and $try -lt $maxTries) {
    try {
        docker exec -i postgres-finanzas pg_isready -U angularv3 -d angularv3
        if ($LASTEXITCODE -eq 0) {
            $ready = $true
        } else {
            Start-Sleep -Seconds 2
            $try++
        }
    } catch {
        Start-Sleep -Seconds 2
        $try++
    }
}

if (-not $ready) {
    Write-Host "ERROR: PostgreSQL no respondió después de varios intentos."
    exit 1
}

Write-Host "PostgreSQL está listo. Iniciando JHipster..."
./mvnw