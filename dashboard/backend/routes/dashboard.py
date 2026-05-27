from flask import Blueprint, jsonify, request
from models.db import query
from utils.stats import calcular_estatisticas, safe_float
import unicodedata

dashboard_bp = Blueprint("dashboard", __name__, url_prefix="/api/dashboard")


def _filtro_periodo(alias: str) -> tuple[str, list]:
    """Returns WHERE clauses and params for ano_inicio / ano_fim."""
    ano_inicio = request.args.get("ano_inicio")
    ano_fim = request.args.get("ano_fim")
    conds, params = [], []
    if ano_inicio and ano_inicio != "todos":
        conds.append(f"{alias}.ANO_LANCAMENTO >= %s")
        params.append(int(ano_inicio))
    if ano_fim and ano_fim != "todos":
        conds.append(f"{alias}.ANO_LANCAMENTO <= %s")
        params.append(int(ano_fim))
    clause = (" AND " + " AND ".join(conds)) if conds else ""
    return clause, params


def _filtro_genero(genero: str | None) -> tuple[str, list]:
    if genero and genero != "todos":
        corrupted = _corrupt(genero)
        if corrupted != genero:
            return "AND p.NOME_GENERO IN (%s, %s)", [genero, corrupted]
        return "AND p.NOME_GENERO = %s", [genero]
    return "", []


def _is_clean(s: str) -> bool:
    for ch in s:
        if 0x2500 <= ord(ch) <= 0x257F:
            return False
    return True


def _corrupt(s: str) -> str:
    try:
        return s.encode("utf-8").decode("latin-1")
    except Exception:
        return s


def _clean_rows(rows: list, key: str) -> list:
    seen = set()
    result = []
    for r in rows:
        val = r.get(key, "")
        if _is_clean(val) and val not in seen:
            seen.add(val)
            result.append(r)
    return result


@dashboard_bp.route("/indicadores")
def indicadores():
    cond_f, params_f = _filtro_periodo("f")
    cond_s, params_s = _filtro_periodo("s")

    total_filmes = query(
        f"SELECT COUNT(*) AS total FROM filme f WHERE 1=1 {cond_f}", tuple(params_f)
    )[0]["total"]
    total_series = query(
        f"SELECT COUNT(*) AS total FROM serie s WHERE 1=1 {cond_s}", tuple(params_s)
    )[0]["total"]

    total_usuarios  = query("SELECT COUNT(*) AS total FROM usuario")[0]["total"]
    total_avaliacoes = query("SELECT COUNT(*) AS total FROM avaliacao")[0]["total"]

    media_nota_filmes = query(
        f"SELECT ROUND(AVG(NOTA_IMDB), 2) AS media FROM filme f WHERE NOTA_IMDB IS NOT NULL {cond_f}",
        tuple(params_f)
    )[0]["media"] or 0

    media_nota_series = query(
        f"SELECT ROUND(AVG(NOTA_IMDB), 2) AS media FROM serie s WHERE NOTA_IMDB IS NOT NULL {cond_s}",
        tuple(params_s)
    )[0]["media"] or 0

    total_plataformas = query("SELECT COUNT(*) AS total FROM plataforma")[0]["total"]
    total_episodios   = query("SELECT COUNT(*) AS total FROM episodio")[0]["total"]
    total_generos     = query("SELECT COUNT(DISTINCT NOME_GENERO) AS total FROM genero")[0]["total"]

    maior_filme = query(
        f"SELECT TITULO, NOTA_IMDB FROM filme f WHERE NOTA_IMDB IS NOT NULL {cond_f} ORDER BY NOTA_IMDB DESC LIMIT 1",
        tuple(params_f)
    )
    menor_filme = query(
        f"SELECT TITULO, NOTA_IMDB FROM filme f WHERE NOTA_IMDB IS NOT NULL {cond_f} ORDER BY NOTA_IMDB ASC LIMIT 1",
        tuple(params_f)
    )
    maior_serie = query(
        f"SELECT TITULO, NOTA_IMDB FROM serie s WHERE NOTA_IMDB IS NOT NULL {cond_s} ORDER BY NOTA_IMDB DESC LIMIT 1",
        tuple(params_s)
    )
    menor_serie = query(
        f"SELECT TITULO, NOTA_IMDB FROM serie s WHERE NOTA_IMDB IS NOT NULL {cond_s} ORDER BY NOTA_IMDB ASC LIMIT 1",
        tuple(params_s)
    )

    return jsonify({
        "total_filmes":    total_filmes,
        "total_series":    total_series,
        "total_usuarios":  total_usuarios,
        "total_avaliacoes": total_avaliacoes,
        "media_nota_filmes": float(media_nota_filmes),
        "media_nota_series": float(media_nota_series),
        "total_plataformas": total_plataformas,
        "total_episodios":   total_episodios,
        "total_generos":     total_generos,
        "maior_nota_filme": {"titulo": maior_filme[0]["TITULO"] if maior_filme else "—", "nota": float(maior_filme[0]["NOTA_IMDB"]) if maior_filme else 0},
        "menor_nota_filme": {"titulo": menor_filme[0]["TITULO"] if menor_filme else "—", "nota": float(menor_filme[0]["NOTA_IMDB"]) if menor_filme else 0},
        "maior_nota_serie": {"titulo": maior_serie[0]["TITULO"] if maior_serie else "—", "nota": float(maior_serie[0]["NOTA_IMDB"]) if maior_serie else 0},
        "menor_nota_serie": {"titulo": menor_serie[0]["TITULO"] if menor_serie else "—", "nota": float(menor_serie[0]["NOTA_IMDB"]) if menor_serie else 0},
    })


@dashboard_bp.route("/filmes-por-genero")
def filmes_por_genero():
    cond_periodo, params_periodo = _filtro_periodo("f")

    sql = f"""
        SELECT p.NOME_GENERO AS genero, COUNT(DISTINCT f.ID_MIDIA) AS total
        FROM pertencer p
        JOIN filme f ON p.FK_FILME_ID_MIDIA = f.ID_MIDIA
        WHERE p.FK_FILME_ID_MIDIA IS NOT NULL {cond_periodo}
        GROUP BY p.NOME_GENERO
        ORDER BY total DESC
        LIMIT 30
    """
    rows = query(sql, tuple(params_periodo))
    rows = _clean_rows(rows, "genero")
    return jsonify(rows[:15])


@dashboard_bp.route("/series-por-genero")
def series_por_genero():
    cond_periodo, params_periodo = _filtro_periodo("s")

    sql = f"""
        SELECT p.NOME_GENERO AS genero, COUNT(DISTINCT s.ID_MIDIA) AS total
        FROM pertencer p
        JOIN serie s ON p.FK_SERIE_ID_MIDIA = s.ID_MIDIA
        WHERE p.FK_SERIE_ID_MIDIA IS NOT NULL {cond_periodo}
        GROUP BY p.NOME_GENERO
        ORDER BY total DESC
        LIMIT 30
    """
    rows = query(sql, tuple(params_periodo))
    rows = _clean_rows(rows, "genero")
    return jsonify(rows[:15])


@dashboard_bp.route("/distribuicao-notas")
def distribuicao_notas():
    tipo = request.args.get("tipo", "filme")
    tabela = "serie" if tipo == "serie" else "filme"
    alias = "s" if tipo == "serie" else "f"
    cond_periodo, params_periodo = _filtro_periodo(alias)

    sql = f"""
        SELECT
            CASE
                WHEN NOTA_IMDB BETWEEN 0   AND 2   THEN '0-2 (Pessimo)'
                WHEN NOTA_IMDB BETWEEN 2.01 AND 4  THEN '2-4 (Ruim)'
                WHEN NOTA_IMDB BETWEEN 4.01 AND 6  THEN '4-6 (Regular)'
                WHEN NOTA_IMDB BETWEEN 6.01 AND 8  THEN '6-8 (Bom)'
                WHEN NOTA_IMDB BETWEEN 8.01 AND 10 THEN '8-10 (Excelente)'
            END AS faixa,
            COUNT(*) AS total
        FROM {tabela} {alias} WHERE NOTA_IMDB IS NOT NULL {cond_periodo}
        GROUP BY faixa ORDER BY MIN(NOTA_IMDB)
    """
    return jsonify(query(sql, tuple(params_periodo)))


@dashboard_bp.route("/lancamentos-por-ano")
def lancamentos_por_ano():
    tipo = request.args.get("tipo", "filme")
    tabela = "serie" if tipo == "serie" else "filme"
    alias = "s" if tipo == "serie" else "f"
    cond_periodo, params_periodo = _filtro_periodo(alias)

    sql = f"""
        SELECT ANO_LANCAMENTO AS ano, COUNT(*) AS total,
               ROUND(AVG(NOTA_IMDB), 2) AS media_nota
        FROM {tabela} {alias}
        WHERE ANO_LANCAMENTO IS NOT NULL {cond_periodo}
        GROUP BY ANO_LANCAMENTO ORDER BY ANO_LANCAMENTO
    """
    rows = query(sql, tuple(params_periodo))
    for r in rows:
        r["media_nota"] = float(r["media_nota"]) if r["media_nota"] is not None else None
    return jsonify(rows)


@dashboard_bp.route("/estatisticas-notas")
def estatisticas_notas():
    tipo = request.args.get("tipo", "filme")
    tabela = "serie" if tipo == "serie" else "filme"
    alias = "s" if tipo == "serie" else "f"
    cond_periodo, params_periodo = _filtro_periodo(alias)

    rows = query(
        f"SELECT NOTA_IMDB as nota FROM {tabela} {alias} WHERE NOTA_IMDB IS NOT NULL {cond_periodo}",
        tuple(params_periodo)
    )
    valores = [safe_float(r["nota"]) for r in rows]
    return jsonify(calcular_estatisticas(valores))


@dashboard_bp.route("/top-bilheteria")
def top_bilheteria():
    genero = request.args.get("genero")
    cond_genero, params_genero = _filtro_genero(genero)
    cond_periodo, params_periodo = _filtro_periodo("f")

    sql = f"""
        SELECT f.TITULO, f.BILHETERIA, f.NOTA_IMDB, f.ANO_LANCAMENTO,
               GROUP_CONCAT(DISTINCT p.NOME_GENERO SEPARATOR ', ') AS generos
        FROM filme f
        LEFT JOIN pertencer p ON p.FK_FILME_ID_MIDIA = f.ID_MIDIA
        WHERE f.BILHETERIA IS NOT NULL AND f.BILHETERIA > 0 {cond_genero} {cond_periodo}
        GROUP BY f.ID_MIDIA, f.TITULO, f.BILHETERIA, f.NOTA_IMDB, f.ANO_LANCAMENTO
        ORDER BY f.BILHETERIA DESC LIMIT 10
    """
    rows = query(sql, tuple(params_genero) + tuple(params_periodo))
    for r in rows:
        r["BILHETERIA"] = float(r["BILHETERIA"] or 0)
        r["NOTA_IMDB"]  = float(r["NOTA_IMDB"] or 0)
    return jsonify(rows)


@dashboard_bp.route("/filtros")
def filtros():
    anos_filmes = query("SELECT DISTINCT ANO_LANCAMENTO AS ano FROM filme WHERE ANO_LANCAMENTO IS NOT NULL ORDER BY ano DESC")
    anos_series = query("SELECT DISTINCT ANO_LANCAMENTO AS ano FROM serie  WHERE ANO_LANCAMENTO IS NOT NULL ORDER BY ano DESC")
    todos_anos  = sorted(set([r["ano"] for r in anos_filmes] + [r["ano"] for r in anos_series]), reverse=True)

    generos_raw  = query("SELECT DISTINCT NOME_GENERO AS genero FROM genero ORDER BY genero")
    generos_raw  = _clean_rows(generos_raw, "genero")
    plataformas  = query("SELECT ID_PLATAFORMA AS id, NOME AS nome FROM plataforma ORDER BY nome")

    return jsonify({
        "anos":       todos_anos,
        "generos":    [r["genero"] for r in generos_raw],
        "plataformas": plataformas,
    })


@dashboard_bp.route("/correlacao-nota-bilheteria")
def correlacao_nota_bilheteria():
    genero = request.args.get("genero")
    cond_genero, params_genero = _filtro_genero(genero)
    cond_periodo, params_periodo = _filtro_periodo("f")

    sql = f"""
        SELECT f.TITULO, ROUND(f.NOTA_IMDB, 2) AS nota_imdb,
               f.BILHETERIA, f.ANO_LANCAMENTO
        FROM filme f
        LEFT JOIN pertencer p ON p.FK_FILME_ID_MIDIA = f.ID_MIDIA
        WHERE f.NOTA_IMDB IS NOT NULL AND f.BILHETERIA IS NOT NULL AND f.BILHETERIA > 0 {cond_genero} {cond_periodo}
        GROUP BY f.ID_MIDIA, f.TITULO, f.NOTA_IMDB, f.BILHETERIA, f.ANO_LANCAMENTO
        ORDER BY f.BILHETERIA DESC LIMIT 50
    """
    rows = query(sql, tuple(params_genero) + tuple(params_periodo))
    for r in rows:
        r["BILHETERIA"] = float(r["BILHETERIA"] or 0)
        r["nota_imdb"]  = float(r["nota_imdb"] or 0)
    return jsonify(rows)


@dashboard_bp.route("/filmes-por-regiao")
def filmes_por_regiao():
    sql = """
        SELECT
            CASE
                WHEN PAIS_ORIGEM IN ('EUA','Canada') THEN 'America do Norte'
                WHEN PAIS_ORIGEM IN ('Brasil','Argentina','Mexico','Colombia') THEN 'America Latina'
                WHEN PAIS_ORIGEM IN ('Reino Unido','Franca','Alemanha','Italia','Espanha',
                                     'Portugal','Suecia','Noruega','Dinamarca','Belgica',
                                     'Austria','Suica','Irlanda','Holanda','Finlandia') THEN 'Europa'
                WHEN PAIS_ORIGEM IN ('Japao','Japão','Coreia do Sul','China','India',
                                     'Tailandia','Hong Kong','Taiwan') THEN 'Asia'
                WHEN PAIS_ORIGEM IN ('Australia','Nova Zelandia') THEN 'Oceania'
                WHEN PAIS_ORIGEM IN ('Nigeria','Africa do Sul','Egito') THEN 'Africa'
                ELSE 'Outros'
            END AS regiao,
            COUNT(*) AS total
        FROM filme WHERE PAIS_ORIGEM IS NOT NULL
        GROUP BY regiao ORDER BY total DESC
    """
    return jsonify(query(sql))


@dashboard_bp.route("/filmes-por-pais")
def filmes_por_pais():
    sql = """
        SELECT PAIS_ORIGEM AS pais, COUNT(*) AS total
        FROM filme WHERE PAIS_ORIGEM IS NOT NULL
        GROUP BY PAIS_ORIGEM ORDER BY total DESC LIMIT 15
    """
    return jsonify(query(sql))


@dashboard_bp.route("/por-plataforma")
def por_plataforma():
    sql = """
        SELECT pl.NOME AS plataforma,
               COUNT(DISTINCT d.FK_FILME_ID_MIDIA) AS total_filmes,
               COUNT(DISTINCT d.FK_SERIE_ID_MIDIA) AS total_series
        FROM disponivel d
        JOIN plataforma pl ON d.FK_PLATAFORMA_ID_PLATAFORMA = pl.ID_PLATAFORMA
        GROUP BY pl.NOME
        ORDER BY (total_filmes + total_series) DESC
    """
    return jsonify(query(sql))
