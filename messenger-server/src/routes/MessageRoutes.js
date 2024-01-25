const express = require("express");
const messageController = require("../controllers/MessageController");
const jwtVerify = require("../models/JwtVerify");
const multer = require("multer");
const { v4 } = require("uuid");
const path = require("path");
const router = express.Router();

const storage = multer.diskStorage({
  destination: "upload/",
  filename: function (req, file, cb) {
    const type = req.query.type;
    const dir = type === "v" ? "voice" : type === "p" ? "photo" : "file";
    const uuid = v4();
    const fileExtension = path.extname(file.originalname);
    const fileName = dir + "/" + uuid + fileExtension;
    cb(null, fileName);
  },
});
const upload = multer({ storage: storage });

router.get("/message", jwtVerify.verify, messageController.findUserMessage);
router.post(
  "/upload",
  jwtVerify.verify,
  upload.single("file"),
  messageController.upload
);
module.exports = router;
router.get("/download", jwtVerify.verify, messageController.download);
