const divPrincipal = document.getElementById("divPrincipal");
const navbar = document.querySelector(".navPrincipal");
const nextPageBtn = document.getElementById("nextPageBtn");
const prevPageBtn = document.getElementById("prevPageBtn");

const totalPokemon = 1025;
const pageSize = 9;

let pagesHistory = [];
let currentPageIndex = -1;

// Scroll navbar
divPrincipal.addEventListener("scroll", () => {
    if (divPrincipal.scrollTop > 40) {
        navbar.classList.add("shrink");
    } else {
        navbar.classList.remove("shrink");
    }
});

// Generar IDs aleatorios sin repetir dentro de la misma página
function getRandomPokemonIds() {
    const ids = new Set();
    while (ids.size < pageSize) {
        const randomId = Math.floor(Math.random() * totalPokemon) + 1;
        ids.add(randomId);
    }
    return [...ids];
}

// Función para cargar una página de Pokémon (recibe array de IDs)
function cargarPagina(ids) {
    divPrincipal.innerHTML = "";

    const promises = ids.map(id =>
        fetch(`https://pokeapi.co/api/v2/pokemon/${id}`)
            .then(res => {
                if (!res.ok) throw new Error(`Error fetching Pokémon ${id}`);
                return res.json();
            })
    );

    Promise.all(promises)
        .then(pokemons => {
            pokemons.forEach(p => {
                const card = document.createElement("div");
                card.className = "cardPokemon";

                card.innerHTML = `
                    <div class="card" style="width: 18rem;">
                        <img src="${p.sprites.front_default}" class="card-img-top" alt="${p.name}">
                        <div class="card-body">
                            <h5 class="card-title">${p.name}</h5>
                            <p class="card-text">${p.types.map(t => t.type.name).join(' / ')}</p>
                            <div class="d-flex justify-content-between">
                                <a href="#" class="btn btn-primary">Añadir</a>
                                <a href="#" class="btn btn-secondary">Ver información</a>
                            </div>
                        </div>
                    </div>
                `;
                divPrincipal.appendChild(card);
            });
        })
        .catch(err => console.error("Error cargando Pokémon:", err));
}

// Primera página al iniciar
function cargarPrimeraPagina() {
    const firstIds = getRandomPokemonIds();
    pagesHistory.push(firstIds);
    currentPageIndex = 0;
    cargarPagina(firstIds);
}

cargarPrimeraPagina();

// Siguiente página
nextPageBtn.addEventListener("click", () => {
    const ids = getRandomPokemonIds();
    // Si ya volvimos atrás, eliminar páginas "adelante"
    pagesHistory = pagesHistory.slice(0, currentPageIndex + 1);
    pagesHistory.push(ids);
    currentPageIndex++;
    cargarPagina(ids);
});

// Página anterior
prevPageBtn.addEventListener("click", () => {
    if (currentPageIndex > 0) {
        currentPageIndex--;
        cargarPagina(pagesHistory[currentPageIndex]);
    }
});
