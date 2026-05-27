import statistics
import numpy as np
from collections import Counter


def calcular_estatisticas(valores: list) -> dict:

    if not valores:
        return {
            "media": 0,
            "mediana": 0,
            "moda": 0,
            "variancia": 0,
            "desvio_padrao": 0,
            "total": 0
        }

    arr = [float(v) for v in valores if v is not None]

    if not arr:
        return {
            "media": 0,
            "mediana": 0,
            "moda": 0,
            "variancia": 0,
            "desvio_padrao": 0,
            "total": 0
        }

    try:
        moda = statistics.mode(arr)
    except statistics.StatisticsError:
        contagem = Counter(arr)
        moda = contagem.most_common(1)[0][0]

    return {
        "media": round(float(np.mean(arr)), 2),
        "mediana": round(float(np.median(arr)), 2),
        "moda": round(float(moda), 2),
        "variancia": round(float(np.var(arr)), 2),
        "desvio_padrao": round(float(np.std(arr)), 2),
        "total": len(arr)
    }


def safe_float(val):
    try:
        return float(val) if val is not None else 0.0
    except (ValueError, TypeError):
        return 0.0