const jwt = require("jsonwebtoken");

exports.verify = (req, res, next) => {
  const token = req.cookies.accessToken;
  if (!token) return res.status(401).send("Unauthorized");
  jwt.verify(token, process.env.JWT_SECRET_KEY, (err, user) => {
    if (err) return res.status(401).send("Invalid token");
    req.user = { id: user.id, uuid: user.uuid, user: user.user };
    next();
  });
};

exports.getUser = (headers) => {
  const token = headers.cookies;
  if (!token) return null;
  return jwt.verify(token, process.env.JWT_SECRET_KEY, (err, user) => {
    if (err) return null;
    return { id: user.id, uuid: user.uuid, user: user.user };
  });
};
