const user = require("../models/User");

exports.findAll = async (req, res, next) => {
  try {
    const data = await user.findAll({
      user: req.user,
      page: req.query.page,
      search: req.query?.search,
    });
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.findById = async (req, res, next) => {
  try {
    const type = req.query.type;
    const id = req.query.id;
    const data =
      type === "user"
        ? await user.findUserById(id)
        : await user.findGroupById(id);
    if (data) {
      res.status(200).json(data);
    } else {
      res.status(404).send("User not found");
    }
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.getUserProfile = async (req, res, next) => {
  try {
    const id = req.query.id;
    const data = await user.getUserProfile(id);
    if (data) {
      res.status(200).json(data);
    } else {
      res.status(404).send("User not found");
    }
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.getUser = async (req, res, next) => {
  try {
    const id = req.query.id;
    const data = await user.getUser(id);
    if (data) {
      res.status(200).json(data);
    } else {
      res.status(404).send("User not found");
    }
  } catch (err) {
    res.status(500).send(err);
  }
};
