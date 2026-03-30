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

### 3. Rodando a Aplicação:
Depois de criar o arquivo *application.properties* e realizar as mudanças necessárias, abra a pasta *main* e depois a *java* e rode o arquivo *CinetrackerApplication.java*.

<img width="445" height="504" alt="image" src="https://github.com/user-attachments/assets/85d2a77d-d10d-40f4-8c21-7927aa581ee6" />

Depois disso basta rodar localmente o site com o link "http://localhost:8080/index.html":

<img width="1919" height="1026" alt="image" src="https://github.com/user-attachments/assets/dac336e3-9349-4048-a6c6-f73eb06bc6cc" />

AO SE CADASTRAR E ENTRAR COM SUA CONTA:

<img width="1913" height="1028" alt="image" src="https://github.com/user-attachments/assets/fc8eb853-229a-4b24-b1df-b22467a43b8a" />
