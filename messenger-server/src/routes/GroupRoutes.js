const express = require("express");
const groupController = require("../controllers/GroupController");
const jwtVerify = require("../models/JwtVerify");
const multer = require("multer");
const { v4 } = require("uuid");
const path = require("path");
const router = express.Router();

const storage = multer.diskStorage({
  destination: "upload/",
  filename: function (req, file, cb) {
    const dir = "profile";
    const uuid = v4();
    const fileExtension = path.extname(file.originalname);
    const fileName = dir + "/" + uuid + fileExtension;
    cb(null, fileName);
  },
});
const upload = multer({ storage: storage });

router.get("/check", jwtVerify.verify, groupController.check);
router.post(
  "/create",
  jwtVerify.verify,
  upload.single("file"),
  groupController.create
);
router.post("/join", jwtVerify.verify, groupController.joinGroup);
router.get("/member", jwtVerify.verify, groupController.getGroupMember);

module.exports = router;
