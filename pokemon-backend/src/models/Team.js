const mongoose = require("mongoose");

const MoveSchema = new mongoose.Schema({
  name: String,
  type: String,
  category: String,
  power: Number,
  accuracy: Number
});

const StatsSchema = new mongoose.Schema(
  {
    hp: Number,
    atk: Number,
    def: Number,
    spa: Number,
    spd: Number,
    spe: Number
  },
  { _id: false }
);

const PokemonSchema = new mongoose.Schema({
  pokemonKey: String,
  level: Number,
  nature: String,
  ability: String,
  item: String,
  evs: StatsSchema,
  ivs: StatsSchema,
  stats: StatsSchema,
  role: String,
  moves: [MoveSchema]
});

const TeamSchema = new mongoose.Schema(
  {
    userId: { type: mongoose.Schema.Types.ObjectId, ref: "User" },
    name: String,
    format: String,
    status: { type: String, default: "ACTIVE" },
    pokemon: [PokemonSchema]
  },
  { timestamps: true }
);

module.exports = mongoose.model("Team", TeamSchema);
