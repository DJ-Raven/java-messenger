const db = require("../config/db");
const { v4 } = require("uuid");
const bcryptjs = require("bcryptjs");
const jwt = require("jsonwebtoken");

const auth = function () {};

auth.login = (data) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select user_id, user_uuid, first_name, last_name, user_name, `password` from `user` where user_name=? and `status`='1' limit 1";
    db.execute(sql, [data.user], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const user = result[0];
        const isPasswordCorrect = bcryptjs.compareSync(
          data.password,
          user.password
        );
        if (isPasswordCorrect) {
          const uuid = user.user_uuid;
          const name = user.first_name + " " + user.last_name;
          const jwt_key = process.env.JWT_SECRET_KEY;
          const token = jwt.sign(
            { id: user.user_id, uuid: uuid, user: name },
            jwt_key,
            {
              expiresIn: "15d",
            }
          );
          resolve({ token: token, data: { user: name } });
        } else {
          resolve(null);
        }
      } else {
        resolve(null);
      }
    });
  });
};

auth.register = (data) => {
  return new Promise((resolve, reject) => {
    validate(data)
      .then(() => {
        const sql =
          "insert into `user` (user_uuid, first_name, last_name, gender, user_name, `password`) values (?,?,?,?,?,?)";
        const salt = bcryptjs.genSaltSync(10);
        const hash = bcryptjs.hashSync(data.password, salt);
        const uuid = v4();
        db.execute(
          sql,
          [
            uuid,
            data.first_name,
            data.last_name,
            data.gender,
            data.user_name,
            hash,
          ],
          (err, result) => {
            if (err) return reject(err);
            resolve(data.user_name);
          }
        );
      })
      .catch((e) => {
        reject(e);
      });
  });
};

function validate(data) {
  return new Promise((resolve, reject) => {
    const sql =
      "select user_id from `user` where user_name=? and `status`='1' limit 1";
    db.execute(sql, [data.user_name], (err, result) => {
      if (err) return reject(err);
      if (result.length === 0) {
        resolve("Ok");
      } else {
        reject("Username or email already exists. Please use a different one.");
      }
    });
  });
}

module.exports = auth;
