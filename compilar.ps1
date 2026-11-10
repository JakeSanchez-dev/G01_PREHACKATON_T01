# =============================================================================
# Script de compilación - Módulo Integrante 1 (Java 17.0.20 + JavaFX 17.0.2)
# Requisito: tener los JARs necesarios en la carpeta lib/
#   - JavaFX SDK: javafx.controls.jar, javafx.fxml.jar (+ módulos nativos del SO)
#   - MySQL: mysql-connector-j-8.x.x.jar
#   - JasperReports: jasperreports-6.x.x.jar + dependencias (commons-*, groovy, itext, etc.)
# =============================================================================

$ErrorActionPreference = "Stop"

# Verificar versión de Java
$javaVersion = java -version 2>&1 | Select-Object -First 1
Write-Host "Java detectado: $javaVersion"

$LIB = "lib\*"
$OUT = "out"

if (-not (Test-Path "lib")) {
    Write-Error "No se encontró la carpeta lib/. Coloca allí los JARs de JavaFX, MySQL Connector/J y JasperReports."
}

Write-Host "Creando carpeta de salida $OUT..."
New-Item -ItemType Directory -Force -Path $OUT | Out-Null

$fuentes = Get-ChildItem -Recurse src\main\java -Filter *.java | ForEach-Object { $_.FullName }

Write-Host "Compilando con Java 17..."
javac --release 17 --module-path $LIB --add-modules javafx.controls,javafx.fxml -cp "$LIB" -d $OUT $fuentes

if ($LASTEXITCODE -eq 0) {
    Write-Host "Compilación exitosa. Clases en $OUT" -ForegroundColor Green
} else {
    Write-Error "Error de compilación."
}
