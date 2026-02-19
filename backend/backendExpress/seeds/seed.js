// ARCHIVO QUE VAMOS A USAR PARA AÑADIR LOS DATOS DEMO

require("dotenv").config();
const mongoose = require("mongoose");

const User = require("../src/models/User");
const Team = require("../src/models/Team");

const seedDatabase = async () => {
  try {
    await mongoose.connect(process.env.MONGO_URI);
    console.log("Conectado a MongoDB");

    // Limpieza previa
    await User.deleteMany();
    await Team.deleteMany();

    console.log("Colecciones limpiadas");

    // Usuario demo
    const user = await User.create({
      username: "demoUser",
      email: "demo@pokemon.com"
    });

    console.log("Usuario demo creado");

    // Equipo demo
    const team = await Team.create({
      userId: user._id,
      name: "Equipo Demo OU",
      format: "OU",
      pokemon: [
        {
          pokemonKey: "garchomp",
          level: 100,
          nature: "Jolly",
          ability: "Rough Skin",
          item: "Choice Scarf",
          evs: {
            hp: 4,
            atk: 252,
            def: 0,
            spa: 0,
            spd: 0,
            spe: 252
          },
          ivs: {
            hp: 31,
            atk: 31,
            def: 31,
            spa: 31,
            spd: 31,
            spe: 31
          },
          stats: {
            hp: 358,
            atk: 359,
            def: 226,
            spa: 176,
            spd: 206,
            spe: 333
          },
          role: "Physical Sweeper",
          moves: [
            { name: "Earthquake", type: "Ground", category: "Physical", power: 100 },
            { name: "Dragon Claw", type: "Dragon", category: "Physical", power: 80 }
          ]
        }
      ]
    });

    console.log("Equipo demo creado:", team.name);

    await mongoose.connection.close();
    console.log("Seed completado correctamente");
    process.exit(0);
  } catch (error) {
    console.error("Error ejecutando seed", error);
    process.exit(1);
  }
};

seedDatabase();
