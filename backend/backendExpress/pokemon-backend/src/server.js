require("dotenv").config(); // ← OBLIGATORIO

const app = require("./app");
const connectDB = require("./config/db");

const PORT = process.env.PORT || 3000;

connectDB();

app.listen(PORT, () => {
  console.log(`Servidor escuchando en puerto ${PORT}`);
});
