# =============================================================================
# Script de ejecución - Módulo Integrante 1 (Java 17.0.20 + JavaFX 17.0.2)
# =============================================================================

$ErrorActionPreference = "Stop"

$LIB = "lib\*"
$OUT = "out"

if (-not (Test-Path $OUT)) {
    Write-Error "No se encontró la carpeta out/. Ejecuta primero compilar.ps1"
}

Write-Host "Iniciando aplicación..."
java --release 17 --module-path "lib;$OUT" --add-modules javafx.controls,javafx.fxml -cp "out;$LIB" com.prehackaton.eventos.Main
