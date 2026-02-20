const API = "https://pokemon-backend-849x.onrender.com/api";

(async () => {
    const token = await window.api.getToken();
    if (!token) { window.location.href = "inicio_sesion.html"; return; }
    initInfoPokemon();
})();

const tiposEN = {
    "Normal":"normal","Lucha":"fighting","Volador":"flying","Veneno":"poison",
    "Tierra":"ground","Roca":"rock","Bicho":"bug","Fantasma":"ghost","Acero":"steel",
    "Fuego":"fire","Agua":"water","Planta":"grass","Eléctrico":"electric",
    "Psíquico":"psychic","Hielo":"ice","Dragón":"dragon","Siniestro":"dark","Hada":"fairy"
};
const typeIcons = {
    "Normal":"1.png","Lucha":"2.png","Volador":"3.png","Veneno":"4.png","Tierra":"5.png",
    "Roca":"6.png","Bicho":"7.png","Fantasma":"8.png","Acero":"9.png","Fuego":"10.png",
    "Agua":"11.png","Planta":"12.png","Eléctrico":"13.png","Psíquico":"14.png",
    "Hielo":"15.png","Dragón":"16.png","Siniestro":"17.png","Hada":"18.png"
};
const typeColors = {
    "Normal":     { base: "#9a9a7a", light: "#ccc8a0" },
    "Lucha":      { base: "#b03020", light: "#e06050" },
    "Volador":    { base: "#8ba8f0", light: "#b8ccff" },
    "Veneno":     { base: "#903890", light: "#c070c0" },
    "Tierra":     { base: "#d4a840", light: "#f0d080" },
    "Roca":       { base: "#a89830", light: "#d0c060" },
    "Bicho":      { base: "#88a010", light: "#b8d040" },
    "Fantasma":   { base: "#6050a0", light: "#9070d0" },
    "Acero":      { base: "#9898c0", light: "#c8c8e8" },
    "Fuego":      { base: "#e06820", light: "#f0a060" },
    "Agua":       { base: "#4878d8", light: "#80a8f8" },
    "Planta":     { base: "#50a830", light: "#80d060" },
    "Eléctrico":  { base: "#d8b010", light: "#f8e040" },
    "Psíquico":   { base: "#d84870", light: "#f880a0" },
    "Hielo":      { base: "#60c8c8", light: "#98e8e8" },
    "Dragón":     { base: "#5018e8", light: "#8060ff" },
    "Siniestro":  { base: "#584840", light: "#907870" },
    "Hada":       { base: "#d870a0", light: "#f0a8c8" },
};

let pokemonName = null;
let bsModalInfo = null;

function initInfoPokemon() {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");
    if (!id) { document.getElementById("detalleContainer").innerHTML = "<p>No se ha especificado ningún Pokémon.</p>"; return; }
    cargarDetalle(id);
}

async function cargarDetalle(id) {
    const container = document.getElementById("detalleContainer");
    container.innerHTML = `<div class="d-flex justify-content-center mt-5"><div class="spinner-grow text-warning"><span class="visually-hidden">Loading...</span></div></div>`;

    try {
        const res = await fetch(`https://pokeapi.co/api/v2/pokemon/${id}`);
        if (!res.ok) throw new Error("No encontrado");
        const pokemon = await res.json();
        pokemonName = pokemon.name;

        const speciesRes  = await fetch(pokemon.species.url);
        const speciesData = await speciesRes.json();
        const nombreES    = speciesData.names.find(n => n.language.name === "es")?.name || pokemon.name;

        // Tipo primario para los colores
        const tipoPrimarioEN = pokemon.types[0].type.name;
        const tipoPrimarioES = Object.keys(tiposEN).find(k => tiposEN[k] === tipoPrimarioEN) || tipoPrimarioEN;
        const colores = typeColors[tipoPrimarioES] || { base: "#888", light: "#bbb" };

        // Aplicar variables CSS de color al :root
        document.documentElement.style.setProperty("--primary-type", colores.base);
        document.documentElement.style.setProperty("--primary-type-light", colores.light);

        // Badges de tipos
        const tiposHTML = pokemon.types.map(t => {
            const nombre = Object.keys(tiposEN).find(k => tiposEN[k] === t.type.name) || t.type.name;
            return `<div class="tipo-badge"><div class="recorte"><img src="./assets/types/${typeIcons[nombre]||'1.png'}"></div><span>${nombre}</span></div>`;
        }).join(`<span class="tipo-sep">·</span>`);

        // Stats
        const statsData = [
            { name: "PS",      key: "hp" },
            { name: "Ataque",  key: "attack" },
            { name: "Defensa", key: "defense" },
            { name: "Atq. Esp", key: "special-attack" },
            { name: "Def. Esp", key: "special-defense" },
            { name: "Veloc.",  key: "speed" },
        ];
        const statsHTML = statsData.map(s => {
            const val = pokemon.stats.find(x => x.stat.name === s.key)?.base_stat ?? 0;
            const pct = Math.min(100, Math.round(val / 255 * 100));
            return `
            <div class="stat-row">
                <span class="stat-name">${s.name}</span>
                <span class="stat-val">${val}</span>
                <div class="stat-bar-bg"><div class="stat-bar" style="width:${pct}%"></div></div>
            </div>`;
        }).join("");

        // Imagen de mayor resolución si está disponible
        const imgSrc = pokemon.sprites.other?.["official-artwork"]?.front_default
                    || pokemon.sprites.front_default;

        container.innerHTML = `
            <div class="pokemon-card mx-auto">
                <div class="d-flex flex-wrap">
                    <!-- Columna imagen -->
                    <div class="pokemon-img-section">
                        <img src="${imgSrc}" alt="${nombreES}" id="imgPokemon">
                        <span class="pokemon-id-badge">#${String(pokemon.id).padStart(3,"0")}</span>
                    </div>

                    <!-- Columna info -->
                    <div class="pokemon-info-section">
                        <h2 class="pokemon-name">${nombreES}</h2>

                        <div class="d-flex gap-2 flex-wrap align-items-center info-group">
                            ${tiposHTML}
                        </div>

                        <div class="info-group">
                            <div class="info-label">Altura</div>
                            <div class="info-value">${pokemon.height / 10} m</div>
                        </div>
                        <div class="info-group">
                            <div class="info-label">Peso</div>
                            <div class="info-value">${pokemon.weight / 10} kg</div>
                        </div>
                    </div>
                </div>

                <!-- Stats -->
                <div class="stats-section">
                    <h6>Estadísticas base</h6>
                    ${statsHTML}
                </div>
            </div>

            <div class="d-flex mt-3 gap-2" id="divBotones">
                <a href="./index.html" class="btn btn-volver btn-lg">← Volver</a>
                <button class="btn btn-anadir btn-lg" id="btnAnadirEquipo">Añadir al equipo</button>
            </div>`;

        document.getElementById("btnAnadirEquipo").addEventListener("click", abrirModalEquipos);

    } catch(err) { container.innerHTML = `<p class="text-white">Error al cargar el Pokémon: ${err.message}</p>`; }
}

function crearModalEquipos() {
    if (document.getElementById("modalEquiposInfo")) return;
    document.body.insertAdjacentHTML("beforeend", `
    <div class="modal fade" id="modalEquiposInfo" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Añadir al equipo</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="listaEquiposInfo"></div>
                    <hr>
                    <p class="fw-semibold">Crear nuevo equipo</p>
                    <input type="text" class="form-control mb-2" id="nuevoNombreEq" placeholder="Nombre">
                    <input type="text" class="form-control mb-2" id="nuevoFormatoEq" placeholder="Formato">
                    <div id="errCrearEq" class="alert alert-danger d-none py-2"></div>
                    <button class="btn btn-success w-100" id="btnCrearEqInfo">Crear equipo</button>
                </div>
            </div>
        </div>
    </div>
    <div class="toast-container position-fixed bottom-0 end-0 p-3">
        <div id="toastInfo" class="toast align-items-center border-0" role="alert">
            <div class="d-flex">
                <div class="toast-body" id="toastInfoBody"></div>
                <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
            </div>
        </div>
    </div>`);
    document.getElementById("btnCrearEqInfo").addEventListener("click", crearEquipoInfo);
}

async function abrirModalEquipos() {
    crearModalEquipos();
    if (!bsModalInfo) bsModalInfo = new bootstrap.Modal(document.getElementById("modalEquiposInfo"));
    bsModalInfo.show();
    await cargarEquiposInfo();
}

async function cargarEquiposInfo() {
    const lista = document.getElementById("listaEquiposInfo");
    lista.innerHTML = `<div class="d-flex justify-content-center"><div class="spinner-border text-warning"></div></div>`;
    const res = await window.api.fetchWithAuth(`${API}/teams/me`);
    lista.innerHTML = "";
    if (!res.ok) { lista.innerHTML = `<p class="text-danger">Error.</p>`; return; }
    if (!res.data.length) { lista.innerHTML = `<p class="text-muted">No tienes equipos. Crea uno abajo.</p>`; return; }
    lista.innerHTML = `<p class="fw-semibold mb-2">Selecciona un equipo:</p>`;
    res.data.forEach(eq => {
        const btn = document.createElement("button");
        btn.className = "btn btn-outline-primary w-100 mb-2";
        btn.textContent = `${eq.name} (${eq.format||"Sin formato"})`;
        btn.addEventListener("click", () => anadirAEquipo(eq.id));
        lista.appendChild(btn);
    });
}

async function anadirAEquipo(teamId) {
    const res = await window.api.fetchWithAuth(`${API}/pokemon?teamId=${teamId}`, {
        method: "POST", body: JSON.stringify({ identifier: pokemonName })
    });
    bsModalInfo.hide();
    const toast = document.getElementById("toastInfo");
    if (res.ok) {
        toast.className = "toast align-items-center text-bg-success border-0";
        document.getElementById("toastInfoBody").textContent = `¡${pokemonName} añadido!`;
    } else if (res.status === 400) {
        toast.className = "toast align-items-center text-bg-danger border-0";
        document.getElementById("toastInfoBody").textContent = "El equipo ya tiene 6 Pokémon.";
    } else {
        toast.className = "toast align-items-center text-bg-danger border-0";
        document.getElementById("toastInfoBody").textContent = "Error al añadir.";
    }
    bootstrap.Toast.getOrCreateInstance(toast).show();
}

async function crearEquipoInfo() {
    const nombre = document.getElementById("nuevoNombreEq").value.trim();
    const formato = document.getElementById("nuevoFormatoEq").value.trim();
    const errEl = document.getElementById("errCrearEq");
    errEl.classList.add("d-none");
    if (!nombre) { errEl.textContent = "El nombre es obligatorio."; errEl.classList.remove("d-none"); return; }
    const res = await window.api.fetchWithAuth(`${API}/teams`, {
        method: "POST", body: JSON.stringify({ name: nombre, format: formato })
    });
    if (res.ok) {
        document.getElementById("nuevoNombreEq").value = "";
        document.getElementById("nuevoFormatoEq").value = "";
        await cargarEquiposInfo();
    } else { errEl.textContent = "Error al crear."; errEl.classList.remove("d-none"); }
}
