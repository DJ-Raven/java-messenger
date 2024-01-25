const ms = require("ms");

const auth = require("../models/Auth");

exports.login = async (req, res, next) => {
  try {
    const data = await auth.login(req.query);
    if (data !== null) {
      const cookieOptions = {
        maxAge: ms("15d"),
        httpOnly: true,
      };
      res
        .cookie("accessToken", data.token, cookieOptions)
        .status(200)
        .json(data);
    } else {
      res.status(501).send("Invalid username or password");
    }
  } catch (err) {
    res.status(500).send("Server Error");
  }
};

exports.register = async (req, res, next) => {
  try {
    const data = await auth.register(req.body);
    res.status(200).send(data);
  } catch (err) {
    res.status(500).send(err);
  }
};
