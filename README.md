# 🎬 CineTracker

CineTracker é uma aplicação web para catalogar e descobrir filmes e séries, com sistema de avaliações, listas personalizadas e um dashboard analítico para administradores.

---

## ✨ Funcionalidades

- **Catálogo de filmes e séries** com capa, sinopse, ano de lançamento, país de origem e gêneros
- **Avaliações e comentários** — usuários podem avaliar, comentar e curtir avaliações de outros
- **Listas personalizadas** — criação de listas para organizar o que assistiu ou quer assistir
- **Exploração por gênero** com filtros dinâmicos
- **Plataformas de streaming disponíveis** via integração com a API do TMDB (Netflix, Prime Video etc.)
- **Perfil de usuário** com histórico de avaliações e listas
- **Painel administrativo** para gerenciar filmes, séries e usuários
- **Dashboard analítico** com estatísticas de uso, avaliações e distribuição de gêneros

---

## 🛠️ Tecnologias

### Backend (API Principal)
| Tecnologia | Versão | Finalidade |
|---|---|---|
| Java | 21 LTS | Linguagem principal |
| Spring Boot | 3.4.3 | Framework web (REST API) |
| Spring JDBC | — | Acesso ao banco de dados |
| Flyway | — | Versionamento e migrations do banco |
| MySQL Connector/J | — | Driver de conexão com MySQL |
| Maven | — | Gerenciamento de dependências |

### Backend (Dashboard Analítico)
| Tecnologia | Versão | Finalidade |
|---|---|---|
| Python | 3.12 | Linguagem |
| Flask | 3.0.3 | Micro-framework web |
| Flask-CORS | 4.0.1 | Suporte a requisições cross-origin |
| SQLAlchemy | 2.0.30 | ORM / queries analíticas |
| PyMySQL | 1.1.0 | Conector MySQL para Python |
| Pandas / NumPy / SciPy | — | Processamento e estatísticas |
| python-dotenv | 1.0.1 | Gerenciamento de variáveis de ambiente |

### Frontend
- **HTML5, CSS3 e JavaScript** — interfaces dinâmicas e responsivas
- Consumo da API via `fetch` (AJAX assíncrono)

### APIs Externas
- **OMDB API** — metadados de filmes e séries (título, pôster, sinopse)
- **TMDB API** — plataformas de streaming disponíveis por região (BR)

---

## 🗄️ Banco de Dados

O banco de dados é gerenciado pelo **Flyway** com 26 migrations versionadas. As principais entidades são:

- `USUARIO` / `ADMIN` — cadastro e autenticação
- `FILME` / `SERIE` — catálogo de mídias
- `TEMPORADA` / `EPISODIO` — estrutura de séries
- `GENERO` — classificação por gênero
- `AVALIACAO` — notas e comentários dos usuários
- `LISTA` — listas personalizadas
- `SEGUE` — relacionamento social entre usuários
- Views, índices, procedures, triggers e logs de auditoria

---

## ⚙️ Pré-requisitos

- **Java 21 LTS** — [Download](https://www.oracle.com/br/java/technologies/downloads/)
- **MySQL** rodando localmente — [Download](https://dev.mysql.com/downloads/)
- **Python 3.12+** — [Download](https://www.python.org/downloads/)
- **Maven** (ou use o wrapper `./mvnw` incluído no projeto)
- Chaves de API: [OMDB](https://www.omdbapi.com/apikey.aspx) e [TMDB](https://developer.themoviedb.org/)

---

## 🚀 Como Rodar

### 1. Banco de Dados

Crie o banco de dados no MySQL:

```sql
CREATE DATABASE cinetracker;
```

As tabelas serão criadas automaticamente pelo Flyway na primeira execução.

---

### 2. Configurar o Backend Java

Na pasta `src/main/resources/`, crie o arquivo `application.properties` com base no exemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cinetracker
spring.datasource.username=SEU_USUARIO_DO_MYSQL
spring.datasource.password=SUA_SENHA_DO_MYSQL
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
omdb.api.key=SUA_CHAVE_OMDB
tmdb.api.key=SUA_CHAVE_TMDB
```

> ⚠️ Se a porta do seu MySQL não for `3306` (padrão), ajuste a URL acima.

---

### 3. Configurar o Dashboard (Python/Flask)

Na pasta `dashboard/`, crie o arquivo `.env` com base no exemplo:

```env
DB_HOST=localhost
DB_PORT=3306
DB_USER=SEU_USUARIO
DB_PASSWORD=SUA_SENHA
DB_NAME=cinetracker

FLASK_ENV=development
FLASK_DEBUG=True
SECRET_KEY=SUA_CHAVE_SECRETA
```

Em seguida, instale as dependências Python:

```bash
cd dashboard
python -m venv venv

# Linux/Mac:
source venv/bin/activate

# Windows:
venv\Scripts\activate

pip install -r requirements.txt
```

---

### 4. Iniciar a Aplicação

Com tudo configurado, execute na raiz do projeto:

```bash
.\start.bat
```

Acesse no navegador:

```
http://localhost:8080/index.html
```

O dashboard analítico (somente para admins) fica disponível em:

```
http://localhost:5000
```

---

## 📁 Estrutura do Projeto

```
cinetracker/
├── src/
│   └── main/
│       ├── java/com/bd/cinetracker/
│       │   ├── controller/       # Endpoints REST (Filme, Serie, Usuario, Admin, Avaliacao, Lista...)
│       │   ├── model/            # Entidades (Filme, Serie, Usuario, Avaliacao, Episodio...)
│       │   ├── repository/       # Acesso ao banco via JDBC
│       │   ├── service/          # Integrações externas (OMDB, TMDB)
│       │   └── DTOs/             # Objetos de transferência de dados
│       └── resources/
│           ├── application.properties
│           ├── db/migration/     # Scripts Flyway (V1 a V26)
│           └── static/           # Frontend (HTML, CSS, JS)
│               ├── index.html
│               ├── home.html
│               ├── detalhes.html
│               ├── perfil.html
│               ├── admin.html
│               └── js/ e css/
├── dashboard/
│   ├── backend/
│   │   ├── app.py                # Entrada do Flask
│   │   ├── routes/dashboard.py   # Rotas analíticas
│   │   ├── models/db.py          # Conexão com banco
│   │   └── utils/stats.py        # Cálculos estatísticos
│   ├── frontend/
│   │   ├── index.html
│   │   ├── js/dashboard.js
│   │   └── css/style.css
│   └── requirements.txt
├── pom.xml
└── start.bat
```

---

## 🔌 Principais Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/usuario/cadastro` | Cadastra novo usuário |
| `POST` | `/usuario/login` | Autenticação |
| `GET` | `/filmes` | Lista todos os filmes |
| `GET` | `/series` | Lista todas as séries |
| `POST` | `/avaliacoes` | Registra uma avaliação |
| `PUT` | `/avaliacoes/{id}` | Atualiza uma avaliação |
| `DELETE` | `/avaliacoes/{id}` | Remove uma avaliação |
| `POST` | `/avaliacoes/{id}/curtir` | Curte/descurte uma avaliação |
| `GET` | `/lista/{id}` | Busca listas de um usuário |
| `POST` | `/admin/novo-admin` | Cria um administrador |
| `POST` | `/admin/filmes` | Adiciona filme via OMDB |

---

## 📊 Dashboard Analítico

Acessível apenas para administradores, o dashboard exibe:

- Total de usuários, filmes, séries e avaliações cadastradas
- Média geral de avaliações
- Distribuição de avaliações por gênero
- Ranking de mídias mais bem avaliadas
- Evolução de cadastros ao longo do tempo

---

## 🤝 Contribuindo

1. Faça um fork do repositório
2. Crie uma branch para sua feature: `git checkout -b feature/minha-feature`
3. Commit suas alterações: `git commit -m 'feat: minha nova feature'`
4. Push para a branch: `git push origin feature/minha-feature`
5. Abra um Pull Request

---

## 📄 Licença

Este projeto foi desenvolvido para fins acadêmicos.
