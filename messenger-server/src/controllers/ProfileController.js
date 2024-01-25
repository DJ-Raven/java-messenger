const profile = require("../models/Profile");

exports.getProfile = async (req, res, next) => {
  try {
    const data = await profile.getProfile(req.user);
    if (data) {
      res.status(200).json(data);
    } else {
      res.status(404).json("User profile not found");
    }
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.updateProfileUser = async (req, res, next) => {
  try {
    const userData = {
      first_name: req.body.first_name,
      last_name: req.body.last_name,
    };
    const data = await profile.updateProfileUser(req.user, userData);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.updateProfilePhone = async (req, res, next) => {
  try {
    const userData = {
      phone_number: req.query.phone_number,
    };
    const data = await profile.updateProfilePhone(req.user, userData);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.updateProfileGender = async (req, res, next) => {
  try {
    const userData = {
      gender: req.body.gender,
    };
    const data = await profile.updateProfileGender(req.user, userData);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.updateProfileBio = async (req, res, next) => {
  try {
    const userData = {
      bio: req.query.bio,
    };
    const data = await profile.updateProfileBio(req.user, userData);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};

exports.updateProfileImage = async (req, res, next) => {
  try {
    const file = {
      image: req.file.filename,
      size: req.file.size,
    };
    const data = await profile.updateProfileImage(req.user, file);
    res.status(200).json(data);
  } catch (err) {
    res.status(500).send(err);
  }
};
