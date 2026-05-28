import os
from flask import Flask, send_from_directory
from flask_cors import CORS
from dotenv import load_dotenv

load_dotenv(override=True)
from routes.dashboard import dashboard_bp

app = Flask(
    __name__,
    static_folder="../frontend",
    static_url_path=""
)

CORS(app)

app.secret_key = os.getenv("SECRET_KEY", "cinetracker_dev")

app.register_blueprint(dashboard_bp)


@app.route("/")
def index():
    return send_from_directory("../frontend", "index.html")


@app.errorhandler(404)
def not_found(e):
    return {"error": "Rota não encontrada"}, 404


@app.errorhandler(500)
def server_error(e):
    return {"error": "Erro interno no servidor", "detail": str(e)}, 500


if __name__ == "__main__":
    port = int(os.getenv("PORT", 5000))
    debug = os.getenv("FLASK_DEBUG", "True") == "True"
    print(f"🎬 CineTracker rodando em http://localhost:{port}")
    app.run(host="0.0.0.0", port=port, debug=debug)