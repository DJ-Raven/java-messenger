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
    console.log(err)
    res.status(500).send(err);
  }
};

exports.findById = async (req, res, next) => {
  try {
    const data = await user.findUserById(req.params.id);
    if (data) {
      res.status(200).json(data);
    } else {
      res.status(404).send("User not found");
    }
  } catch (err) {
    res.status(500).send(err);
  }
};
