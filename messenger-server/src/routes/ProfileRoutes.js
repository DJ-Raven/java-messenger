const express = require("express");
const profileController = require("../controllers/ProfileController");
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

router.get("/", jwtVerify.verify, profileController.getProfile);
router.put(
  "/image",
  jwtVerify.verify,
  upload.single("file"),
  profileController.updateProfileImage
);
router.put("/user", jwtVerify.verify, profileController.updateProfileUser);
router.put("/phone", jwtVerify.verify, profileController.updateProfilePhone);
router.put("/gender", jwtVerify.verify, profileController.updateProfileGender);
router.put("/bio", jwtVerify.verify, profileController.updateProfileBio);
module.exports = router;
