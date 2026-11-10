@echo off
rem =============================================================================
rem Script de ejecución - Módulo Integrante 1 (Java 17.0.20 + JavaFX 17.0.2)
rem =============================================================================

set LIB=lib\*
set OUT=out

if not exist "%OUT%" (
    echo ERROR: No se encontró la carpeta out/. Ejecuta primero compilar.bat
    exit /b 1
)

echo Iniciando aplicación...
java --release 17 --module-path "lib;%OUT%" --add-modules javafx.controls,javafx.fxml -cp "out;%LIB%" com.prehackaton.eventos.Main
