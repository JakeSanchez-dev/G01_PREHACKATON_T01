@echo off
rem =============================================================================
rem Script de compilación - Módulo Integrante 1 (Java 17.0.20 + JavaFX 17.0.2)
rem Requisito: tener los JARs necesarios en la carpeta lib/
rem =============================================================================

java -version

set LIB=lib\*
set OUT=out

if not exist "lib" (
    echo ERROR: No se encontró la carpeta lib/. Coloca allí los JARs de JavaFX, MySQL Connector/J y JasperReports.
    exit /b 1
)

if not exist "%OUT%" mkdir "%OUT%"

echo Compilando con Java 17...
javac --release 17 --module-path %LIB% --add-modules javafx.controls,javafx.fxml -cp "%LIB%" -d %OUT% src\main\java\com\prehackaton\eventos\**\*.java

if %ERRORLEVEL% equ 0 (
    echo Compilación exitosa. Clases en %OUT%
) else (
    echo Error de compilación.
    exit /b %ERRORLEVEL%
)
