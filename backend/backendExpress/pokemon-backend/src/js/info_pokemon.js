const tiposEN = {
    "Normal": "normal",
    "Lucha": "fighting",
    "Volador": "flying",
    "Veneno": "poison",
    "Tierra": "ground",
    "Roca": "rock",
    "Bicho": "bug",
    "Fantasma": "ghost",
    "Acero": "steel",
    "Fuego": "fire",
    "Agua": "water",
    "Planta": "grass",
    "Eléctrico": "electric",
    "Psíquico": "psychic",
    "Hielo": "ice",
    "Dragón": "dragon",
    "Siniestro": "dark",
    "Hada": "fairy"
};

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

document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const id = params.get("id");

    if (!id) {
        document.getElementById("detalleContainer").innerHTML = "<p>No se ha especificado ningún Pokémon.</p>";
        return;
    }

    cargarDetallePokemon(id);
});

async function cargarDetallePokemon(id) {
    const container = document.getElementById("detalleContainer");
    container.innerHTML = `
        <div class="d-flex justify-content-center mt-5">
            <div class="spinner-grow text-warning" role="status">
                <span class="visually-hidden">Loading...</span>
            </div>
        </div>
    `;

    try {
        const res = await fetch(`https://pokeapi.co/api/v2/pokemon/${id}`);
        if (!res.ok) throw new Error("No encontrado");
        const pokemon = await res.json();

        const speciesRes = await fetch(pokemon.species.url);
        const speciesData = await speciesRes.json();
        const nombreES = speciesData.names.find(n => n.language.name === "es")?.name || pokemon.name;

        // Generar HTML de tipos con icono
        const tiposHTML = pokemon.types.map(t => {
            // Buscar el nombre en español
            const nombreTipoES = Object.keys(tiposEN).find(key => tiposEN[key] === t.type.name) || t.type.name;
            const icono = typeIcons[nombreTipoES] || "default.png";
            return `
                <div class="d-flex align-items-center gap-1">
                    <div class="recorte"><img src="./assets/types/${icono}" alt="${nombreTipoES}" class="tipo-icon"></div>
                    <span>${nombreTipoES}</span>
                </div>
            `;
        }).join(`<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-grip-vertical" viewBox="0 0 16 16">
  <path d="M7 2a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 5a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0M7 8a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0m-3 3a1 1 0 1 1-2 0 1 1 0 0 1 2 0m3 0a1 1 0 1 1-2 0 1 1 0 0 1 2 0"/>
</svg>`);

        container.innerHTML = `
            <div class="card mx-auto">
                <div class="d-flex gap-4 flex-wrap">
                    <img src="${pokemon.sprites.front_default}" class="card-img-top" alt="${nombreES}" id="imgPokemon">
                    <div class="card-body d-grid align-items-start gap-2">
                        <h5 class="card-title h2">${nombreES}</h5>
                        <p><strong>ID Pokédex:</strong> ${pokemon.id}</p>
                        <div class="d-flex gap-2 flex-wrap align-items-center mb-3">
                        <p id="pTipos"><strong>Tipo/s:</strong></p>
                            ${tiposHTML}
                        </div>
                        <p><strong>Altura:</strong> ${pokemon.height / 10} m</p>
                        <p><strong>Peso:</strong> ${pokemon.weight / 10} kg</p>
                    </div>
                    <div class="card-body d-grid align-items-start gap-2">
                        <h5 class="card-title">Estadísticas base</h5>
                        <p><strong>PS:</strong> ${pokemon.stats.find(s => s.stat.name === "hp").base_stat}</p>
                        <p><strong>Velocidad:</strong> ${pokemon.stats.find(s => s.stat.name === "speed").base_stat}</p>
                        <p><strong>Ataque:</strong> ${pokemon.stats.find(s => s.stat.name === "attack").base_stat}</p>
                        <p><strong>Defensa:</strong> ${pokemon.stats.find(s => s.stat.name === "defense").base_stat}</p>
                        <p><strong>Ataque especial:</strong> ${pokemon.stats.find(s => s.stat.name === "special-attack").base_stat}</p>
                        <p><strong>Defensa especial:</strong> ${pokemon.stats.find(s => s.stat.name === "special-defense").base_stat}</p>
                    </div>
                </div>
            </div>
            <div class="d-flex mt-4" id="divBotones">
            <a href="./index.html"><button class="btn btn-secondary btn-lg">Volver</button></a>
            <a href=""><button class="btn btn-primary btn-lg">Añadir al equipo</button></a>
            </div>
        `;
    } catch (err) {
        container.innerHTML = `<p>Error al cargar el Pokémon: ${err.message}</p>`;
    }
}

