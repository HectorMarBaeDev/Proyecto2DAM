(function() {
const API = "https://pokemon-backend-849x.onrender.com/api";

(async () => {
    const token = await window.api.getToken();
    if (!token) { window.location.href = "inicio_sesion.html"; return; }
    initEquipos();
})();

async function initEquipos() {

    // ── Traducción tipos EN→ES (igual que index) ─────────
    const TIPOS_ES = {
        "normal":"Normal","fighting":"Lucha","flying":"Volador","poison":"Veneno",
        "ground":"Tierra","rock":"Roca","bug":"Bicho","ghost":"Fantasma","steel":"Acero",
        "fire":"Fuego","water":"Agua","grass":"Planta","electric":"Eléctrico",
        "psychic":"Psíquico","ice":"Hielo","dragon":"Dragón","dark":"Siniestro","fairy":"Hada"
    };
    const TIPO_COLORES = {
        "normal":"#9fa19f","fighting":"#ff8000","flying":"#81b9ef","poison":"#9141cb",
        "ground":"#915121","rock":"#afa981","bug":"#91a119","ghost":"#704170",
        "steel":"#60a1b8","fire":"#e62829","water":"#2980ef","grass":"#3fa129",
        "electric":"#fac000","psychic":"#ef4179","ice":"#3dcef3","dragon":"#5060e1",
        "dark":"#624d4e","fairy":"#ef70ef"
    };
    function tipoBadge(typeEn) {
        const es = TIPOS_ES[typeEn] || typeEn;
        const color = TIPO_COLORES[typeEn] || "#666";
        return `<span class="tipo-badge-pkm" style="background:${color}22;border:1px solid ${color}66;color:${color};">${es}</span>`;
    }

    const editItem = document.getElementById("editItem");
    const editAbility = document.getElementById("editAbility");
    const move1 = document.getElementById("move1");
    const move2 = document.getElementById("move2");
    const move3 = document.getElementById("move3");
    const move4 = document.getElementById("move4");
    const evHp  = document.getElementById("evHp");
    const evAtk = document.getElementById("evAtk");
    const evDef = document.getElementById("evDef");
    const evSpA = document.getElementById("evSpA");
    const evSpD = document.getElementById("evSpD");
    const evSpe = document.getElementById("evSpe");
    const ivHp  = document.getElementById("ivHp");
    const ivAtk = document.getElementById("ivAtk");
    const ivDef = document.getElementById("ivDef");
    const ivSpA = document.getElementById("ivSpA");
    const ivSpD = document.getElementById("ivSpD");
    const ivSpe = document.getElementById("ivSpe");

    // ── Shrink navbar igual que en index ──────────────
    const navbar  = document.querySelector(".navPrincipal");
    const equiposView = document.getElementById("equiposView");
    equiposView.addEventListener("scroll", () => {
        navbar.classList.toggle("shrink", equiposView.scrollTop > 40);
    });

    document.getElementById("cerrarSesion").querySelector("a").addEventListener("click", async e => {
        e.preventDefault();
        await window.api.clearToken();
        await window.api.clearUser();
        window.location.href = "inicio_sesion.html";
    });

    let equipoActualId = null;
    const bsModalEquipo = new bootstrap.Modal(document.getElementById("modalEquipo"));

    function mostrarToast(msg, tipo = "success") {
        const el = document.getElementById("toastEquipos");
        el.className = `toast align-items-center text-bg-${tipo} border-0`;
        document.getElementById("toastEquiposBody").textContent = msg;
        bootstrap.Toast.getOrCreateInstance(el).show();
    }

    // ── Paginación ─────────────────────────────────────
    const EQUIPOS_POR_PAGINA = 6;
    let paginaActual = 1;
    let todosLosEquipos = [];

    const ICONO_VACIO = `
        <div class="preview-vacio">
            <svg xmlns="http://www.w3.org/2000/svg" width="36" height="36" viewBox="0 0 1024 1024" fill="currentColor">
                <path d="M512,359C596,359 665,428 665,512C665,596 596,665 512,665C428,665 359,596 359,512C359,428 428,359 512,359ZM512,410C456,410 410,456 410,512C410,568 456,614 512,614C568,614 614,568 614,512C614,456 568,410 512,410ZM806,218C884,296 928,402 928,512C928,622 884,728 806,806C728,884 622,928 512,928C402,928 296,884 218,806C140,728 96,622 96,512C96,402 140,296 218,218C296,140 402,96 512,96C622,96 728,140 806,218ZM332,512C332,512 138,512 138,512C138,611 178,706 248,776C318,846 413,886 512,886C611,886 706,846 776,776C846,706 886,611 886,512C886,512 692,512 692,512C692,413 611,332 512,332C413,332 332,413 332,512Z"/>
            </svg>
            <span>Sin pokémon</span>
        </div>`;

    function crearCardEquipo(eq) {
        const wrap = document.createElement("div");
        wrap.className = "equipo-card-wrap";
        const formatoBadge = eq.format
            ? `<span class="equipo-formato">${eq.format}</span>`
            : `<span class="equipo-formato" style="opacity:0.45;">Sin formato</span>`;
        wrap.innerHTML = `
            <div class="card equipo-card">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${eq.name}</h5>
                    ${formatoBadge}
                    <div class="pokemon-preview" id="preview-${eq.id}">
                        <div style="grid-column:1/-1;align-self:center;justify-self:center;">
                            <div class="spinner-border spinner-border-sm text-warning"></div>
                        </div>
                    </div>
                    <div class="mt-auto d-flex gap-2">
                        <button class="btn btn-ver flex-fill btn-ver" data-id="${eq.id}" data-name="${eq.name}">Ver / Editar</button>
                        <button class="btn btn-exportar d-flex align-items-center justify-content-center" data-id="${eq.id}" title="Exportar formato Showdown">
                            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
                                <path d="M4 1.5H3a2 2 0 0 0-2 2V14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V3.5a2 2 0 0 0-2-2h-1v1h1a1 1 0 0 1 1 1V14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V3.5a1 1 0 0 1 1-1h1z"/>
                                <path d="M9.5 1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5zm-3-1A1.5 1.5 0 0 0 5 1.5v1A1.5 1.5 0 0 0 6.5 4h3A1.5 1.5 0 0 0 11 2.5v-1A1.5 1.5 0 0 0 9.5 0z"/>
                            </svg>
                        </button>
                        <button class="btn btn-borrar d-flex align-items-center justify-content-center" data-id="${eq.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="15" height="15" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">
                                <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                            </svg>
                        </button>
                    </div>
                </div>
            </div>`;
        return wrap;
    }

    function renderizarPagina() {
        const lista = document.getElementById("listaEquipos");
        lista.innerHTML = "";

        const inicio = (paginaActual - 1) * EQUIPOS_POR_PAGINA;
        const equiposPagina = todosLosEquipos.slice(inicio, inicio + EQUIPOS_POR_PAGINA);

        // Fila 1: primeros 3
        const fila1 = document.createElement("div");
        fila1.className = "equipos-fila";
        equiposPagina.slice(0, 3).forEach(eq => {
            const wrap = crearCardEquipo(eq);
            fila1.appendChild(wrap);
            cargarPreview(eq.id);
        });
        lista.appendChild(fila1);

        // Fila 2: siguientes 3 (si los hay)
        if (equiposPagina.length > 3) {
            const fila2 = document.createElement("div");
            fila2.className = "equipos-fila";
            equiposPagina.slice(3, 6).forEach(eq => {
                const wrap = crearCardEquipo(eq);
                fila2.appendChild(wrap);
                cargarPreview(eq.id);
            });
            lista.appendChild(fila2);
        }

        lista.querySelectorAll(".btn-ver").forEach(btn =>
            btn.addEventListener("click", () => abrirModal(btn.dataset.id, btn.dataset.name)));
        lista.querySelectorAll(".btn-exportar").forEach(btn =>
            btn.addEventListener("click", () => exportarEquipo(btn.dataset.id)));
        lista.querySelectorAll(".btn-borrar").forEach(btn =>
            btn.addEventListener("click", () => eliminarEquipo(btn.dataset.id)));

        crearPaginacion();
    }

    // ── Modal salto de página (igual que index) ──────────
    const modalSaltoEquipos = new bootstrap.Modal(document.getElementById("modalSaltoPaginaEquipos"));
    const inputPaginaEquipos = document.getElementById("inputPaginaEquipos");
    const errorPaginaEquipos = document.getElementById("errorPaginaEquipos");
    const btnIrPaginaEquipos = document.getElementById("btnIrPaginaEquipos");

    function mostrarModalSaltoEquipos() {
        inputPaginaEquipos.value = "";
        errorPaginaEquipos.classList.add("d-none");
        modalSaltoEquipos.show();
    }

    inputPaginaEquipos.addEventListener("keypress", e => { if (e.key === "Enter") btnIrPaginaEquipos.click(); });
    btnIrPaginaEquipos.addEventListener("click", () => {
        const page = parseInt(inputPaginaEquipos.value);
        const total = Math.ceil(todosLosEquipos.length / EQUIPOS_POR_PAGINA);
        if (!isNaN(page) && page >= 1 && page <= total) {
            modalSaltoEquipos.hide();
            irAPagina(page);
        } else {
            errorPaginaEquipos.classList.remove("d-none");
        }
    });

    function irAPagina(page) {
        paginaActual = page;
        renderizarPagina();
        equiposView.scrollTo({ top: 0, behavior: "smooth" });
    }

    function crearPaginacion() {
        let paginacion = document.getElementById("paginacionEquipos");
        if (!paginacion) {
            paginacion = document.createElement("div");
            paginacion.id = "paginacionEquipos";
            paginacion.className = "d-flex justify-content-center mt-2 pb-4";
            document.getElementById("equiposView").appendChild(paginacion);
        }
        paginacion.innerHTML = "";

        const total = Math.ceil(todosLosEquipos.length / EQUIPOS_POR_PAGINA);
        if (total <= 1) return;

        const container = document.createElement("div");
        container.className = "d-flex align-items-center gap-2 flex-wrap justify-content-center";

        // Botón anterior
        const prev = document.createElement("button");
        prev.className = "btn btn-secondary";
        prev.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-left" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M15 8a.5.5 0 0 0-.5-.5H2.707l3.147-3.146a.5.5 0 1 0-.708-.708l-4 4a.5.5 0 0 0 0 .708l4 4a.5.5 0 0 0 .708-.708L2.707 8.5H14.5A.5.5 0 0 0 15 8"/></svg>`;
        prev.disabled = paginaActual === 1;
        prev.onclick = () => irAPagina(paginaActual - 1);
        container.appendChild(prev);

        // Lógica de páginas con "..." igual que index
        const pages = [];

        if (total === 1) {
            pages.push(1);
        } else {
            pages.push(1);
            if (paginaActual === total) {
                pages.push("...");
                pages.push(total);
            } else {
                if (paginaActual > 1) pages.push(paginaActual);
                if (paginaActual < total) pages.push("...");
                pages.push(total);
            }
        }

        pages.forEach(p => {
            if (p === "...") {
                const b = document.createElement("button");
                b.className = "btn btn-outline-primary";
                b.textContent = "...";
                b.onclick = mostrarModalSaltoEquipos;
                container.appendChild(b);
            } else {
                const b = document.createElement("button");
                b.className = p === paginaActual ? "btn btn-primary" : "btn btn-outline-primary";
                b.textContent = p;
                b.disabled = p === paginaActual;
                b.onclick = () => irAPagina(p);
                container.appendChild(b);
            }
        });

        // Botón siguiente
        const next = document.createElement("button");
        next.className = "btn btn-secondary";
        next.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-arrow-right" viewBox="0 0 16 16"><path fill-rule="evenodd" d="M1 8a.5.5 0 0 1 .5-.5h11.793l-3.147-3.146a.5.5 0 0 1 .708-.708l4 4a.5.5 0 0 1 0 .708l-4 4a.5.5 0 0 1-.708-.708L13.293 8.5H1.5A.5.5 0 0 1 1 8"/></svg>`;
        next.disabled = paginaActual === total;
        next.onclick = () => irAPagina(paginaActual + 1);
        container.appendChild(next);

        paginacion.appendChild(container);
    }

    async function cargarEquipos() {
        document.getElementById("spinnerEquipos").classList.remove("d-none");
        document.getElementById("listaEquipos").classList.add("d-none");
        document.getElementById("sinEquipos").classList.add("d-none");
        const paginacionVieja = document.getElementById("paginacionEquipos");
        if (paginacionVieja) paginacionVieja.innerHTML = "";

        const res = await window.api.fetchWithAuth(`${API}/teams/me`);
        document.getElementById("spinnerEquipos").classList.add("d-none");

        if (!res.ok) { mostrarToast("Error al cargar equipos.", "danger"); return; }
        if (!res.data.length) { document.getElementById("sinEquipos").classList.remove("d-none"); return; }

        todosLosEquipos = res.data;
        paginaActual = 1;
        document.getElementById("listaEquipos").classList.remove("d-none");
        renderizarPagina();
    }

    async function cargarPreview(teamId) {
        const res = await window.api.fetchWithAuth(`${API}/pokemon/team/${teamId}`);
        const el = document.getElementById(`preview-${teamId}`);
        if (!el) return;
        el.innerHTML = "";
        if (!res.ok || !res.data.length) {
            el.innerHTML = ICONO_VACIO;
            return;
        }
        res.data.forEach(p => {
            const img = document.createElement("img");
            img.src = p.image; img.alt = p.name; img.title = p.name;
            el.appendChild(img);
        });
    }

    async function abrirModal(teamId, teamName) {
        equipoActualId = teamId;
        document.getElementById("modalEquipoTitulo").textContent = teamName;
        document.getElementById("errorAnadirPokemon").classList.add("d-none");
        document.getElementById("inputBuscarPokemon").value = "";
        bsModalEquipo.show();
        await cargarPokemonModal(teamId);
    }

    async function cargarPokemonModal(teamId) {
        const container = document.getElementById("pokemonDelEquipo");
        container.innerHTML = `<div class="d-flex justify-content-center w-100"><div class="spinner-border text-warning"></div></div>`;
        const res = await window.api.fetchWithAuth(`${API}/pokemon/team/${teamId}`);
        container.innerHTML = "";
        const count = res.ok ? res.data.length : 0;
        document.getElementById("contadorPokemon").textContent = `${count}/6`;
        document.getElementById("addPokemonSection").classList.toggle("d-none", count >= 6);

        if (!res.ok) { container.innerHTML = `<p class="text-danger">Error al cargar.</p>`; return; }
        if (!res.data.length) { container.innerHTML = `<p class="text-muted w-100 text-center">Este equipo no tiene Pokémon.</p>`; return; }

        res.data.forEach(p => {
            const col = document.createElement("div");
            col.className = "col-md-4";
            const tipos = [p.primaryType, p.secondaryType].filter(Boolean).map(tipoBadge).join("");
            col.innerHTML = `
                <div class="equipo-pokemon-card-modal">
                    <img src="${p.image}" alt="${p.name}" class="pkm-modal-img">
                    <p class="pkm-modal-nombre">${p.name}</p>
                    <div class="pkm-modal-tipos">${tipos}</div>
                    <div class="pkm-modal-btns">
                        <button class="btn btn-pkm-editar btn-editar" data-id="${p.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M12.854.146a.5.5 0 0 0-.707 0L10.5 1.793 14.207 5.5l1.647-1.646a.5.5 0 0 0 0-.708zm.646 6.061L9.793 2.5 3.293 9H3.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.5h.5a.5.5 0 0 1 .5.5v.207zm-7.468 7.468A.5.5 0 0 1 6 13.5V13h-.5a.5.5 0 0 1-.5-.5V12h-.5a.5.5 0 0 1-.5-.5V11h-.5a.5.5 0 0 1-.5-.5V10h-.5a.5.5 0 0 1-.175-.032l-.179.178a.5.5 0 0 0-.11.168l-2 5a.5.5 0 0 0 .65.65l5-2a.5.5 0 0 0 .168-.11z"/></svg>
                            Editar
                        </button>
                        <button class="btn btn-pkm-quitar btn-quitar" data-id="${p.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" fill="currentColor" viewBox="0 0 16 16"><path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/></svg>
                            Quitar
                        </button>
                    </div>
                </div>`;
            container.appendChild(col);
        });

        container.querySelectorAll(".btn-quitar").forEach(btn =>
            btn.addEventListener("click", () => quitarPokemon(btn.dataset.id)));
        container.querySelectorAll(".btn-editar").forEach(btn =>
            btn.addEventListener("click", () => abrirModalEditar(btn.dataset.id)));
    }

    async function quitarPokemon(pkId) {
        const res = await window.api.fetchWithAuth(`${API}/pokemon/id/${pkId}`, { method: "DELETE" });
        if (res.ok) { mostrarToast("Pokémon eliminado.", "warning"); await cargarPokemonModal(equipoActualId); await cargarPreview(equipoActualId); }
        else mostrarToast("Error al eliminar.", "danger");
    }

    document.getElementById("btnAnadirPokemon").addEventListener("click", async () => {
        const errEl = document.getElementById("errorAnadirPokemon");
        errEl.classList.add("d-none");
        const identifier = document.getElementById("inputBuscarPokemon").value.trim().toLowerCase();
        if (!identifier) { errEl.textContent = "Escribe un nombre o número."; errEl.classList.remove("d-none"); return; }
        const res = await window.api.fetchWithAuth(`${API}/pokemon?teamId=${equipoActualId}`, {
            method: "POST", body: JSON.stringify({ identifier })
        });
        if (res.ok) {
            document.getElementById("inputBuscarPokemon").value = "";
            mostrarToast("¡Pokémon añadido!", "success");
            await cargarPokemonModal(equipoActualId); await cargarPreview(equipoActualId);
        } else if (res.status === 400) {
            errEl.textContent = "Equipo lleno o Pokémon no encontrado."; errEl.classList.remove("d-none");
        } else {
            errEl.textContent = "Error al añadir."; errEl.classList.remove("d-none");
        }
    });

    document.getElementById("inputBuscarPokemon").addEventListener("keypress", e => {
        if (e.key === "Enter") document.getElementById("btnAnadirPokemon").click();
    });

    document.getElementById("btnEliminarEquipo").addEventListener("click", async () => {
        if (!confirm("¿Eliminar este equipo y todos sus Pokémon?")) return;
        bsModalEquipo.hide();
        await eliminarEquipo(equipoActualId);
    });

    async function eliminarEquipo(teamId) {
        const res = await window.api.fetchWithAuth(`${API}/teams/${teamId}`, { method: "DELETE" });
        if (res.ok) { mostrarToast("Equipo eliminado.", "warning"); await cargarEquipos(); }
        else mostrarToast("Error al eliminar el equipo.", "danger");
    }

    document.getElementById("btnNuevoEquipo").addEventListener("click", () => {
        document.getElementById("formNuevoEquipo").classList.toggle("d-none");
    });
    document.getElementById("btnCancelarEquipo").addEventListener("click", () => {
        document.getElementById("formNuevoEquipo").classList.add("d-none");
        document.getElementById("errorCrearEquipo").classList.add("d-none");
    });
    document.getElementById("btnGuardarEquipo").addEventListener("click", async () => {
        const errEl  = document.getElementById("errorCrearEquipo");
        const nombre  = document.getElementById("inputNombreEquipo").value.trim();
        const formato = document.getElementById("inputFormatoEquipo").value.trim();
        errEl.classList.add("d-none");
        if (!nombre) { errEl.textContent = "El nombre es obligatorio."; errEl.classList.remove("d-none"); return; }
        const res = await window.api.fetchWithAuth(`${API}/teams`, { method: "POST", body: JSON.stringify({ name: nombre, format: formato }) });
        if (res.ok) {
            document.getElementById("inputNombreEquipo").value = "";
            document.getElementById("inputFormatoEquipo").value = "";
            document.getElementById("formNuevoEquipo").classList.add("d-none");
            mostrarToast("¡Equipo creado!", "success");
            await cargarEquipos();
        } else { errEl.textContent = "Error al crear el equipo."; errEl.classList.remove("d-none"); }
    });

    let bsModalEditar = null;

    // ── Caché de traducciones PokeAPI ─────────────────────
    const cacheTrad = {};
    async function traducirPokeAPI(endpoint, nombre) {
        const key = endpoint + "|" + nombre;
        if (cacheTrad[key] !== undefined) return cacheTrad[key];
        try {
            const slug = nombre.toLowerCase().replace(/ /g, "-").replace(/[^a-z0-9-]/g, "");
            const r = await fetch(`https://pokeapi.co/api/v2/${endpoint}/${slug}`);
            if (!r.ok) { cacheTrad[key] = null; return null; }
            const d = await r.json();
            // Buscar nombre en español
            const names = d.names || d.flavor_text_entries || [];
            const es = names.find(n => (n.language || n.language)?.name === "es");
            const resultado = es ? (es.name || es.flavor_text) : null;
            cacheTrad[key] = resultado;
            return resultado;
        } catch { cacheTrad[key] = null; return null; }
    }

    // Traducir lista de strings en paralelo (máx 8 a la vez para no saturar)
    async function traducirLista(endpoint, lista) {
        const BATCH = 8;
        const mapa = {}; // english -> español
        for (let i = 0; i < lista.length; i += BATCH) {
            const batch = lista.slice(i, i + BATCH);
            const results = await Promise.all(batch.map(nombre => traducirPokeAPI(endpoint, nombre)));
            batch.forEach((nombre, j) => { mapa[nombre] = results[j] || nombre; });
        }
        return mapa;
    }

    // Rellenar select con opciones traducidas, manteniendo value en inglés
    function rellenarSelectTraducido(id, opciones, mapaES, seleccionado) {
        const select = document.getElementById(id);
        if (!select) return;
        select.innerHTML = "";
        opciones.forEach(op => {
            const option = document.createElement("option");
            option.value = op;
            option.textContent = mapaES[op] || op;
            if (op === seleccionado) option.selected = true;
            select.appendChild(option);
        });
    }

    // ── Sliders EV/IV ─────────────────────────────────────
    const EV_STATS = [
        { range: "evHp",  val: "evHpVal"  },
        { range: "evAtk", val: "evAtkVal" },
        { range: "evDef", val: "evDefVal" },
        { range: "evSpA", val: "evSpAVal" },
        { range: "evSpD", val: "evSpDVal" },
        { range: "evSpe", val: "evSpeVal" },
    ];
    const IV_STATS = [
        { range: "ivHp",  val: "ivHpVal"  },
        { range: "ivAtk", val: "ivAtkVal" },
        { range: "ivDef", val: "ivDefVal" },
        { range: "ivSpA", val: "ivSpAVal" },
        { range: "ivSpD", val: "ivSpDVal" },
        { range: "ivSpe", val: "ivSpeVal" },
    ];

    function initSliders() {
        // Sincronizar valor visible + barra EV total
        EV_STATS.forEach(({ range, val }) => {
            const r = document.getElementById(range);
            const v = document.getElementById(val);
            if (!r || !v) return;
            r.addEventListener("input", () => { v.textContent = r.value; actualizarBarraEV(); actualizarColorEV(r); });
            actualizarColorEV(r);
        });
        IV_STATS.forEach(({ range, val }) => {
            const r = document.getElementById(range);
            const v = document.getElementById(val);
            if (!r || !v) return;
            r.addEventListener("input", () => { v.textContent = r.value; actualizarColorIV(r); });
            actualizarColorIV(r);
        });
        actualizarBarraEV();
    }

    function actualizarBarraEV() {
        const total = EV_STATS.reduce((s, { range }) => s + (+document.getElementById(range)?.value || 0), 0);
        const pct = Math.min(100, (total / 510) * 100);
        const barEl = document.getElementById("evTotal");
        const barBar = document.getElementById("evTotalBar");
        if (barEl) barEl.textContent = total;
        if (barBar) {
            barBar.style.width = pct + "%";
            const color = total > 510 ? "#e74c3c" : total > 440 ? "#ff5202" : "#466cca";
            barBar.style.background = color;
            // Colorear también los ranges de EV
            EV_STATS.forEach(({ range }) => actualizarColorEV(document.getElementById(range)));
        }
    }

    function actualizarColorEV(r) {
        if (!r) return;
        const pct = (r.value / 252) * 100;
        const total = EV_STATS.reduce((s, { range }) => s + (+document.getElementById(range)?.value || 0), 0);
        const color = total > 510 ? "#e74c3c" : pct > 80 ? "#ff5202" : "#466cca";
        r.style.setProperty("--range-fill", color);
        r.style.background = `linear-gradient(to right, ${color} ${pct}%, rgba(255,255,255,0.08) ${pct}%)`;
    }

    function actualizarColorIV(r) {
        if (!r) return;
        const pct = (r.value / 31) * 100;
        const color = pct === 100 ? "#2dba7e" : pct < 30 ? "#e74c3c" : "#8ba5e8";
        r.style.background = `linear-gradient(to right, ${color} ${pct}%, rgba(255,255,255,0.08) ${pct}%)`;
    }

    function setSliderVal(id, valId, value) {
        const r = document.getElementById(id);
        const v = document.getElementById(valId);
        if (r) r.value = value;
        if (v) v.textContent = value;
    }

    async function abrirModalEditar(pokemonId) {
        if (!bsModalEditar) {
            bsModalEditar = new bootstrap.Modal(document.getElementById("modalEditarPokemon"));
        }
        try {
            const [pokemonRes, abilitiesRes, movesRes, itemsRes] = await Promise.all([
                window.api.fetchWithAuth(`${API}/pokemon/id/${pokemonId}`),
                window.api.fetchWithAuth(`${API}/pokemon/id/${pokemonId}/abilities`),
                window.api.fetchWithAuth(`${API}/pokemon/${pokemonId}/moves`),
                window.api.fetchWithAuth(`${API}/pokemon/competitive-items`)
            ]);
            if (!pokemonRes.ok) return;
            const p = pokemonRes.data;

            // Nombre del pokémon en el título
            const tituloEl = document.getElementById("editarPokemonTitulo");
            const subtitleEl = document.getElementById("editarPokemonSubtitle");
            if (tituloEl) tituloEl.textContent = "Editar Pokémon";
            if (subtitleEl) subtitleEl.textContent = p.name ? p.name.charAt(0).toUpperCase() + p.name.slice(1) : "";

            // Poner options en inglés primero (carga rápida)
            rellenarSelectTraducido("editItem", itemsRes.data, {}, p.item);
            rellenarSelectTraducido("editAbility", abilitiesRes.data, {}, p.ability);
            ["move1","move2","move3","move4"].forEach((id, i) =>
                rellenarSelectTraducido(id, movesRes.data, {}, p[`move${i+1}`])
            );

            // Sliders EV
            setSliderVal("evHp",  "evHpVal",  p.hpEv ?? 0);
            setSliderVal("evAtk", "evAtkVal", p.atkEv ?? 0);
            setSliderVal("evDef", "evDefVal", p.defEv ?? 0);
            setSliderVal("evSpA", "evSpAVal", p.spAtkEv ?? 0);
            setSliderVal("evSpD", "evSpDVal", p.spDefEv ?? 0);
            setSliderVal("evSpe", "evSpeVal", p.speedEv ?? 0);

            // Sliders IV
            setSliderVal("ivHp",  "ivHpVal",  p.hpIv ?? 31);
            setSliderVal("ivAtk", "ivAtkVal", p.atkIv ?? 31);
            setSliderVal("ivDef", "ivDefVal", p.defIv ?? 31);
            setSliderVal("ivSpA", "ivSpAVal", p.spAtkIv ?? 31);
            setSliderVal("ivSpD", "ivSpDVal", p.spDefIv ?? 31);
            setSliderVal("ivSpe", "ivSpeVal", p.speedIv ?? 31);

            document.getElementById("btnGuardarEdicionPokemon").onclick = () => guardarEdicionPokemon(pokemonId);
            bsModalEditar.show();
            initSliders();

            // Traducir en paralelo (en background, sin bloquear la apertura del modal)
            const mostrarSpinner = id => document.getElementById(id)?.classList.remove("d-none");
            const ocultarSpinner = id => document.getElementById(id)?.classList.add("d-none");

            mostrarSpinner("spinnerItem");
            mostrarSpinner("spinnerAbility");
            mostrarSpinner("spinnerMoves");

            const [mapaItems, mapaAbilities, mapaMoves] = await Promise.all([
                traducirLista("item", itemsRes.data),
                traducirLista("ability", abilitiesRes.data),
                traducirLista("move", movesRes.data),
            ]);

            ocultarSpinner("spinnerItem");
            ocultarSpinner("spinnerAbility");
            ocultarSpinner("spinnerMoves");

            rellenarSelectTraducido("editItem", itemsRes.data, mapaItems, p.item);
            rellenarSelectTraducido("editAbility", abilitiesRes.data, mapaAbilities, p.ability);
            ["move1","move2","move3","move4"].forEach((id, i) =>
                rellenarSelectTraducido(id, movesRes.data, mapaMoves, p[`move${i+1}`])
            );

        } catch (err) {
            console.error(err);
        }
    }

    function rellenarSelect(id, opciones, seleccionado) {
        rellenarSelectTraducido(id, opciones, {}, seleccionado);
    }

    async function guardarEdicionPokemon(pokemonId) {
        const getEV = id => +(document.getElementById(id)?.value || 0);
        const getIV = id => +(document.getElementById(id)?.value || 31);

        const valoresEV = [getEV("evHp"), getEV("evAtk"), getEV("evDef"), getEV("evSpA"), getEV("evSpD"), getEV("evSpe")];
        const totalEV = valoresEV.reduce((a, b) => a + b, 0);
        const errEl = document.getElementById("errorEditarPokemon");

        if (totalEV > 510) {
            errEl.textContent = `Total de EVs: ${totalEV}/510. Reduce alguno.`;
            errEl.classList.remove("d-none");
            return;
        }
        errEl.classList.add("d-none");

        await window.api.fetchWithAuth(`${API}/pokemon/id/${pokemonId}`, {
            method: "PUT",
            body: JSON.stringify({
                item:     document.getElementById("editItem")?.value,
                ability:  document.getElementById("editAbility")?.value,
                move1:    document.getElementById("move1")?.value,
                move2:    document.getElementById("move2")?.value,
                move3:    document.getElementById("move3")?.value,
                move4:    document.getElementById("move4")?.value,
                hpEv:     getEV("evHp"),   atkEv:   getEV("evAtk"),  defEv:   getEV("evDef"),
                spAtkEv:  getEV("evSpA"),  spDefEv: getEV("evSpD"),  speedEv: getEV("evSpe"),
                hpIv:     getIV("ivHp"),   atkIv:   getIV("ivAtk"),  defIv:   getIV("ivDef"),
                spAtkIv:  getIV("ivSpA"),  spDefIv: getIV("ivSpD"),  speedIv: getIV("ivSpe"),
            })
        });

        bsModalEditar.hide();
        await cargarPokemonModal(equipoActualId);
        await cargarPreview(equipoActualId);
    }

    // ── Exportar equipo (formato Showdown) ───────────────
    async function exportarEquipo(teamId) {
        try {
            const listRes = await window.api.fetchWithAuth(`${API}/pokemon/team/${teamId}`);
            if (!listRes.ok || !listRes.data.length) { mostrarToast("El equipo no tiene Pokémon.", "warning"); return; }

            const fullPokemons = await Promise.all(
                listRes.data.map(p => window.api.fetchWithAuth(`${API}/pokemon/id/${p.id}`).then(r => r.data))
            );

            const teamText = fullPokemons.map(p => {
                const lines = [];
                const name = p.name.charAt(0).toUpperCase() + p.name.slice(1);

                if (p.item) lines.push(`${name} @ ${p.item}`);
                else        lines.push(name);

                if (p.ability) lines.push(`Ability: ${p.ability}`);

                lines.push("Tera Type: Normal");

                const evs = [
                    p.hpEv    > 0 ? `${p.hpEv} HP`      : null,
                    p.atkEv   > 0 ? `${p.atkEv} Atk`    : null,
                    p.defEv   > 0 ? `${p.defEv} Def`    : null,
                    p.spAtkEv > 0 ? `${p.spAtkEv} SpA`  : null,
                    p.spDefEv > 0 ? `${p.spDefEv} SpD`  : null,
                    p.speedEv > 0 ? `${p.speedEv} Spe`  : null
                ].filter(Boolean);
                if (evs.length) lines.push(`EVs: ${evs.join(" / ")}`);

                const ivs = [
                    p.hpIv    !== 31 ? `${p.hpIv} HP`      : null,
                    p.atkIv   !== 31 ? `${p.atkIv} Atk`    : null,
                    p.defIv   !== 31 ? `${p.defIv} Def`    : null,
                    p.spAtkIv !== 31 ? `${p.spAtkIv} SpA`  : null,
                    p.spDefIv !== 31 ? `${p.spDefIv} SpD`  : null,
                    p.speedIv !== 31 ? `${p.speedIv} Spe`  : null
                ].filter(Boolean);
                if (ivs.length) lines.push(`IVs: ${ivs.join(" / ")}`);

                [p.move1, p.move2, p.move3, p.move4]
                    .filter(m => m && m.trim())
                    .forEach(m => lines.push(`- ${m}`));

                return lines.join("\n");
            }).join("\n\n");

            await navigator.clipboard.writeText(teamText);
            mostrarToast("¡Equipo copiado en formato Showdown!", "success");

        } catch (err) {
            console.error(err);
            mostrarToast("Error al exportar el equipo.", "danger");
        }
    }

    cargarEquipos();
}

})();
