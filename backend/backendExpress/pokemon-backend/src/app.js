const express = require("express");
const cors = require("cors");

const app = express();

app.use(cors());
app.use(express.json());

// Rutas (vacías de momento)
app.use("/api/users", require("./routes/users.routes"));
app.use("/api/teams", require("./routes/teams.routes"));

// Endpoint de prueba
app.get("/api/health", (req, res) => {
  res.json({ status: "OK", message: "Backend operativo" });
});

module.exports = app;
