// Generar cards Pokémon
const representaPokemons = () => {
    const divPrincipal = document.getElementById("divPrincipal");
    divPrincipal.innerHTML = "";

    for (let i = 0; i < 12; i++) { // más de 6 para probar scroll
        const card = document.createElement("div");
        card.className = "cardPokemon";

        card.innerHTML = `
            <div class="card" style="width: 18rem;">
                <img src="./sources/foto_prueba_card.jpg" class="card-img-top" alt="...">
                <div class="card-body">
                    <h5 class="card-title">Prueba Card ${i+1}</h5>
                    <p class="card-text">Descripción de la card Pokémon.</p>
                    <div class="d-flex justify-content-between">
                        <a href="#" class="btn btn-primary">Añadir</a>
                        <a href="#" class="btn btn-secondary">Ver información</a>
                    </div>
                </div>
            </div>
        `;
        divPrincipal.appendChild(card);
    }
};

representaPokemons();

const divPrincipal = document.getElementById("divPrincipal");
const navbar = document.querySelector(".navPrincipal");

divPrincipal.addEventListener("scroll", () => {
    if (divPrincipal.scrollTop > 40) {
        navbar.classList.add("shrink");
    } else {
        navbar.classList.remove("shrink");
    }
});


let currentPage = 1;
const pageSize = 9;

const pokemonContainer = document.getElementById("pokemonContainer");
const nextPageBtn = document.getElementById("nextPageBtn");

function cargarPokemons(page) {
    fetch(`http://localhost:8080/api/pokemon/index/page?page=${page}&pageSize=${pageSize}`)
        .then(res => res.json())
        .then(pokemons => {
            // Limpiar contenedor
            pokemonContainer.innerHTML = "";

            pokemons.forEach(p => {
                const card = document.createElement("div");
                card.classList.add("card", "p-2", "shadow", "text-center");
                card.style.width = "150px";

                card.innerHTML = `
                    <img src="${p.image}" alt="${p.name}" class="card-img-top" style="width:100px;margin:auto;">
                    <h5>${p.name}</h5>
                    <p>${p.primaryType}${p.secondaryType ? ' / ' + p.secondaryType : ''}</p>
                `;

                pokemonContainer.appendChild(card);
            });
        })
        .catch(err => console.error(err));
}

// Cargar primera página al iniciar
cargarPokemons(currentPage);

// Botón siguiente página
nextPageBtn.addEventListener("click", () => {
    currentPage++;
    cargarPokemons(currentPage);
});
