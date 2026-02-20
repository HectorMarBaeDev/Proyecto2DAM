(function() {
const API = "https://pokemon-backend-849x.onrender.com/api";

// ── Protección de ruta (async) ───────────────────────────
(async () => {
    const token = await window.api.getToken();
    if (!token) { window.location.href = "inicio_sesion.html"; return; }

    init();
})();

async function init() {
    // ── Elementos DOM ────────────────────────────────────
    const divPrincipal        = document.getElementById("divPrincipal");
    const navbar              = document.querySelector(".navPrincipal");
    const searchForm          = document.getElementById("searchForm");
    const searchInput         = document.getElementById("searchInput");
    const cardsContainer      = document.getElementById("cardsContainer");
    const paginationContainer = document.getElementById("paginationContainer");
    const filtrar             = document.getElementById("filtrar");

    const totalPokemon = 1025;
    const pageSize     = 10;
    const totalPages   = Math.ceil(totalPokemon / pageSize);

    let currentPage         = 1;
    let filteredPokemonList = null;

    // ── Mapas tipos ──────────────────────────────────────
    const tiposEN = {
        "Normal": "normal", "Lucha": "fighting", "Volador": "flying",
        "Veneno": "poison", "Tierra": "ground", "Roca": "rock",
        "Bicho": "bug", "Fantasma": "ghost", "Acero": "steel",
        "Fuego": "fire", "Agua": "water", "Planta": "grass",
        "Eléctrico": "electric", "Psíquico": "psychic", "Hielo": "ice",
        "Dragón": "dragon", "Siniestro": "dark", "Hada": "fairy"
    };
    const typeIcons = {
        "Normal":"1.png","Lucha":"2.png","Volador":"3.png","Veneno":"4.png",
        "Tierra":"5.png","Roca":"6.png","Bicho":"7.png","Fantasma":"8.png",
        "Acero":"9.png","Fuego":"10.png","Agua":"11.png","Planta":"12.png",
        "Eléctrico":"13.png","Psíquico":"14.png","Hielo":"15.png",
        "Dragón":"16.png","Siniestro":"17.png","Hada":"18.png"
    };
    const tiposDisponibles = [
        "Normal","Fuego","Agua","Planta","Eléctrico","Hielo","Lucha","Veneno",
        "Tierra","Volador","Psíquico","Bicho","Roca","Fantasma","Dragón","Siniestro","Acero","Hada"
    ];
    const generaciones = [
        {nombre:"Gen 1",min:1,max:151},{nombre:"Gen 2",min:152,max:251},
        {nombre:"Gen 3",min:252,max:386},{nombre:"Gen 4",min:387,max:493},
        {nombre:"Gen 5",min:494,max:649},{nombre:"Gen 6",min:650,max:721},
        {nombre:"Gen 7",min:722,max:809},{nombre:"Gen 8",min:810,max:905},
        {nombre:"Gen 9",min:906,max:1025}
    ];

    // ── Navbar ───────────────────────────────────────────
    divPrincipal.addEventListener("scroll", () => {
        navbar.classList.toggle("shrink", divPrincipal.scrollTop > 40);
    });

    document.getElementById("cerrarSesion").querySelector("a").addEventListener("click", async (e) => {
        e.preventDefault();
        await window.api.clearToken();
        await window.api.clearUser();
        window.location.href = "inicio_sesion.html";
    });

    document.getElementById("miEquipo").querySelector("a").addEventListener("click", (e) => {
        e.preventDefault();
        window.location.href = "equipos.html";
    });

    // ── Filtros ──────────────────────────────────────────
    const tiposContainer = document.getElementById("filtroTipos");
    const genContainer   = document.getElementById("filtroGeneraciones");
    tiposDisponibles.forEach(tipo => {
        tiposContainer.innerHTML += `<div class="form-check"><input class="form-check-input filtro-tipo" type="checkbox" value="${tipo}" id="tipo-${tipo}"><label class="form-check-label" for="tipo-${tipo}">${tipo}</label></div>`;
    });
    generaciones.forEach((gen, i) => {
        genContainer.innerHTML += `<div class="form-check"><input class="form-check-input filtro-gen" type="checkbox" value="${i}" id="gen-${i}"><label class="form-check-label" for="gen-${i}">${gen.nombre}</label></div>`;
    });

    // ── Render card ──────────────────────────────────────
    const SEP = `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-grip-vertical" viewBox="0 0 16 16"><path d="M7 2a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 5a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/></svg>`;

    function renderCard(p) {
        const tiposHTML = p.tiposTraducidos.map(tipo =>
            `<div class="recorte"><img src="./assets/types/${typeIcons[tipo]||'1.png'}" alt="${tipo}" class="tipo-icon"></div><span class="txt-tipo">${tipo}</span>`
        ).join(SEP);

        const card = document.createElement("div");
        card.className = "cardPokemon";
        card.innerHTML = `
            <div class="card" style="width:18rem;">
                <img src="${p.sprites.front_default}" class="card_img rounded mx-auto d-block" alt="${p.nombreTraducido}">
                <div class="card-body">
                    <h5 class="card-title">${p.nombreTraducido}</h5>
                    <div class="card-text d-flex gap-2 flex-wrap align-items-center mb-3">${tiposHTML}</div>
                    <div class="d-flex justify-content-between">
                        <button class="btn btn-primary btn-anadir">Añadir</button>
                        <a href="info_pokemon.html?id=${p.id}" class="btn btn-secondary">Ver info</a>
                    </div>
                </div>
            </div>`;
        card.querySelector(".btn-anadir").addEventListener("click", () => abrirModalAnadir(p.name));
        cardsContainer.appendChild(card);
    }

    // ── Modal añadir al equipo ────────────────────────────
    function crearModalEquipo() {
        if (document.getElementById("modalAnadirEquipo")) return;
        document.body.insertAdjacentHTML("beforeend", `
        <div class="modal fade" id="modalAnadirEquipo" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Añadir al equipo</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p id="pNombreModal" class="fw-bold"></p>
                        <div id="listaEquiposModal"></div>
                        <hr>
                        <p class="fw-semibold">Crear nuevo equipo</p>
                        <input type="text" class="form-control mb-2" id="nuevoNombreEquipo" placeholder="Nombre del equipo">
                        <input type="text" class="form-control mb-2" id="nuevoFormatoEquipo" placeholder="Formato (ej: OU, VGC...)">
                        <div id="errorNuevoEquipo" class="alert alert-danger d-none py-2"></div>
                        <button class="btn btn-success w-100" id="btnCrearEquipo">Crear equipo</button>
                    </div>
                </div>
            </div>
        </div>
        <div class="toast-container position-fixed bottom-0 end-0 p-3">
            <div id="toastMsg" class="toast align-items-center border-0" role="alert">
                <div class="d-flex">
                    <div class="toast-body" id="toastBody"></div>
                    <button type="button" class="btn-close me-2 m-auto" data-bs-dismiss="toast"></button>
                </div>
            </div>
        </div>`);
        document.getElementById("btnCrearEquipo").addEventListener("click", crearEquipo);
    }

    let bsModalEquipo      = null;
    let pokemonPendiente   = null;

    async function abrirModalAnadir(name) {
        crearModalEquipo();
        pokemonPendiente = name;
        document.getElementById("pNombreModal").textContent = `Pokémon: ${name}`;
        if (!bsModalEquipo) bsModalEquipo = new bootstrap.Modal(document.getElementById("modalAnadirEquipo"));
        bsModalEquipo.show();
        await cargarEquiposEnModal();
    }

    async function cargarEquiposEnModal() {
        const lista = document.getElementById("listaEquiposModal");
        lista.innerHTML = `<div class="d-flex justify-content-center"><div class="spinner-border text-warning"></div></div>`;
        const res = await window.api.fetchWithAuth(`${API}/teams/me`);
        lista.innerHTML = "";
        if (!res.ok) { lista.innerHTML = `<p class="text-danger">Error al cargar equipos.</p>`; return; }
        if (!res.data.length) { lista.innerHTML = `<p class="text-muted">No tienes equipos. Crea uno abajo.</p>`; return; }
        lista.innerHTML = `<p class="fw-semibold">Selecciona un equipo:</p>`;
        res.data.forEach(eq => {
            const btn = document.createElement("button");
            btn.className = "btn btn-outline-primary w-100 mb-2";
            btn.textContent = `${eq.name} (${eq.format || "Sin formato"})`;
            btn.addEventListener("click", () => anadirPokemon(eq.id));
            lista.appendChild(btn);
        });
    }

    async function anadirPokemon(teamId) {
        const res = await window.api.fetchWithAuth(`${API}/pokemon?teamId=${teamId}`, {
            method: "POST", body: JSON.stringify({ identifier: pokemonPendiente })
        });
        bsModalEquipo.hide();
        if (res.ok) mostrarToast(`¡${pokemonPendiente} añadido!`, "success");
        else if (res.status === 400) mostrarToast("El equipo ya tiene 6 Pokémon.", "danger");
        else mostrarToast("Error al añadir el Pokémon.", "danger");
    }

    async function crearEquipo() {
        const nombre  = document.getElementById("nuevoNombreEquipo").value.trim();
        const formato = document.getElementById("nuevoFormatoEquipo").value.trim();
        const errEl   = document.getElementById("errorNuevoEquipo");
        errEl.classList.add("d-none");
        if (!nombre) { errEl.textContent = "El nombre es obligatorio."; errEl.classList.remove("d-none"); return; }
        const res = await window.api.fetchWithAuth(`${API}/teams`, {
            method: "POST", body: JSON.stringify({ name: nombre, format: formato })
        });
        if (res.ok) {
            document.getElementById("nuevoNombreEquipo").value = "";
            document.getElementById("nuevoFormatoEquipo").value = "";
            await cargarEquiposEnModal();
        } else { errEl.textContent = "Error al crear el equipo."; errEl.classList.remove("d-none"); }
    }

    function mostrarToast(msg, tipo = "success") {
        const el = document.getElementById("toastMsg");
        el.className = `toast align-items-center text-bg-${tipo} border-0`;
        document.getElementById("toastBody").textContent = msg;
        bootstrap.Toast.getOrCreateInstance(el).show();
    }

    // ── Cargar página ────────────────────────────────────
    async function fetchPokemonData(list) {
        return Promise.all(list.map(p =>
            fetch(p.url).then(r => r.json())
                .then(pd => fetch(pd.species.url).then(r => r.json())
                    .then(sd => {
                        const nombreES = sd.names.find(n => n.language.name === "es");
                        return Promise.all(pd.types.map(t =>
                            fetch(t.type.url).then(r => r.json())
                                .then(td => (td.names.find(n => n.language.name === "es") || {}).name || t.type.name)
                        )).then(tiposES => ({ ...pd, nombreTraducido: nombreES ? nombreES.name : pd.name, tiposTraducidos: tiposES }));
                    }))
        ));
    }

    function cargarPagina(page) {
        const existente = document.getElementById("mensajeSinResultados");
        if (existente) existente.remove();
        divPrincipal.classList.replace("fade-in", "fade-out");
        mostrarSpinner();

        setTimeout(async () => {
            cardsContainer.innerHTML = "";
            paginationContainer.innerHTML = "";
            currentPage = page;
            try {
                let pokemons;
                if (filteredPokemonList) {
                    const slice = filteredPokemonList.slice((page-1)*pageSize, page*pageSize);
                    pokemons = await fetchPokemonData(slice);
                } else {
                    const offset = (page-1)*pageSize;
                    const data   = await fetch(`https://pokeapi.co/api/v2/pokemon?limit=${pageSize}&offset=${offset}`).then(r=>r.json());
                    pokemons     = await fetchPokemonData(data.results);
                }
                ocultarSpinner();
                pokemons.forEach(renderCard);
                crearPaginacion();
                divPrincipal.classList.replace("fade-out", "fade-in");
            } catch(err) { console.error(err); ocultarSpinner(); }
        }, 300);
    }

    // ── Paginación ───────────────────────────────────────
    function crearPaginacion() {
        paginationContainer.innerHTML = "";
        const container = document.createElement("div");
        container.style.gridColumn = "1 / -1";
        container.className = "d-flex align-items-center gap-2 flex-wrap justify-content-center";
        const total = filteredPokemonList ? Math.ceil(filteredPokemonList.length/pageSize) : totalPages;

        const prev = document.createElement("button");
        prev.className = "btn btn-secondary"; prev.innerHTML = "&lt;";
        prev.disabled = currentPage===1; prev.onclick = ()=>cargarPagina(currentPage-1);
        container.appendChild(prev);

        const pages = [1];
        for(let i=Math.max(2,currentPage); i<=Math.min(total-1,currentPage); i++) pages.push(i);
        if(Math.min(total-1,currentPage)<total-1) pages.push("...");
        if(total>1) pages.push(total);

        pages.forEach(p => {
            if(p==="...") {
                const b=document.createElement("button"); b.className="btn btn-outline-primary"; b.textContent="...";
                b.onclick=mostrarModalSalto; container.appendChild(b);
            } else {
                const b=document.createElement("button");
                b.className=p===currentPage?"btn btn-primary":"btn btn-outline-primary";
                b.textContent=p; b.disabled=p===currentPage; b.onclick=()=>cargarPagina(p);
                container.appendChild(b);
            }
        });

        const next=document.createElement("button");
        next.className="btn btn-secondary"; next.innerHTML="&gt;";
        next.disabled=currentPage===total; next.onclick=()=>cargarPagina(currentPage+1);
        container.appendChild(next);
        paginationContainer.appendChild(container);
    }

    // ── Modal salto ──────────────────────────────────────
    const modalSalto  = new bootstrap.Modal(document.getElementById("modalSaltoPagina"));
    const inputPagina = document.getElementById("inputPagina");
    const errorPagina = document.getElementById("errorPagina");
    const btnIr       = document.getElementById("btnIrPagina");

    function mostrarModalSalto() { inputPagina.value=""; errorPagina.classList.add("d-none"); modalSalto.show(); }
    inputPagina.addEventListener("keypress", e=>{ if(e.key==="Enter") btnIr.click(); });
    btnIr.addEventListener("click", ()=>{
        const page=parseInt(inputPagina.value);
        const total=filteredPokemonList?Math.ceil(filteredPokemonList.length/pageSize):totalPages;
        if(!isNaN(page)&&page>=1&&page<=total){ modalSalto.hide(); cargarPagina(page); }
        else errorPagina.classList.remove("d-none");
    });

    // ── Búsqueda ─────────────────────────────────────────
    searchForm.addEventListener("submit", e=>{
        e.preventDefault();
        document.getElementById("mensajeSinResultados")?.remove();
        mostrarSpinner(); cardsContainer.innerHTML=""; paginationContainer.innerHTML="";
        const q=searchInput.value.trim().toLowerCase();
        if(!q){ filteredPokemonList=null; cargarPagina(1); return; }
        if(!isNaN(q)){
            const id=parseInt(q);
            if(id<1||id>totalPokemon){ mostrarSinResultados(); return; }
            fetch(`https://pokeapi.co/api/v2/pokemon/${id}`).then(r=>{ if(!r.ok) throw new Error(); return r.json(); })
                .then(pk=>{ filteredPokemonList=[{name:pk.name,url:`https://pokeapi.co/api/v2/pokemon/${id}/`}]; cargarPagina(1); })
                .catch(()=>mostrarSinResultados());
        } else {
            fetch("https://pokeapi.co/api/v2/pokemon?limit=1025").then(r=>r.json())
                .then(data=>{ filteredPokemonList=data.results.filter(p=>p.name.toLowerCase().startsWith(q)); filteredPokemonList.length?cargarPagina(1):mostrarSinResultados(); })
                .catch(()=>mostrarSinResultados());
        }
    });
    searchInput.addEventListener("input", resetFilter);

    // ── Filtros modal ─────────────────────────────────────
    const modalFiltros=new bootstrap.Modal(document.getElementById("modalFiltros"));
    filtrar.addEventListener("click",e=>{ e.preventDefault(); modalFiltros.show(); filteredPokemonList=null; searchInput.value=""; });
    document.getElementById("aplicarFiltros").addEventListener("click",()=>{
        const tipos=[...document.querySelectorAll(".filtro-tipo:checked")].map(el=>el.value);
        const gens=[...document.querySelectorAll(".filtro-gen:checked")].map(el=>generaciones[el.value]);
        const orden=document.getElementById("ordenSelect").value;
        aplicarFiltros(tipos,gens,orden); modalFiltros.hide();
    });

    async function aplicarFiltros(tipos,gens,orden){
        mostrarSpinner(); cardsContainer.innerHTML=""; paginationContainer.innerHTML="";
        let lista=filteredPokemonList||await fetch("https://pokeapi.co/api/v2/pokemon?limit=1025").then(r=>r.json()).then(d=>d.results);
        if(gens.length) lista=lista.filter(p=>{ const id=extraerID(p.url); return gens.some(g=>id>=g.min&&id<=g.max); });
        if(tipos.length){
            const filtrados=[];
            for(const p of lista){ const d=await fetch(p.url).then(r=>r.json()); if(tipos.every(t=>d.types.map(x=>x.type.name).includes(tiposEN[t]))) filtrados.push(p); }
            lista=filtrados;
        }
        if(orden==="az") lista.sort((a,b)=>a.name.localeCompare(b.name));
        if(orden==="za") lista.sort((a,b)=>b.name.localeCompare(a.name));
        if(orden==="id-asc") lista.sort((a,b)=>extraerID(a.url)-extraerID(b.url));
        if(orden==="id-desc") lista.sort((a,b)=>extraerID(b.url)-extraerID(a.url));
        filteredPokemonList=lista;
        lista.length?cargarPagina(1):mostrarSinResultados();
    }

    document.getElementById("resetFiltros").addEventListener("click", resetFilter);
    function resetFilter(){
        document.querySelectorAll(".filtro-tipo").forEach(el=>el.checked=false);
        document.querySelectorAll(".filtro-gen").forEach(el=>el.checked=false);
        document.getElementById("ordenSelect").value="";
    }

    function extraerID(url){ const p=url.split("/"); return parseInt(p[p.length-2]); }

    function mostrarSinResultados(){
        ocultarSpinner(); filteredPokemonList=[];
        cardsContainer.innerHTML=""; paginationContainer.innerHTML="";
        const m=document.createElement("p"); m.id="mensajeSinResultados";
        m.className="text-center fs-4 mt-4"; m.textContent="No se han encontrado resultados";
        divPrincipal.appendChild(m);
    }
    function mostrarSpinner(){ document.getElementById("spinnerCarga").classList.remove("d-none"); document.getElementById("spinnerCarga").classList.add("d-flex"); }
    function ocultarSpinner(){ document.getElementById("spinnerCarga").classList.remove("d-flex"); document.getElementById("spinnerCarga").classList.add("d-none"); }

    // ── Primera carga ────────────────────────────────────
    cargarPagina(1);
}

})();
