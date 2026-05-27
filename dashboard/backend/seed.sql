-- ============================================================
-- CineTracker — SEED ATUALIZADO (BLINDADO)
-- Gerado com base no banco real (banco_convertido.db)
--
-- O que JÁ EXISTE no banco (NÃO recriado aqui):
--   • 30 filmes  (IDs 1–30)
--   • 30 séries  (IDs 1–30)
--   • 120 temporadas
--   • 148 episódios
--   • 1 telefone (ID 1)
--   • 1 admin    (ID 1)
-- ============================================================

USE cinetracker;

-- ─────────────────────────────────────────────
-- TELEFONES
-- ─────────────────────────────────────────────
INSERT IGNORE INTO telefone (TELEFONE_PK, TELEFONE) VALUES
(2, '81988880001'),
(3, '81988880002'),
(4, '81988880003'),
(5, '81988880004'),
(6, '81988880005'),
(7, '81988880006'),
(8, '81988880007'),
(9, '81988880008');

-- ─────────────────────────────────────────────
-- USUÁRIOS
-- ─────────────────────────────────────────────
INSERT IGNORE INTO usuario (ID_USER, NOME, EMAIL, SENHA, DT_CADASTRO, FK_TELEFONE_TELEFONE_PK) VALUES
(1, 'Ana Lima',    'ana@email.com',   '$2b$12$hash1', '2022-01-15', 2),
(2, 'Bruno Souza', 'bruno@email.com', '$2b$12$hash2', '2022-03-22', 3),
(3, 'Carla Melo',  'carla@email.com', '$2b$12$hash3', '2022-06-10', 4),
(4, 'Diego Ramos', 'diego@email.com', '$2b$12$hash4', '2023-01-05', 5),
(5, 'Elisa Nunes', 'elisa@email.com', '$2b$12$hash5', '2023-07-18', 6),
(6, 'Fábio Costa', 'fabio@email.com', '$2b$12$hash6', '2023-09-30', 7),
(7, 'Gabi Torres', 'gabi@email.com',  '$2b$12$hash7', '2024-02-11', 8),
(8, 'Hugo Pires',  'hugo@email.com',  '$2b$12$hash8', '2024-05-20', 9);

-- ─────────────────────────────────────────────
-- PLATAFORMAS
-- ─────────────────────────────────────────────
INSERT IGNORE INTO plataforma (ID_PLATAFORMA, NOME, URL) VALUES
(1, 'Netflix',      'https://netflix.com'),
(2, 'Amazon Prime', 'https://primevideo.com'),
(3, 'Disney+',      'https://disneyplus.com'),
(4, 'HBO Max',      'https://hbomax.com'),
(5, 'Apple TV+',    'https://tv.apple.com');

-- ─────────────────────────────────────────────
-- GÊNEROS
-- ─────────────────────────────────────────────
INSERT IGNORE INTO genero (NOME_GENERO) VALUES
('Ação'),
('Animação'),
('Aventura'),
('Biografia'),
('Comédia'),
('Crime'),
('Drama'),
('Fantasia'),
('Ficção Científica'),
('Musical'),
('Romance'),
('Suspense'),
('Terror'),
('Thriller');

-- ─────────────────────────────────────────────
-- PERTENCER — filmes → gêneros
-- ─────────────────────────────────────────────
INSERT IGNORE INTO pertencer (NOME_GENERO, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA) VALUES
-- Mean Girls (1)
('Comédia',           1, NULL),
('Drama',             1, NULL),
-- Avatar (2)
('Ação',              2, NULL),
('Aventura',          2, NULL),
('Ficção Científica', 2, NULL),
-- Pearl (3)
('Terror',            3, NULL),
('Thriller',          3, NULL),
-- KPop Demon Hunters (4)
('Ação',              4, NULL),
('Fantasia',          4, NULL),
-- Past Lives (5)
('Drama',             5, NULL),
('Romance',           5, NULL),
-- Eu Ainda Estou Aqui (6)
('Drama',             6, NULL),
('Biografia',         6, NULL),
-- Dead Poets Society (7)
('Drama',             7, NULL),
-- Coraline (8)
('Animação',          8, NULL),
('Fantasia',          8, NULL),
-- Clueless (9)
('Comédia',           9, NULL),
('Romance',           9, NULL),
-- Batman Begins (10)
('Ação',             10, NULL),
('Aventura',         10, NULL),
-- How to Lose a Guy in 10 Days (12)
('Comédia',          12, NULL),
('Romance',          12, NULL),
-- My Neighbor Totoro (13)
('Animação',         13, NULL),
('Aventura',         13, NULL),
-- Ponyo (14)
('Animação',         14, NULL),
('Aventura',         14, NULL),
-- Twilight (15)
('Drama',            15, NULL),
('Romance',          15, NULL),
('Fantasia',         15, NULL),
-- The Phantom of the Opera (16)
('Drama',            16, NULL),
('Musical',          16, NULL),
('Romance',          16, NULL),
-- Kill Bill Vol. 1 (17)
('Ação',             17, NULL),
('Crime',            17, NULL),
-- Black Swan (18)
('Drama',            18, NULL),
('Thriller',         18, NULL),
-- Superman (20)
('Ação',             20, NULL),
('Aventura',         20, NULL),
('Ficção Científica',20, NULL),
-- Get Out (21)
('Terror',           21, NULL),
('Thriller',         21, NULL),
-- The Hangover (22)
('Comédia',          22, NULL),
-- Inception (23)
('Ação',             23, NULL),
('Ficção Científica',23, NULL),
('Thriller',         23, NULL),
-- Parasite (24)
('Drama',            24, NULL),
('Crime',            24, NULL),
('Thriller',         24, NULL),
-- Beauty and the Beast (25)
('Animação',         25, NULL),
('Musical',          25, NULL),
('Romance',          25, NULL),
-- Sinners (26)
('Ação',             26, NULL),
('Drama',            26, NULL),
-- Forrest Gump (27)
('Drama',            27, NULL),
('Romance',          27, NULL),
-- Fight Club (28)
('Drama',            28, NULL),
('Thriller',         28, NULL),
-- The Dark Knight (29)
('Ação',             29, NULL),
('Crime',            29, NULL),
('Thriller',         29, NULL),
-- The Godfather (30)
('Crime',            30, NULL),
('Drama',            30, NULL);

-- PERTENCER — séries → gêneros
INSERT IGNORE INTO pertencer (NOME_GENERO, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA) VALUES
-- The Bear (1)
('Drama',            NULL, 1),
('Comédia',          NULL, 1),
-- Anne with an E (2)
('Drama',            NULL, 2),
('Romance',          NULL, 2),
-- Mindhunter (3)
('Crime',            NULL, 3),
('Thriller',         NULL, 3),
-- Breaking Bad (4)
('Crime',            NULL, 4),
('Drama',            NULL, 4),
('Thriller',         NULL, 4),
-- You (5)
('Drama',            NULL, 5),
('Thriller',         NULL, 5),
('Romance',          NULL, 5),
-- Chernobyl (6)
('Drama',            NULL, 6),
('Biografia',        NULL, 6),
-- O Gambito da Rainha (7)
('Drama',            NULL, 7),
-- The Witcher (8)
('Ação',             NULL, 8),
('Fantasia',         NULL, 8),
-- Arcane (10)
('Animação',         NULL, 10),
('Ação',             NULL, 10),
('Ficção Científica',NULL, 10),
-- IT: Bem-vindo a Derry (11)
('Terror',           NULL, 11),
('Thriller',         NULL, 11),
-- Euphoria (12)
('Drama',            NULL, 12),
-- The Last of Us (13)
('Ação',             NULL, 13),
('Drama',            NULL, 13),
('Aventura',         NULL, 13),
-- Supernatural (14)
('Ação',             NULL, 14),
('Fantasia',         NULL, 14),
('Terror',           NULL, 14),
-- Gossip Girl (15)
('Drama',            NULL, 15),
('Romance',          NULL, 15),
-- Batman do Futuro (17)
('Ação',             NULL, 17),
('Animação',         NULL, 17),
('Ficção Científica',NULL, 17),
-- True Detective (18)
('Crime',            NULL, 18),
('Drama',            NULL, 18),
('Thriller',         NULL, 18),
-- Death Note (19)
('Animação',         NULL, 19),
('Crime',            NULL, 19),
('Thriller',         NULL, 19),
-- Attack on Titan (20)
('Ação',             NULL, 20),
('Animação',         NULL, 20),
('Drama',            NULL, 20),
-- Jujutsu Kaisen (21)
('Ação',             NULL, 21),
('Animação',         NULL, 21),
('Fantasia',         NULL, 21),
-- Fullmetal Alchemist (22)
('Ação',             NULL, 22),
('Animação',         NULL, 22),
('Aventura',         NULL, 22),
-- Hunter x Hunter (23)
('Ação',             NULL, 23),
('Animação',         NULL, 23),
('Aventura',         NULL, 23),
-- Twin Peaks (24)
('Crime',            NULL, 24),
('Drama',            NULL, 24),
('Thriller',         NULL, 24),
-- Daredevil (25)
('Ação',             NULL, 25),
('Crime',            NULL, 25),
-- Dark (27)
('Drama',            NULL, 27),
('Ficção Científica',NULL, 27),
('Thriller',         NULL, 27),
-- Modern Family (28)
('Comédia',          NULL, 28),
-- Game of Thrones (29)
('Ação',             NULL, 29),
('Aventura',         NULL, 29),
('Drama',            NULL, 29),
('Fantasia',         NULL, 29),
-- Rick and Morty (30)
('Animação',         NULL, 30),
('Aventura',         NULL, 30),
('Ficção Científica',NULL, 30),
('Comédia',          NULL, 30);

-- ─────────────────────────────────────────────
-- DISPONÍVEL — filmes e séries em plataformas
-- ─────────────────────────────────────────────
INSERT IGNORE INTO disponivel (FK_PLATAFORMA_ID_PLATAFORMA, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA) VALUES
-- Netflix
(1, 1,  NULL), -- Mean Girls
(1, 18, NULL), -- Black Swan
(1, 21, NULL), -- Get Out
(1, 24, NULL), -- Parasite
(1, 28, NULL), -- Fight Club
-- Amazon Prime
(2, 7,  NULL), -- Dead Poets Society
(2, 17, NULL), -- Kill Bill Vol. 1
(2, 22, NULL), -- The Hangover
(2, 27, NULL), -- Forrest Gump
-- Disney+
(3, 2,  NULL), -- Avatar
(3, 8,  NULL), -- Coraline
(3, 13, NULL), -- My Neighbor Totoro
(3, 14, NULL), -- Ponyo
(3, 25, NULL), -- Beauty and the Beast
-- HBO Max
(4, 10, NULL), -- Batman Begins
(4, 23, NULL), -- Inception
(4, 29, NULL), -- The Dark Knight
(4, 30, NULL), -- The Godfather
-- Apple TV+
(5, 5,  NULL), -- Past Lives
(5, 6,  NULL), -- Eu Ainda Estou Aqui

-- Séries
(1, NULL, 1),  -- The Bear
(1, NULL, 4),  -- Breaking Bad
(1, NULL, 5),  -- You
(1, NULL, 12), -- Euphoria
(1, NULL, 13), -- The Last of Us
(4, NULL, 6),  -- Chernobyl
(4, NULL, 18), -- True Detective
(4, NULL, 29), -- Game of Thrones
(3, NULL, 8),  -- The Witcher
(3, NULL, 10), -- Arcane
(3, NULL, 20), -- Attack on Titan
(2, NULL, 2),  -- Anne with an E
(2, NULL, 3),  -- Mindhunter
(2, NULL, 7),  -- O Gambito da Rainha
(1, NULL, 19), -- Death Note
(1, NULL, 21), -- Jujutsu Kaisen
(1, NULL, 23), -- Hunter x Hunter
(4, NULL, 27), -- Dark
(5, NULL, 11); -- IT: Bem-vindo a Derry

-- ─────────────────────────────────────────────
-- PESSOAS (atores / diretores)
-- ─────────────────────────────────────────────
INSERT IGNORE INTO pessoa (ID_PESSOA, NOME, PESSOA_TIPO) VALUES
(1,  'Christopher Nolan',    2), -- diretor
(2,  'Bong Joon-ho',         2), -- diretor
(3,  'Quentin Tarantino',    2), -- diretor
(4,  'James Cameron',        2), -- diretor
(5,  'Darren Aronofsky',     2), -- diretor
(6,  'Cillian Murphy',       1), -- ator
(7,  'Natalie Portman',      1), -- atriz
(8,  'Keanu Reeves',         1), -- ator
(9,  'Song Kang-ho',         1), -- ator
(10, 'Leonardo DiCaprio',    1), -- ator
(11, 'Vince Gilligan',       2), -- diretor (criador BB)
(12, 'Bryan Cranston',       1), -- ator
(13, 'Pedro Pascal',         1), -- ator
(14, 'Anna Torv',            1), -- atriz
(15, 'Jeremy Allen White',   1); -- ator

-- ─────────────────────────────────────────────
-- ATUAR
-- ─────────────────────────────────────────────
INSERT IGNORE INTO atuar (FK_PESSOA_ID_PESSOA, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA) VALUES
(6,  10,   NULL), -- Cillian Murphy → Batman Begins
(7,  18,   NULL), -- Natalie Portman → Black Swan
(10, 23,   NULL), -- DiCaprio → Inception
(9,  24,   NULL), -- Song Kang-ho → Parasite
(12, NULL, 4),    -- Bryan Cranston → Breaking Bad
(13, NULL, 13),   -- Pedro Pascal → The Last of Us
(14, NULL, 13),   -- Anna Torv → The Last of Us
(15, NULL, 1);    -- Jeremy Allen White → The Bear

-- ─────────────────────────────────────────────
-- DIRIGIR
-- ─────────────────────────────────────────────
INSERT IGNORE INTO dirigir (FK_PESSOA_ID_PESSOA, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA) VALUES
(1, 23, NULL), -- Nolan → Inception
(1, 29, NULL), -- Nolan → The Dark Knight
(1, 10, NULL), -- Nolan → Batman Begins
(2, 24, NULL), -- Bong → Parasite
(3, 17, NULL), -- Tarantino → Kill Bill
(4,  2, NULL), -- Cameron → Avatar
(5, 18, NULL), -- Aronofsky → Black Swan
(11, NULL, 4); -- Vince Gilligan → Breaking Bad

-- ─────────────────────────────────────────────
-- LISTAS
-- ─────────────────────────────────────────────
INSERT IGNORE INTO lista (ID_LISTA, NOME_LISTA) VALUES
(1, 'Favoritos da Ana'),
(2, 'Quero Assistir — Bruno'),
(3, 'Melhores Animações'),
(4, 'Maratona Suspense');

-- CRIA (usuário → lista)
INSERT IGNORE INTO cria (FK_LISTA_ID_LISTA, FK_USUARIO_ID_USER) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 4);

-- CONTEM (lista → filme ou série)
INSERT IGNORE INTO contem (FK_LISTA_ID_LISTA, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA) VALUES
-- Favoritos da Ana
(1, 23,   NULL), -- Inception
(1, 27,   NULL), -- Forrest Gump
(1, NULL, 4),    -- Breaking Bad
(1, NULL, 13),   -- The Last of Us
-- Quero Assistir — Bruno
(2, 24,   NULL), -- Parasite
(2, 30,   NULL), -- The Godfather
(2, NULL, 6),    -- Chernobyl
(2, NULL, 27),   -- Dark
-- Melhores Animações
(3, 8,    NULL), -- Coraline
(3, 13,   NULL), -- My Neighbor Totoro
(3, 25,   NULL), -- Beauty and the Beast
(3, NULL, 10),   -- Arcane
(3, NULL, 19),   -- Death Note
(3, NULL, 20),   -- Attack on Titan
-- Maratona Suspense
(4, 28,   NULL), -- Fight Club
(4, 29,   NULL), -- The Dark Knight
(4, NULL, 3),    -- Mindhunter
(4, NULL, 18),   -- True Detective
(4, NULL, 24);   -- Twin Peaks

-- ─────────────────────────────────────────────
-- SEGUIR
-- ─────────────────────────────────────────────
INSERT IGNORE INTO segue (FK_USUARIO_ID_USER, FK_USUARIO_ID_USER_) VALUES
(1, 2), (1, 3), (2, 1),
(3, 1), (4, 2), (5, 1),
(6, 3), (7, 4), (8, 5);

-- ─────────────────────────────────────────────
-- AVALIAÇÕES
-- ─────────────────────────────────────────────
INSERT IGNORE INTO avaliacao
  (ID_AVALIACAO, FK_USUARIO_ID_USER, FK_FILME_ID_MIDIA, FK_SERIE_ID_MIDIA,
   NOTA, COMENTARIO, DATA_AVALIACAO)
VALUES
-- 2022
(1,  1, 23,   NULL, 9.5, 'Obra-prima absoluta, assisti três vezes.',            '2022-02-14 20:00:00'),
(2,  2, 29,   NULL, 9.5, 'O Coringa do Heath Ledger é inesquecível.',           '2022-03-20 21:00:00'),
(3,  3, NULL, 4,    9.0, 'Breaking Bad mudou o que eu penso sobre séries.',      '2022-04-10 19:00:00'),
(4,  4, 27,   NULL, 9.0, 'Forrest Gump é um abraço em formato de filme.',       '2022-05-05 15:00:00'),
(5,  5, 18,   NULL, 8.5, 'Natalie Portman entregou tudo nesse filme.',          '2022-06-22 22:00:00'),
(6,  6, NULL, 6,    9.5, 'Chernobyl me deixou sem dormir de tanta tensão.',     '2022-07-30 20:30:00'),
(7,  7, 24,   NULL, 9.0, 'Parasite mereceu o Oscar mais do que qualquer outro.','2022-08-18 18:00:00'),
(8,  8, 30,   NULL, 9.5, 'Entendo por que é considerado o melhor filme já feito.','2022-09-25 20:00:00'),

-- 2023
(9,  1, NULL, 13,   9.5, 'The Last of Us fez o que nenhuma adaptação conseguiu.','2023-01-22 21:00:00'),
(10, 2, 28,   NULL, 8.5, 'Fight Club ainda é relevante hoje em dia.',           '2023-02-14 20:00:00'),
(11, 3, NULL, 18,   9.0, 'True Detective T1 é melhor que muita coisa no cinema.','2023-03-30 19:00:00'),
(12, 4, NULL, 27,   9.5, 'Dark é a melhor série de ficção científica já feita.','2023-04-12 22:00:00'),
(13, 5, NULL, 10,   9.0, 'Arcane é o que toda animação deveria almejar.',       '2023-05-20 18:00:00'),
(14, 6, NULL, 1,    8.5, 'The Bear me estressou no melhor sentido possível.',   '2023-06-08 21:00:00'),
(15, 7, 17,   NULL, 8.5, 'Kill Bill é cinematografia virou arte.',              '2023-07-15 20:00:00'),
(16, 8, NULL, 3,    8.5, 'Mindhunter deveria ter tido uma terceira temporada.', '2023-08-28 19:00:00'),
(17, 1, NULL, 29,   8.0, 'Game of Thrones foi incrível até onde foi incrível.', '2023-09-10 22:00:00'),
(18, 2, 5,    NULL, 8.5, 'Past Lives me fez questionar muitas coisas.',         '2023-10-03 21:00:00'),
(19, 3, 21,   NULL, 8.0, 'Get Out é um thriller que vai além do gênero.',       '2023-11-19 20:00:00'),
(20, 4, NULL, 19,   8.5, 'Death Note é o anime que coloco para quem não assiste anime.','2023-12-05 18:00:00'),

-- 2024
(21, 5, NULL, 20,   9.0, 'Attack on Titan entregou o final que merecia.',       '2024-01-15 21:00:00'),
(22, 6, NULL, 23,   9.5, 'Hunter x Hunter é a melhor obra de ficção que conheço.','2024-02-20 20:00:00'),
(23, 7, NULL, 24,   8.5, 'Twin Peaks é de outro planeta, no bom sentido.',      '2024-03-14 19:00:00'),
(24, 8, 2,    NULL, 8.0, 'Avatar continua bonito depois de tantos anos.',       '2024-04-22 22:00:00'),
(25, 1, NULL, 4,    9.0, 'Revi Breaking Bad e ficou ainda melhor.',             '2024-05-08 20:00:00'),
(26, 2, 10,   NULL, 8.5, 'Batman Begins ainda é o melhor filme de origem.',     '2024-06-17 21:00:00'),
(27, 3, NULL, 2,    8.5, 'Anne with an E merecia ter continuado.',              '2024-07-30 19:00:00'),
(28, 4, 7,    NULL, 8.5, 'Dead Poets Society deveria ser obrigatório nas escolas.','2024-08-12 22:00:00'),
(29, 5, 20,   NULL, 7.5, 'Superman (2025) foi uma grata surpresa.',             '2024-09-25 18:00:00'),
(30, 6, NULL, 7,    8.5, 'O Gambito da Rainha prendeu minha atenção do início ao fim.','2024-10-14 20:00:00'),

-- 2025
(31, 7, NULL, 11,   8.0, 'IT: Bem-vindo a Derry trouxe o terror de volta à TV.','2025-01-20 21:00:00'),
(32, 8, NULL, 25,   8.5, 'Daredevil é a melhor série de super-herói da Marvel.','2025-02-10 19:00:00'),
(33, 1, 26,   NULL, 7.5, 'Sinners foi uma experiência única no cinema.',        '2025-03-15 20:00:00'),
(34, 2, NULL, 30,   8.5, 'Rick and Morty cansa mas nunca decepciona.',          '2025-04-22 22:00:00'),
(35, 3, NULL, 22,   8.5, '2003 ou Brotherhood, ambos são excelentes.',          '2025-05-01 18:00:00'),
(36, 4, 4,    NULL, 7.0, 'KPop Demon Hunters foi uma surpresa positiva.',       '2025-05-10 21:00:00'),
(37, 5, NULL, 21,   8.5, 'Jujutsu Kaisen tem animação de dar inveja.',          '2025-05-18 19:00:00'),
(38, 6, 6,    NULL, 9.5, 'Eu Ainda Estou Aqui deveria ter ganho o Oscar.',      '2025-05-20 22:00:00');

-- ─────────────────────────────────────────────
-- INTERAGIR (usuários curtindo avaliações)
-- ─────────────────────────────────────────────
INSERT IGNORE INTO interagir (FK_USUARIO_ID_USER, FK_AVALIACAO_ID) VALUES
(2, 1), (3, 1), (4, 1),
(1, 2), (5, 2),
(1, 3), (2, 3), (7, 3),
(5, 6), (6, 6),
(2, 9), (3, 9), (8, 9),
(1, 12),(4, 12),
(3, 22),(5, 22),(7, 22),
(6, 38),(7, 38),(8, 38);

-- ─────────────────────────────────────────────
-- LOG DE AVALIAÇÕES (histórico de edições)
-- ─────────────────────────────────────────────
INSERT IGNORE INTO log_avaliacao (ID_LOG, ID_AVALIACAO, NOTA_ANTIGA, NOTA_NOVA, DATA_ALTERACAO) VALUES
(1, 17, 7.0, 8.0, '2023-09-12 14:30:00'), -- Ana editou avaliação de GoT
(2, 25, 8.0, 9.0, '2024-05-10 10:00:00'), -- Ana editou avaliação de Breaking Bad
(3, 29, 7.0, 7.5, '2024-09-27 16:00:00'), -- Elisa ajustou nota do Superman
(4, 14, 8.0, 8.5, '2023-06-10 09:00:00'); -- Fábio ajustou nota do The Bear

SELECT 'Seed executado com sucesso! 🎬' AS status;