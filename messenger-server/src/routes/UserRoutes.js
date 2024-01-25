const express = require("express");
const userController = require("../controllers/UserController");
const jwtVerify = require("../models/JwtVerify");
const router = express.Router();

router.get("/", jwtVerify.verify, userController.findAll);
router.get("/:id", jwtVerify.verify, userController.findById);

module.exports = router;
