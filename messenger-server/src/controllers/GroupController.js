const group = require("../models/Group");

exports.check = async (req, res, next) => {
  try {
    const id = req.query.group;
    const data = await group.check(req.user, id);
    if (data) {
      res.status(200).json(data);
    } else {
      res.status(404).send("Group not found");
    }
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.create = async (req, res, next) => {
  try {
    const file = req.file
      ? {
          image: req.file.filename,
          size: req.file.size,
        }
      : null;
    const reqData = {
      file: file,
      name: req.query.name,
      description: req.query.description,
    };
    const data = await group.create(req.user, reqData);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.joinGroup = async (req, res, next) => {
  try {
    const reqData = {
      group: req.query.group,
    };
    const data = await group.joinGroup(req.user, reqData);
    if (data) {
      res.status(200).json(data);
    }
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.getGroupMember = async (req, res, next) => {
  try {
    const data = await group.getGroupMember({
      user: req.user,
      group: req.query.group,
      page: req.query.page,
    });
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};
