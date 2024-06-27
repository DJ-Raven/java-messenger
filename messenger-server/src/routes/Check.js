const express = require("express");
const VERSION = process.env.VERSION || 1;

const router = express.Router();

router.get("/", (req, res) => {
  const version = req.headers.version;
  if (version === undefined || version < VERSION) {
    return res.status(426).send("Please update you app to new version");
  } else {
    return res.status(200).send("Server is working");
  }
});

module.exports = router;
