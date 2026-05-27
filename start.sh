#linux
#!/bin/bash

echo "🎬 Iniciando CineTracker..."

# Inicia o dashboard Flask em background
echo "📊 Subindo dashboard (http://localhost:5000)..."
source dashboard/venv/bin/activate
cd dashboard/backend
python app.py &
FLASK_PID=$!
cd ../..

echo "✅ Dashboard rodando (PID: $FLASK_PID)"
echo ""

# Inicia o Spring Boot em foreground (com stdin)
echo "☕ Subindo Spring Boot..."
./mvnw spring-boot:run

# Quando o Spring Boot encerrar, mata o Flask junto
echo ""
echo "🛑 Encerrando dashboard..."
kill $FLASK_PID 2>/dev/null
echo "👋 Até logo!"