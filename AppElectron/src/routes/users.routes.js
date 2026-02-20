const express = require("express");
const router = express.Router();

router.get("/", (req, res) => {
  res.json({ message: "Ruta usuarios operativa" });
});

module.exports = router;
