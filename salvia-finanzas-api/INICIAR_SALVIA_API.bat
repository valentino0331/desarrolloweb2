@echo off
title SERVIDOR SPRING BOOT - ECOTECHTRACK REST API (RAEE / MINAM)
color 0B
cls
echo =====================================================================
echo       INICIANDO ECOTECHTRACK API RESTFUL (SPRING BOOT 3.3 / JAVA 21)
echo       Trazabilidad y Fiscalizacion de RAEE (D.S. N 009-2019-MINAM)
echo =====================================================================
echo.
echo Puerto: 8080
echo Consola H2: http://localhost:8080/h2-console (JDBC: jdbc:h2:mem:salviadb)
echo Swagger / API Auth: http://localhost:8080/api/auth/login
echo.
echo Presiona CTRL + C en esta ventana para detener el servidor.
echo =====================================================================
echo.

"C:\Program Files\Android\Android Studio\jbr\bin\java.exe" -jar "%~dp0target\salvia-finanzas-api-0.0.1-SNAPSHOT.jar"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] No se pudo iniciar con JBR. Intentando con java por defecto...
    java -jar "%~dp0target\salvia-finanzas-api-0.0.1-SNAPSHOT.jar"
)

pause