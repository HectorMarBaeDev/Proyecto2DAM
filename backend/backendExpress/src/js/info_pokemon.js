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
        pokemonName   = pokemon.name;

        const speciesRes  = await fetch(pokemon.species.url);
        const speciesData = await speciesRes.json();
        const nombreES    = speciesData.names.find(n => n.language.name === "es")?.name || pokemon.name;

        const SEP = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-grip-vertical" viewBox="0 0 16 16"><path d="M7 2a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 5a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/></svg>`;
        const tiposHTML = pokemon.types.map(t => {
            const nombre = Object.keys(tiposEN).find(k => tiposEN[k] === t.type.name) || t.type.name;
            return `<div class="d-flex align-items-center gap-1"><div class="recorte"><img src="./assets/types/${typeIcons[nombre]||'1.png'}" class="tipo-icon"></div><span>${nombre}</span></div>`;
        }).join(SEP);

        container.innerHTML = `
            <div class="card mx-auto">
                <div class="d-flex gap-4 flex-wrap">
                    <img src="${pokemon.sprites.front_default}" class="card-img-top" alt="${nombreES}" id="imgPokemon">
                    <div class="card-body d-grid align-items-start gap-2">
                        <h5 class="card-title h2">${nombreES}</h5>
                        <p><strong>ID Pokédex:</strong> ${pokemon.id}</p>
                        <div class="d-flex gap-2 flex-wrap align-items-center mb-3">
                            <p id="pTipos" class="mb-0"><strong>Tipo/s:</strong></p>${tiposHTML}
                        </div>
                        <p><strong>Altura:</strong> ${pokemon.height/10} m</p>
                        <p><strong>Peso:</strong> ${pokemon.weight/10} kg</p>
                    </div>
                    <div class="card-body d-grid align-items-start gap-2">
                        <h5 class="card-title">Estadísticas base</h5>
                        <p><strong>PS:</strong> ${pokemon.stats.find(s=>s.stat.name==="hp").base_stat}</p>
                        <p><strong>Velocidad:</strong> ${pokemon.stats.find(s=>s.stat.name==="speed").base_stat}</p>
                        <p><strong>Ataque:</strong> ${pokemon.stats.find(s=>s.stat.name==="attack").base_stat}</p>
                        <p><strong>Defensa:</strong> ${pokemon.stats.find(s=>s.stat.name==="defense").base_stat}</p>
                        <p><strong>Ataque especial:</strong> ${pokemon.stats.find(s=>s.stat.name==="special-attack").base_stat}</p>
                        <p><strong>Defensa especial:</strong> ${pokemon.stats.find(s=>s.stat.name==="special-defense").base_stat}</p>
                    </div>
                </div>
            </div>
            <div class="d-flex mt-4 gap-2" id="divBotones">
                <a href="./index.html"><button class="btn btn-secondary btn-lg">Volver</button></a>
                <button class="btn btn-primary btn-lg" id="btnAnadirEquipo">Añadir al equipo</button>
            </div>`;

        document.getElementById("btnAnadirEquipo").addEventListener("click", abrirModalEquipos);

    } catch(err) { container.innerHTML = `<p>Error al cargar el Pokémon: ${err.message}</p>`; }
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
