const API = "http://localhost:5000/api/dashboard";

const PALETTE = [
  "#e50914","#f5a623","#4ecdc4","#a78bfa","#38bdf8",
  "#fb7185","#fbbf24","#34d399","#f97316","#60a5fa",
  "#e879f9","#a3e635","#f43f5e","#06b6d4","#8b5cf6"
];

const filtros = {
  anoInicio: "todos",
  anoFim: "todos",
  tipoNota: "filme",
  tipoDistribuicao: "filme",
  tipoLancamento: "filme",
  tipoGenero: "filme",
};

function buildPeriodoParams() {
  const p = new URLSearchParams();
  if (filtros.anoInicio !== "todos") p.set("ano_inicio", filtros.anoInicio);
  if (filtros.anoFim !== "todos") p.set("ano_fim", filtros.anoFim);
  return p.toString() ? `?${p.toString()}` : "";
}

function appendPeriodo(url) {
  const sep = url.includes("?") ? "&" : "?";
  const parts = [];
  if (filtros.anoInicio !== "todos") parts.push(`ano_inicio=${filtros.anoInicio}`);
  if (filtros.anoFim !== "todos") parts.push(`ano_fim=${filtros.anoFim}`);
  return parts.length ? `${url}${sep}${parts.join("&")}` : url;
}

const charts = {};


function showLoading(show) {
  document.getElementById("loading").style.display = show ? "flex" : "none";
}

function showToast(msg) {
  const t = document.getElementById("toast");
  t.textContent = "Aviso: " + msg;
  t.classList.add("show");
  setTimeout(() => t.classList.remove("show"), 4000);
}

async function fetchJson(url) {
  const res = await fetch(url);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json();
}

function destroyChart(id) {
  if (charts[id]) { charts[id].destroy(); delete charts[id]; }
}

function fmt(n) {
  if (n === null || n === undefined) return "—";
  if (n >= 1_000_000_000) return `$${(n/1e9).toFixed(2)}B`;
  if (n >= 1_000_000) return `$${(n/1e6).toFixed(1)}M`;
  if (n >= 1_000) return `$${(n/1e3).toFixed(1)}k`;
  return String(n);
}

const defaultChartOpts = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { labels: { color: "#7a8299", font: { size: 11 } } },
    tooltip: {
      backgroundColor: "#1e2535", borderColor: "#2a3048", borderWidth: 1,
      titleColor: "#e8eaf0", bodyColor: "#a0aec0", padding: 10,
    },
  },
  scales: {
    x: { ticks: { color: "#7a8299" }, grid: { color: "#1e2535" } },
    y: { ticks: { color: "#7a8299" }, grid: { color: "#1e2535" } },
  },
};


async function carregarFiltros() {
  try {
    const data = await fetchJson(`${API}/filtros`);

    const anosOrdenados = [...data.anos].sort((a, b) => a - b);

    const selInicio = document.getElementById("sel-ano-inicio");
    const selFim = document.getElementById("sel-ano-fim");
    anosOrdenados.forEach(a => {
      selInicio.appendChild(new Option(a, a));
      selFim.appendChild(new Option(a, a));
    });
    // default fim = último ano disponível
    if (anosOrdenados.length) selFim.value = anosOrdenados[anosOrdenados.length - 1];

  } catch (e) { showToast("Erro ao carregar filtros: " + e.message); }
}

async function carregarIndicadores() {
  try {
    const d = await fetchJson(appendPeriodo(`${API}/indicadores`));
    document.getElementById("kpi-filmes").textContent    = d.total_filmes.toLocaleString("pt-BR");
    document.getElementById("kpi-series").textContent    = d.total_series.toLocaleString("pt-BR");
    document.getElementById("kpi-usuarios").textContent  = d.total_usuarios.toLocaleString("pt-BR");
    document.getElementById("kpi-episodios").textContent = d.total_episodios.toLocaleString("pt-BR");
    document.getElementById("kpi-nota-filmes").textContent = d.media_nota_filmes.toFixed(1);
    document.getElementById("kpi-nota-series").textContent = d.media_nota_series.toFixed(1);
    document.getElementById("kpi-plataformas").textContent = d.total_plataformas;
    document.getElementById("kpi-generos").textContent     = d.total_generos;

    document.getElementById("kpi-maior-filme").textContent      = d.maior_nota_filme.titulo;
    document.getElementById("kpi-maior-filme-nota").textContent = d.maior_nota_filme.nota.toFixed(1) + " / 10";
    document.getElementById("kpi-menor-filme").textContent      = d.menor_nota_filme.titulo;
    document.getElementById("kpi-menor-filme-nota").textContent = d.menor_nota_filme.nota.toFixed(1) + " / 10";
    document.getElementById("kpi-maior-serie").textContent      = d.maior_nota_serie.titulo;
    document.getElementById("kpi-maior-serie-nota").textContent = d.maior_nota_serie.nota.toFixed(1) + " / 10";
    document.getElementById("kpi-menor-serie").textContent      = d.menor_nota_serie.titulo;
    document.getElementById("kpi-menor-serie-nota").textContent = d.menor_nota_serie.nota.toFixed(1) + " / 10";
  } catch (e) { showToast("Erro nos indicadores: " + e.message); }
}


async function carregarGenero() {
  try {
    const isFilme = filtros.tipoGenero === "filme";
    const baseUrl = isFilme
      ? `${API}/filmes-por-genero`
      : `${API}/series-por-genero`;
    const data = await fetchJson(appendPeriodo(baseUrl));

    document.getElementById("titulo-genero").textContent =
      isFilme ? "Filmes por Gênero" : "Séries por Gênero";

    destroyChart("genero");
    const ctx = document.getElementById("chart-genero").getContext("2d");

    // Both filmes and séries now use vertical bars (same style)
    charts["genero"] = new Chart(ctx, {
      type: "bar",
      data: {
        labels: data.map(d => d.genero),
        datasets: [{
          label: isFilme ? "Qtd. Filmes" : "Qtd. Séries",
          data: data.map(d => d.total),
          backgroundColor: PALETTE,
          borderRadius: 6
        }]
      },
      options: {
        ...defaultChartOpts,
        plugins: { ...defaultChartOpts.plugins, legend: { display: false } },
        scales: {
          ...defaultChartOpts.scales,
          y: { ...defaultChartOpts.scales.y, beginAtZero: true, ticks: { color: "#7a8299", stepSize: 1 } }
        }
      }
    });
  } catch (e) { showToast("Erro grafico genero: " + e.message); }
}


async function carregarDistribuicaoNotas() {
  try {
    const data = await fetchJson(`${API}/distribuicao-notas?tipo=${filtros.tipoDistribuicao}`);
    destroyChart("pizza");

    if (!data || data.length === 0) {
      const ctx = document.getElementById("chart-pizza").getContext("2d");
      ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
      ctx.fillStyle = "#7a8299"; ctx.font = "14px Inter"; ctx.textAlign = "center";
      ctx.fillText("Sem dados disponíveis", ctx.canvas.width / 2, ctx.canvas.height / 2);
      return;
    }

    const cores = ["#ef4444","#f97316","#eab308","#22c55e","#4ecdc4"];
    const ctx = document.getElementById("chart-pizza").getContext("2d");
    charts["pizza"] = new Chart(ctx, {
      type: "doughnut",
      data: {
        labels: data.map(d => d.faixa),
        datasets: [{ data: data.map(d => d.total), backgroundColor: cores, borderWidth: 2, borderColor: "#161b27" }]
      },
      options: {
        responsive: true, maintainAspectRatio: false, cutout: "60%",
        plugins: {
          legend: { position: "right", labels: { color: "#7a8299", font: { size: 11 }, padding: 12 } },
          tooltip: defaultChartOpts.plugins.tooltip,
        }
      }
    });
  } catch (e) { showToast("Erro grafico distribuicao: " + e.message); }
}


async function carregarLancamentosPorAno() {
  try {
    const data = await fetchJson(appendPeriodo(`${API}/lancamentos-por-ano?tipo=${filtros.tipoLancamento}`));
    destroyChart("linha");
    const ctx = document.getElementById("chart-linha").getContext("2d");
    charts["linha"] = new Chart(ctx, {
      type: "line",
      data: {
        labels: data.map(d => d.ano),
        datasets: [
          {
            label: "Qtd. Lançamentos",
            data: data.map(d => d.total),
            borderColor: "#e50914", backgroundColor: "rgba(229,9,20,0.1)",
            fill: true, tension: 0.4, pointBackgroundColor: "#e50914", pointRadius: 4, yAxisID: "y",
          },
          {
            label: "Media IMDb",
            data: data.map(d => d.media_nota),
            borderColor: "#f5a623", backgroundColor: "rgba(245,166,35,0.08)",
            fill: false, tension: 0.4, pointBackgroundColor: "#f5a623", pointRadius: 4,
            yAxisID: "y2", spanGaps: true,
          }
        ]
      },
      options: {
        ...defaultChartOpts,
        scales: {
          x: defaultChartOpts.scales.x,
          y: { ...defaultChartOpts.scales.y, position: "left", beginAtZero: true, ticks: { color: "#7a8299", stepSize: 1 }, title: { display: true, text: "Lançamentos", color: "#7a8299" } },
          y2: { ...defaultChartOpts.scales.y, position: "right", min: 0, max: 10, title: { display: true, text: "Media IMDb", color: "#7a8299" }, grid: { drawOnChartArea: false } }
        }
      }
    });
  } catch (e) { showToast("Erro grafico lancamentos: " + e.message); }
}


async function carregarEstatisticas() {
  try {
    const d = await fetchJson(appendPeriodo(`${API}/estatisticas-notas?tipo=${filtros.tipoNota}`));
    document.getElementById("stat-media").textContent    = d.media;
    document.getElementById("stat-mediana").textContent  = d.mediana;
    document.getElementById("stat-moda").textContent     = d.moda;
    document.getElementById("stat-variancia").textContent = d.variancia;
    document.getElementById("stat-dp").textContent       = d.desvio_padrao;

    destroyChart("radar");
    const maxVar = 25;
    const ctx = document.getElementById("chart-radar").getContext("2d");
    charts["radar"] = new Chart(ctx, {
      type: "radar",
      data: {
        labels: ["Media", "Mediana", "Moda", "Variancia (x10)", "Desv. Padrao (x2)"],
        datasets: [{
          label: filtros.tipoNota === "serie" ? "Series (IMDb)" : "Filmes (IMDb)",
          data: [d.media, d.mediana, d.moda, Math.min(d.variancia * 10 / maxVar * 10, 10), Math.min(d.desvio_padrao * 2, 10)],
          backgroundColor: "rgba(229,9,20,0.15)", borderColor: "#e50914",
          pointBackgroundColor: "#e50914", pointRadius: 5,
        }]
      },
      options: {
        responsive: true, maintainAspectRatio: false,
        scales: { r: { min: 0, max: 10, ticks: { color: "#7a8299", stepSize: 2, backdropColor: "transparent" }, grid: { color: "#2a3048" }, angleLines: { color: "#2a3048" }, pointLabels: { color: "#a0aec0", font: { size: 11 } } } },
        plugins: { legend: { labels: { color: "#7a8299" } }, tooltip: defaultChartOpts.plugins.tooltip }
      }
    });
  } catch (e) { showToast("Erro grafico radar: " + e.message); }
}


async function carregarTopBilheteria() {
  try {
    const data = await fetchJson(appendPeriodo(`${API}/top-bilheteria`));
    destroyChart("bilheteria");
    const ctx = document.getElementById("chart-bilheteria").getContext("2d");
    charts["bilheteria"] = new Chart(ctx, {
      type: "bar",
      data: {
        labels: data.map(d => d.TITULO.length > 22 ? d.TITULO.slice(0,22)+"…" : d.TITULO),
        datasets: [{ label: "Bilheteria (USD)", data: data.map(d => d.BILHETERIA), backgroundColor: PALETTE.slice(0, data.length), borderRadius: 6 }]
      },
      options: {
        ...defaultChartOpts, indexAxis: "y",
        plugins: { ...defaultChartOpts.plugins, legend: { display: false }, tooltip: { ...defaultChartOpts.plugins.tooltip, callbacks: { label: ctx => ` ${fmt(ctx.raw)} | IMDb: ${data[ctx.dataIndex].NOTA_IMDB}` } } },
        scales: { x: { ...defaultChartOpts.scales.x, ticks: { color: "#7a8299", callback: v => fmt(v) } }, y: defaultChartOpts.scales.y }
      }
    });
  } catch (e) { showToast("Erro grafico bilheteria: " + e.message); }
}

async function carregarCorrelacao() {
  try {
    const data = await fetchJson(appendPeriodo(`${API}/correlacao-nota-bilheteria`));
    destroyChart("correlacao");
    const ctx = document.getElementById("chart-correlacao").getContext("2d");
    charts["correlacao"] = new Chart(ctx, {
      type: "scatter",
      data: { datasets: [{ label: "Filmes", data: data.map(d => ({ x: d.nota_imdb, y: d.BILHETERIA, titulo: d.TITULO })), backgroundColor: "rgba(229,9,20,0.65)", pointRadius: 6, pointHoverRadius: 9 }] },
      options: {
        ...defaultChartOpts,
        plugins: { ...defaultChartOpts.plugins, tooltip: { ...defaultChartOpts.plugins.tooltip, callbacks: { label: ctx => `${ctx.raw.titulo} — Nota: ${ctx.raw.x} | ${fmt(ctx.raw.y)}` } } },
        scales: {
          x: { ...defaultChartOpts.scales.x, title: { display: true, text: "Nota IMDb", color: "#7a8299" }, min: 0, max: 10 },
          y: { ...defaultChartOpts.scales.y, title: { display: true, text: "Bilheteria (USD)", color: "#7a8299" }, ticks: { color: "#7a8299", callback: v => fmt(v) } }
        }
      }
    });
  } catch (e) { showToast("Erro grafico correlacao: " + e.message); }
}


async function carregarFilmesPorPais() {
  try {
    const data = await fetchJson(`${API}/filmes-por-pais`);
    destroyChart("pais");
    const ctx = document.getElementById("chart-pais").getContext("2d");
    charts["pais"] = new Chart(ctx, {
      type: "bar",
      data: {
        labels: data.map(d => d.pais),
        datasets: [{
          label: "Qtd. Filmes",
          data: data.map(d => d.total),
          backgroundColor: PALETTE,
          borderRadius: 6,
        }]
      },
      options: {
        ...defaultChartOpts,
        plugins: { ...defaultChartOpts.plugins, legend: { display: false } },
        scales: {
          x: defaultChartOpts.scales.x,
          y: { ...defaultChartOpts.scales.y, beginAtZero: true, ticks: { color: "#7a8299", stepSize: 1 } }
        }
      }
    });
  } catch (e) { showToast("Erro grafico pais: " + e.message); }
}


async function carregarFilmesPorRegiao() {
  try {
    const data = await fetchJson(`${API}/filmes-por-regiao`);
    destroyChart("regiao");
    const ctx = document.getElementById("chart-regiao").getContext("2d");
    charts["regiao"] = new Chart(ctx, {
      type: "doughnut",
      data: {
        labels: data.map(d => d.regiao),
        datasets: [{ data: data.map(d => d.total), backgroundColor: PALETTE, borderWidth: 2, borderColor: "#161b27" }]
      },
      options: {
        responsive: true, maintainAspectRatio: false, cutout: "55%",
        plugins: {
          legend: { position: "right", labels: { color: "#7a8299", font: { size: 11 }, padding: 12 } },
          tooltip: { ...defaultChartOpts.plugins.tooltip, callbacks: { label: ctx => ` ${ctx.label}: ${ctx.raw} filmes` } }
        }
      }
    });
  } catch (e) { showToast("Erro grafico regiao: " + e.message); }
}


async function carregarPorPlataforma() {
  try {
    const data = await fetchJson(`${API}/por-plataforma`);
    destroyChart("plataforma");
    const ctx = document.getElementById("chart-plataforma").getContext("2d");
    charts["plataforma"] = new Chart(ctx, {
      type: "bar",
      data: {
        labels: data.map(d => d.plataforma),
        datasets: [
          { label: "Filmes", data: data.map(d => d.total_filmes), backgroundColor: "#e50914", borderRadius: 4 },
          { label: "Series", data: data.map(d => d.total_series), backgroundColor: "#38bdf8", borderRadius: 4 }
        ]
      },
      options: { ...defaultChartOpts, scales: { ...defaultChartOpts.scales, y: { ...defaultChartOpts.scales.y, beginAtZero: true, ticks: { color: "#7a8299", stepSize: 2 } } } }
    });
  } catch (e) { showToast("Erro grafico plataforma: " + e.message); }
}


async function carregarDashboard() {
  showLoading(true);
  try {
    await Promise.all([
      carregarIndicadores(),
      carregarGenero(),
      carregarDistribuicaoNotas(),
      carregarLancamentosPorAno(),
      carregarEstatisticas(),
      carregarTopBilheteria(),
      carregarCorrelacao(),
      carregarFilmesPorPais(),
      carregarFilmesPorRegiao(),
      carregarPorPlataforma(),
    ]);
  } finally {
    showLoading(false);
  }
}


function onPeriodoChange() {
  carregarIndicadores();
  carregarGenero();
  carregarDistribuicaoNotas();
  carregarLancamentosPorAno();
  carregarEstatisticas();
  carregarTopBilheteria();
  carregarCorrelacao();
}

function bindEventos() {
  document.getElementById("sel-ano-inicio").addEventListener("change", e => {
    filtros.anoInicio = e.target.value;
    onPeriodoChange();
  });

  document.getElementById("sel-ano-fim").addEventListener("change", e => {
    filtros.anoFim = e.target.value;
    onPeriodoChange();
  });



  document.querySelectorAll(".btn-tipo-genero").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".btn-tipo-genero").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      filtros.tipoGenero = btn.dataset.tipo;
      carregarGenero();
    });
  });

  document.querySelectorAll(".btn-tipo-nota").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".btn-tipo-nota").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      filtros.tipoNota = btn.dataset.tipo;
      carregarEstatisticas();
    });
  });

  document.querySelectorAll(".btn-tipo-dist").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".btn-tipo-dist").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      filtros.tipoDistribuicao = btn.dataset.tipo;
      carregarDistribuicaoNotas();
    });
  });

  document.querySelectorAll(".btn-tipo-lancamento").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".btn-tipo-lancamento").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      filtros.tipoLancamento = btn.dataset.tipo;
      carregarLancamentosPorAno();
    });
  });

  document.getElementById("btn-refresh").addEventListener("click", carregarDashboard);
}


document.addEventListener("DOMContentLoaded", async () => {
  await carregarFiltros();
  bindEventos();
  await carregarDashboard();
});