const divPrincipal = document.getElementById("divPrincipal");
const navbar = document.querySelector(".navPrincipal");

const searchForm = document.getElementById("searchForm");
const searchInput = document.getElementById("searchInput");

const cardsContainer = document.getElementById("cardsContainer");
const paginationContainer = document.getElementById("paginationContainer");


const totalPokemon = 1025;
const pageSize = 9;
const totalPages = Math.ceil(totalPokemon / pageSize);

let currentPage = 1;
let filteredPokemonList = null; // Lista de Pokémon filtrados por búsqueda

const typeIcons = {
    "Normal": "1.png",
    "Lucha": "2.png",
    "Volador": "3.png",
    "Veneno": "4.png",
    "Tierra": "5.png",
    "Roca": "6.png",
    "Bicho": "7.png",
    "Fantasma": "8.png",
    "Acero": "9.png",
    "Fuego": "10.png",
    "Agua": "11.png",
    "Planta": "12.png",
    "Eléctrico": "13.png",
    "Psíquico": "14.png",
    "Hielo": "15.png",
    "Dragón": "16.png",
    "Siniestro": "17.png",
    "Hada": "18.png"
};

// Scroll navbar
divPrincipal.addEventListener("scroll", () => {
    if (divPrincipal.scrollTop > 40) {
        navbar.classList.add("shrink");
    } else {
        navbar.classList.remove("shrink");
    }
});

// 🔥 Cargar página por número
function cargarPagina(page) {
    divPrincipal.classList.remove('fade-in');
    divPrincipal.classList.add('fade-out');

    setTimeout(() => {
        cardsContainer.innerHTML = "";        // Solo borramos cards
        paginationContainer.innerHTML = "";   // Solo borramos paginación
        currentPage = page;

        let fetchList = null;
        if (filteredPokemonList) {
            const start = (page - 1) * pageSize;
            const end = start + pageSize;
            fetchList = filteredPokemonList.slice(start, end);
        }

        let dataPromise;
        if (fetchList) {
            // Pokémon filtrados
            const promises = fetchList.map(p =>
                fetch(p.url)
                    .then(res => res.json())
                    .then(pokemonData => fetch(pokemonData.species.url)
                        .then(res => res.json())
                        .then(speciesData => {
                            const nombreES = speciesData.names.find(n => n.language.name === "es");
                            const typePromises = pokemonData.types.map(t =>
                                fetch(t.type.url)
                                    .then(res => res.json())
                                    .then(typeData => {
                                        const typeES = typeData.names.find(n => n.language.name === "es");
                                        return typeES ? typeES.name : t.type.name;
                                    })
                            );
                            return Promise.all(typePromises).then(typesES => ({
                                ...pokemonData,
                                nombreTraducido: nombreES ? nombreES.name : pokemonData.name,
                                tiposTraducidos: typesES
                            }));
                        })
                    )
            );
            dataPromise = Promise.all(promises);
        } else {
            // Fetch normal
            const offset = (page - 1) * pageSize;
            dataPromise = fetch(`https://pokeapi.co/api/v2/pokemon?limit=${pageSize}&offset=${offset}`)
                .then(res => res.json())
                .then(data => {
                    const promises = data.results.map(p =>
                        fetch(p.url)
                            .then(res => res.json())
                            .then(pokemonData => fetch(pokemonData.species.url)
                                .then(res => res.json())
                                .then(speciesData => {
                                    const nombreES = speciesData.names.find(n => n.language.name === "es");
                                    const typePromises = pokemonData.types.map(t =>
                                        fetch(t.type.url)
                                            .then(res => res.json())
                                            .then(typeData => {
                                                const typeES = typeData.names.find(n => n.language.name === "es");
                                                return typeES ? typeES.name : t.type.name;
                                            })
                                    );
                                    return Promise.all(typePromises).then(typesES => ({
                                        ...pokemonData,
                                        nombreTraducido: nombreES ? nombreES.name : pokemonData.name,
                                        tiposTraducidos: typesES
                                    }));
                                })
                            )
                    );
                    return Promise.all(promises);
                });
        }

        dataPromise.then(pokemons => {
            pokemons.forEach(p => {
                const card = document.createElement("div");
                card.className = "cardPokemon";
                card.innerHTML = `
                    <div class="card" style="width: 18rem;">
                        <img src="${p.sprites.front_default}" class="card_img rounded mx-auto d-block" alt="${p.nombreTraducido}">
                        <div class="card-body">
                            <h5 class="card-title">${p.nombreTraducido}</h5>      
                            <div class="card-text d-flex gap-2 flex-wrap align-items-center mb-3">
                                ${p.tiposTraducidos.map(tipo => `
                                    <div class="recorte">
                                        <img src="./assets/types/${typeIcons[tipo]}" alt="${tipo}" class="tipo-icon">
                                    </div>
                                    <span class="txt-tipo">${tipo}</span>
                                `).join(`<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-grip-vertical" viewBox="0 0 16 16">
  <path d="M7 2a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 5a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/>
</svg>`)}
                            </div>
                            <div class="d-flex justify-content-between">
                                <a href="#" class="btn btn-primary">Añadir</a>
                                <a href="#" class="btn btn-secondary">Ver información</a>
                            </div>
                        </div>
                    </div>
                `;
                cardsContainer.appendChild(card); // ✅ Ahora sí va al contenedor de cards
            });

            crearPaginacion();
            divPrincipal.classList.remove('fade-out');
            divPrincipal.classList.add('fade-in');
        })
        .catch(err => console.error("Error:", err));
    }, 300);
}

// 🔥 Crear controles de paginación
function crearPaginacion() {
    paginationContainer.innerHTML = "";

    const container = document.createElement("div");
    container.style.gridColumn = "1 / -1";
    container.className = "d-flex flex-column align-items-center gap-2";

    const botones = document.createElement("div");
    botones.className = "d-flex align-items-center gap-2 flex-wrap justify-content-center";

    const total = filteredPokemonList
        ? Math.ceil(filteredPokemonList.length / pageSize)
        : totalPages;

    // BOTÓN <
    const prevBtn = document.createElement("button");
    prevBtn.className = "btn btn-secondary";
    prevBtn.innerHTML = "&lt;";
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => cargarPagina(currentPage - 1);
    botones.appendChild(prevBtn);

    // Botones inteligentes
    const pages = [];
    pages.push(1);

    const start = Math.max(2, currentPage);
    const end = Math.min(total - 1, currentPage);

    for (let i = start; i <= end; i++) pages.push(i);

    if (end < total - 1) pages.push("...");
    if (total > 1) pages.push(total);

    pages.forEach(p => {
        if (p === "...") {
            const dotsBtn = document.createElement("button");
            dotsBtn.className = "btn btn-outline-primary";
            dotsBtn.textContent = "...";
            dotsBtn.onclick = mostrarModalSalto;
            botones.appendChild(dotsBtn);
        } else {
            const btn = document.createElement("button");
            btn.className = p === currentPage ? "btn btn-primary" : "btn btn-outline-primary";
            btn.textContent = p;
            btn.disabled = p === currentPage;
            btn.onclick = () => cargarPagina(p);
            botones.appendChild(btn);
        }
    });

    // BOTÓN >
    const nextBtn = document.createElement("button");
    nextBtn.className = "btn btn-secondary";
    nextBtn.innerHTML = "&gt;";
    nextBtn.disabled = currentPage === total;
    nextBtn.onclick = () => cargarPagina(currentPage + 1);
    botones.appendChild(nextBtn);

    container.appendChild(botones);
    paginationContainer.appendChild(container);
}

// Modal de salto
const modalSalto = new bootstrap.Modal(document.getElementById('modalSaltoPagina'));
const inputPagina = document.getElementById('inputPagina');
const errorPagina = document.getElementById('errorPagina');
const btnIrPagina = document.getElementById('btnIrPagina');

function mostrarModalSalto() {
    inputPagina.value = '';
    errorPagina.classList.add('d-none');
    modalSalto.show();
}

    inputPagina.addEventListener('keypress', e => { if (e.key === 'Enter') btnIrPagina.click(); });


btnIrPagina.addEventListener('click', () => {
    const page = parseInt(inputPagina.value);
    const total = filteredPokemonList
        ? Math.ceil(filteredPokemonList.length / pageSize)
        : totalPages;

    if (!isNaN(page) && page >= 1 && page <= total) {
        modalSalto.hide();
        cargarPagina(page);
    } else {
        errorPagina.classList.remove('d-none');
    }
});

// Búsqueda por prefijo
searchForm.addEventListener("submit", e => {
    e.preventDefault();
    const query = searchInput.value.trim().toLowerCase();

    if (!query) {
        filteredPokemonList = null;
        cargarPagina(1);
        return;
    }

    // 🟢 Si es número → buscar por ID
    if (!isNaN(query)) {

        const id = parseInt(query);

        if (id < 1 || id > totalPokemon) {
            mostrarSinResultados();
            return;
        }

        fetch(`https://pokeapi.co/api/v2/pokemon/${id}`)
            .then(res => {
                if (!res.ok) throw new Error("No encontrado");
                return res.json();
            })
            .then(pokemon => {
                filteredPokemonList = [{
                    name: pokemon.name,
                    url: `https://pokeapi.co/api/v2/pokemon/${id}/`
                }];
                cargarPagina(1);
            })
            .catch(() => mostrarSinResultados());

    } else {

        // 🟢 Buscar por nombre (prefijo)
        fetch(`https://pokeapi.co/api/v2/pokemon?limit=1025`)
            .then(res => res.json())
            .then(data => {

                filteredPokemonList = data.results.filter(p =>
                    p.name.toLowerCase().startsWith(query)
                );

                if (filteredPokemonList.length === 0) {
                    mostrarSinResultados();
                } else {
                    cargarPagina(1);
                }
            })
            .catch(() => mostrarSinResultados());
    }
});

function mostrarSinResultados() {

    filteredPokemonList = [];

    cardsContainer.innerHTML = "";
    paginationContainer.innerHTML = "";

    const mensaje = document.createElement("div");
    mensaje.id = "mensajeSinResultados";
    mensaje.className = "text-center fs-4 mt-4";
    mensaje.textContent = "No se han encontrado resultados";

    divPrincipal.appendChild(mensaje); //arreglarlo
}


// 🔥 Primera carga
cargarPagina(1);
