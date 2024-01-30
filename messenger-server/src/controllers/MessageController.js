const message = require("../models/Message");

exports.findUserMessage = async (req, res, next) => {
  try {
    const data = await message.findUserMessage({
      user: req.user,
      target: req.query.user,
      page: req.query.page,
    });
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err.mesaage);
  }
};

exports.upload = async (req, res, next) => {
  try {
    const file = {
      name: req.file.filename,
      original_name: req.file.originalname,
      size: req.file.size,
      type: req.query.type,
      info: req.query.info,
    };
    const data = await message.upload(file);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err.mesaage);
  }
};

exports.download = async (req, res, next) => {
  try {
    const filename = req.query.filename;
    const permission = await message.checkPermission(req.user, filename);
    if (permission) {
      const url = "upload/" + filename;
      res.download(url);
    } else {
      res.status(404).send("File not found");
    }
  } catch (err) {
    res.status(500).send(err.mesaage);
  }
};
