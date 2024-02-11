const express = require("express");
const userController = require("../controllers/UserController");
const jwtVerify = require("../models/JwtVerify");
const router = express.Router();

router.get("/", jwtVerify.verify, userController.findAll);
router.get("/find", jwtVerify.verify, userController.findById);
router.get("/profile", jwtVerify.verify, userController.getUserProfile);
router.get("/get", jwtVerify.verify, userController.getUser);

module.exports = router;
