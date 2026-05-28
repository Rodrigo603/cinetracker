::windows
@echo off
echo 🎬 Iniciando CineTracker...

echo 📊 Verificando dependencias do Dashboard (Python)...
cd /d %~dp0


if not exist "venv\Scripts\activate.bat" (
    echo [!] Ambiente virtual nao encontrado. Criando um novo automaticamente...
    python -m venv venv
    call venv\Scripts\activate
    echo [!] Instalando dependencias do Flask...
    pip install -r dashboard\requirements.txt
) else (
    echo [OK] Ambiente virtual encontrado.
)

echo 📊 Subindo dashboard em nova janela...
start "Dashboard Flask" cmd /k "cd /d %~dp0 && call venv\Scripts\activate && cd dashboard\backend && python app.py"

echo ☕ Subindo Spring Boot...
call mvnw.cmd spring-boot:run

echo 👋 Ate logo!
pause