# CineTracker

Cinetracker é uma **aplicação web** desenvolvida para mostrar vários filmes e séries, contendo capa, sinopse e até avaliações dos próprios usuários dessas mídias e as plataformas em que elas estão disponiveis para serem assistidas. 

## Ferramentas Utilizadas:

**Backend:**
* **Java & Spring Boot** (Web e JDBC)
* **Maven** (Gerenciamento de dependências)
* **MySQL** (Banco de Dados Relacional)
* **Flyway** (Migrations para versionamento do banco)

**Frontend:**
* **HTML5, CSS3 e JavaScript** (Interfaces dinâmicas e responsivas)
* **Integração Assíncrona** (Consumo da API via de FETCH/AJAX)

## Como Rodar a Aplicação:

### 1. Pré-requisitos
Para rodar a aplicação é necessário ter instalado na máquina os seguintes aplicativos: 
* **Java 21 LTS**  [BAIXE AQUI](https://www.oracle.com/br/java/technologies/downloads/)
* **MySQL** (Rodar localmente e fazer o comando CREATE DATABASE cinetracker). [BAIXE AQUI](https://dev.mysql.com/downloads/)

### 2. Configurando as Variáveis de Ambiente:
Ao abrir o arquivo do projeto você verá as seguintes pastas:

<img width="437" height="366" alt="image" src="https://github.com/user-attachments/assets/9a731d10-9c3b-4a11-8d1c-6f01fd9244e0" />

Clique na pasta *main*, depois *resources* e depois abra o arquivo *"application.properties.example"*:

<img width="430" height="406" alt="image" src="https://github.com/user-attachments/assets/00279dd7-d3da-41aa-8c81-e322886ed3c1" />

Ao abrir o arquivo você verá:
```application.properties.example
spring.datasource.url=jdbc:mysql://localhost:3306/cinetracker
spring.datasource.username=SEU_USUARIO_DO_MYSQL_AQUI
spring.datasource.password=SUA_SENHA_DO_MYSQL_AQUI
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
omdb.api.key=SUA_CHAVE_OMDB_AQUI
```
Você deve copiar tudo que está escrito no arquivo e criar outro chamado *application.properties* na mesma pasta (*resources*) e preenche-lo dessa forma:

<img width="696" height="155" alt="image" src="https://github.com/user-attachments/assets/20a4887c-d6c1-4985-b014-85eb1cc0ac5d" />

*AVISO* CASO SEU LOCAL HOST NÃO SEJA 3306 (padrão) altere! (está sublinhado de vermelho)

Depois, deve accesar a pasta *dashboard* e realizar as seguintes ações:

<img width="347" height="360" alt="Captura de tela 2026-05-28 004450" src="https://github.com/user-attachments/assets/1cdc9047-1789-4ad9-abfe-b0da2101e5af" />

1. criação da .env com base no exemplo:
```.env.example
DB_HOST=localhost
DB_PORT=3306
DB_USER=SEU_USUÁRIO
DB_PASSWORD=SUA_SENHA
DB_NAME=SEU_BANCO_DE_DADOS

FLASK_ENV=development
FLASK_DEBUG=True
SECRET_KEY=SUA_CHAVE_SECRETA
```

2. Depois disso, deve digitar os seguintes comandos no terminal: 
```
- cd dashboard
- criar venv: python -m venv venv
- ativar venv: source venv/bin/activate      # ou venv\Scripts\activate no Windows
- pip install -r requirements.txt
```




### 3. Rodando a Aplicação:
Depois de criar o arquivo *application.properties*, *.env*, e instalar tudo do requirements.txt corretamente, insira no terminal:
```
.\start.bat
```

Depois disso basta rodar localmente o site com o link "http://localhost:8080/index.html":

<img width="1919" height="1026" alt="image" src="https://github.com/user-attachments/assets/dac336e3-9349-4048-a6c6-f73eb06bc6cc" />

Ao realizar login, já pode utilizar a aplicação:

<img width="1346" height="629" alt="Captura de tela 2026-05-28 011048" src="https://github.com/user-attachments/assets/56502b71-9df6-4adf-bfe4-557c35651718" />

Ao acessar como admin, é possível acessar o dashboard:

<img width="1349" height="623" alt="Captura de tela 2026-05-28 011610" src="https://github.com/user-attachments/assets/fe934be7-49ba-4a30-89c1-1231ee300e5e" />

E é possível visualizar o dashboard: 

<img width="1352" height="624" alt="Captura de tela 2026-05-28 011807" src="https://github.com/user-attachments/assets/7edf8a4a-5d21-4955-ae44-2b7205482069" />


