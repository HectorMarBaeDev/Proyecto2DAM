(function() {
const API = "https://pokemon-backend-849x.onrender.com/api";

(async () => {
    const token = await window.api.getToken();
    if (!token) { window.location.href = "inicio_sesion.html"; return; }
    initEquipos();
})();

async function initEquipos() {

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
                    <button class="btn btn-sm btn-danger btn-quitar" data-id="${p.id}">Quitar</button>
                </div>`;
            container.appendChild(col);
        });

        container.querySelectorAll(".btn-quitar").forEach(btn =>
            btn.addEventListener("click", () => quitarPokemon(btn.dataset.id)));
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

    cargarEquipos();
}

})();
