::windows
@echo off
echo 🎬 Iniciando CineTracker...

echo 📊 Subindo dashboard em nova janela...
start "Dashboard Flask" cmd /k "cd dashboard\backend && ..\venv\Scripts\activate && python app.py"

echo ☕ Subindo Spring Boot...
call mvnw.cmd spring-boot:run

echo 👋 Até logo!
pause