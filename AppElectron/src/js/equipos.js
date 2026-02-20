(function() {
const API = "https://pokemon-backend-849x.onrender.com/api";

(async () => {
    const token = await window.api.getToken();
    if (!token) { window.location.href = "inicio_sesion.html"; return; }
    initEquipos();
})();

async function initEquipos() {

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

<<<<<<< HEAD
=======
    // ── Paginación ─────────────────────────────────────
    const EQUIPOS_POR_PAGINA = 6;
    let paginaActual = 1;
    let todosLosEquipos = [];

    function crearCardEquipo(eq) {
        const wrap = document.createElement("div");
        wrap.className = "equipo-card-wrap";
        wrap.innerHTML = `
            <div class="card equipo-card">
                <div class="card-body d-flex flex-column">
                    <h5 class="card-title">${eq.name}</h5>
                    <p class="card-text mb-3">Formato: ${eq.format || "Sin formato"}</p>
                    <div class="pokemon-preview mb-3" id="preview-${eq.id}">
                        <div class="spinner-border spinner-border-sm text-warning" style="grid-column:1/-1;align-self:center;justify-self:center;"></div>
                    </div>
                    <div class="mt-auto d-flex gap-2">
                        <button class="btn btn-primary flex-fill btn-ver" data-id="${eq.id}" data-name="${eq.name}">Ver / Editar</button>
                        <button class="btn btn-danger btn-borrar d-flex align-items-center" data-id="${eq.id}">
                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">
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

>>>>>>> bbfd4754fbcce38996193e17afeb82d8c4de8f85
    async function cargarEquipos() {
        document.getElementById("spinnerEquipos").classList.remove("d-none");
        document.getElementById("listaEquipos").classList.add("d-none");
        document.getElementById("sinEquipos").classList.add("d-none");

        const res = await window.api.fetchWithAuth(`${API}/teams/me`);
        document.getElementById("spinnerEquipos").classList.add("d-none");

        if (!res.ok) { mostrarToast("Error al cargar equipos.", "danger"); return; }

        const lista = document.getElementById("listaEquipos");
        lista.innerHTML = "";

        if (!res.data.length) { document.getElementById("sinEquipos").classList.remove("d-none"); return; }

        lista.classList.remove("d-none");
        res.data.forEach(eq => {
            const col = document.createElement("div");
            col.className = "col-md-4";
            col.innerHTML = `
                <div class="card h-100 equipo-card">
                    <div class="card-body d-flex flex-column" id="card-equipos">
                        <h5 class="card-title">${eq.name}</h5>
                        <p class="card-text mb-3">Formato: ${eq.format || "Sin formato"}</p>
                        <div class="pokemon-preview mb-3" id="preview-${eq.id}">
                            <div class="spinner-border spinner-border-sm text-warning"></div>
                        </div>
                        <div class="mt-auto d-flex gap-2">
                            <button class="btn btn-primary flex-fill btn-ver" data-id="${eq.id}" data-name="${eq.name}">Ver / Editar</button>
                            <button class="btn btn-secondary btn-exportar d-flex align-items-center" data-id="${eq.id}" title="Exportar en formato Showdown"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-clipboard" viewBox="0 0 16 16">
  <path d="M4 1.5H3a2 2 0 0 0-2 2V14a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V3.5a2 2 0 0 0-2-2h-1v1h1a1 1 0 0 1 1 1V14a1 1 0 0 1-1 1H3a1 1 0 0 1-1-1V3.5a1 1 0 0 1 1-1h1z"/>
  <path d="M9.5 1a.5.5 0 0 1 .5.5v1a.5.5 0 0 1-.5.5h-3a.5.5 0 0 1-.5-.5v-1a.5.5 0 0 1 .5-.5zm-3-1A1.5 1.5 0 0 0 5 1.5v1A1.5 1.5 0 0 0 6.5 4h3A1.5 1.5 0 0 0 11 2.5v-1A1.5 1.5 0 0 0 9.5 0z"/>
</svg></button>
                            <button class="btn btn-danger btn-borrar d-flex align-items-center" data-id="${eq.id}"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">
  <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
</svg></button>
                        </div>
                    </div>
                </div>`;
            lista.appendChild(col);
            cargarPreview(eq.id);
        });

        lista.querySelectorAll(".btn-ver").forEach(btn =>
            btn.addEventListener("click", () => abrirModal(btn.dataset.id, btn.dataset.name)));
        lista.querySelectorAll(".btn-exportar").forEach(btn =>
            btn.addEventListener("click", () => exportarEquipo(btn.dataset.id)));
        lista.querySelectorAll(".btn-borrar").forEach(btn =>
            btn.addEventListener("click", () => eliminarEquipo(btn.dataset.id)));
    }

    async function cargarPreview(teamId) {
        const el  = document.getElementById(`preview-${teamId}`);
        const res = await window.api.fetchWithAuth(`${API}/pokemon/team/${teamId}`);
        el.innerHTML = "";
        if (!res.ok || !res.data.length) { el.innerHTML = `<span class="text-muted small">Sin Pokémon</span>`; return; }
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

        col.innerHTML = `
            <div class="card p-2 text-center equipo-pokemon-card">
                <img src="${p.image}" alt="${p.name}" style="width:80px;margin:auto;">
                <p class="mb-1 fw-semibold text-capitalize">${p.name}</p>

                <div class="d-flex justify-content-center gap-2 flex-wrap mb-2">
                    ${p.primaryType ? `<span class="badge bg-secondary">${p.primaryType}</span>` : ""}
                    ${p.secondaryType ? `<span class="badge bg-secondary">${p.secondaryType}</span>` : ""}
                </div>

                <div class="d-flex justify-content-center gap-2">
                    <button class="btn btn-sm btn-warning btn-editar" data-id="${p.id}">
                        Editar
                    </button>

                    <button class="btn btn-sm btn-danger btn-quitar" data-id="${p.id}">
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

    // ── Crear equipo ─────────────────────────────────────
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

        async function abrirModalEditar(pokemonId) {

            if (!bsModalEditar) {
                bsModalEditar = new bootstrap.Modal(
                    document.getElementById("modalEditarPokemon")
                );
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

                rellenarSelect("editItem", itemsRes.data, p.item);
                rellenarSelect("editAbility", abilitiesRes.data, p.ability);

                rellenarSelect("move1", movesRes.data, p.move1);
                rellenarSelect("move2", movesRes.data, p.move2);
                rellenarSelect("move3", movesRes.data, p.move3);
                rellenarSelect("move4", movesRes.data, p.move4);

                evHp.value = p.hpEv ?? 0;
                evAtk.value = p.atkEv ?? 0;
                evDef.value = p.defEv ?? 0;
                evSpA.value = p.spAtkEv ?? 0;
                evSpD.value = p.spDefEv ?? 0;
                evSpe.value = p.speedEv ?? 0;

                ivHp.value = p.hpIv ?? 31;
                ivAtk.value = p.atkIv ?? 31;
                ivDef.value = p.defIv ?? 31;
                ivSpA.value = p.spAtkIv ?? 31;
                ivSpD.value = p.spDefIv ?? 31;
                ivSpe.value = p.speedIv ?? 31;

                document.getElementById("btnGuardarEdicionPokemon")
                    .onclick = () => guardarEdicionPokemon(pokemonId);

                bsModalEditar.show();

            } catch (err) {
                console.error(err);
            }
        }

        function rellenarSelect(id, opciones, seleccionado) {
            const select = document.getElementById(id);
            select.innerHTML = "";

            opciones.forEach(op => {
                const option = document.createElement("option");
                option.value = op;
                option.textContent = op;
                if (op === seleccionado) option.selected = true;
                select.appendChild(option);
            });
        }
async function guardarEdicionPokemon(pokemonId) {

    const valoresEV = [
        +evHp.value,
        +evAtk.value,
        +evDef.value,
        +evSpA.value,
        +evSpD.value,
        +evSpe.value
    ];

    for (let ev of valoresEV) {
        if (ev < 0 || ev > 252) {
            const err = document.getElementById("errorEditarPokemon");
            err.textContent = "Cada stat puede tener máximo 252 EVs";
            err.classList.remove("d-none");
            return;
        }
    }

    const totalEV = valoresEV.reduce((a, b) => a + b, 0);

    if (totalEV > 510) {
        const err = document.getElementById("errorEditarPokemon");
        err.textContent = "Máximo total 510 EVs";
        err.classList.remove("d-none");
        return;
    }

    await window.api.fetchWithAuth(
        `${API}/pokemon/id/${pokemonId}`,
        {
            method: "PUT",
            body: JSON.stringify({
                item: editItem.value,
                ability: editAbility.value,
                move1: move1.value,
                move2: move2.value,
                move3: move3.value,
                move4: move4.value,
                hpEv: +evHp.value,
                atkEv: +evAtk.value,
                defEv: +evDef.value,
                spAtkEv: +evSpA.value,
                spDefEv: +evSpD.value,
                speedEv: +evSpe.value,
                hpIv: +ivHp.value,
                atkIv: +ivAtk.value,
                defIv: +ivDef.value,
                spAtkIv: +ivSpA.value,
                spDefIv: +ivSpD.value,
                speedIv: +ivSpe.value
            })
        }
    );

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
                    p.hpEv    > 0 ? `${p.hpEv} HP`    : null,
                    p.atkEv   > 0 ? `${p.atkEv} Atk`   : null,
                    p.defEv   > 0 ? `${p.defEv} Def`   : null,
                    p.spAtkEv > 0 ? `${p.spAtkEv} SpA` : null,
                    p.spDefEv > 0 ? `${p.spDefEv} SpD` : null,
                    p.speedEv > 0 ? `${p.speedEv} Spe` : null
                ].filter(Boolean);
                if (evs.length) lines.push(`EVs: ${evs.join(" / ")}`);

                const ivs = [
                    p.hpIv    !== 31 ? `${p.hpIv} HP`    : null,
                    p.atkIv   !== 31 ? `${p.atkIv} Atk`   : null,
                    p.defIv   !== 31 ? `${p.defIv} Def`   : null,
                    p.spAtkIv !== 31 ? `${p.spAtkIv} SpA` : null,
                    p.spDefIv !== 31 ? `${p.spDefIv} SpD` : null,
                    p.speedIv !== 31 ? `${p.speedIv} Spe` : null
                ].filter(Boolean);
                if (ivs.length) lines.push(`IVs: ${ivs.join(" / ")}`);

                [p.move1, p.move2, p.move3, p.move4]
                    .filter(m => m && m.trim())
                    .forEach(m => lines.push(`- ${m}`));

                return lines.join("\n");
            }).join("\n\n");

            await navigator.clipboard.writeText(teamText);
            mostrarToast("Equipo exportado en el portapapeles.", "success");

        } catch (err) {
            console.error(err);
            mostrarToast("Error al exportar el equipo.", "danger");
        }
    }

    cargarEquipos();
}
    
})();