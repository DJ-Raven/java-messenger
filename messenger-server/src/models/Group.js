const db = require("../config/db");
const { v4 } = require("uuid");
const utils = require("./utils/Utils");
const { json } = require("body-parser");

const group = function () {};

group.check = (user, id) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id, group_uuid, `name`, `profile`, description, create_date, join_date from `groups` join member using (group_id) where `groups`.`status`='1' and group_uuid=? and user_id=? limit 1";
    db.execute(sql, [id, user.id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const d = result[0];
        resolve({
          group_id: d.group_id,
          group_uuid: d.group_uuid,
          name: d.name,
          profile: JSON.parse(d.profile),
          description: d.description,
          create_date: d.create_date,
          join_date: d.join_date,
        });
      } else {
        resolve(null);
      }
    });
  });
};

group.create = (user, data) => {
  return new Promise(async (resolve, reject) => {
    const sql =
      "insert into `groups` (group_uuid, `name`, profile, description, create_by, create_date) values (?,?,?,?,?,?)";
    let file;
    if (data.file) {
      file = data.file;
      const imageinfo = await utils.getImageInfo(`upload/${file.image}`);
      file = Object.assign(file, JSON.parse(imageinfo));
    }
    const uuid = v4();
    const date = new Date();
    db.execute(
      sql,
      [
        uuid,
        data.name,
        file ? JSON.stringify(file) : null,
        data.description,
        user.id,
        date,
      ],
      (err, result) => {
        if (err) return reject(err);
        const sqlJoin =
          "insert into member (user_id, group_id, join_date) values (?,?,?)";
        db.execute(sqlJoin, [user.id, result.insertId, date], (err, result) => {
          if (err) return reject(err);
          resolve({
            id: result.insertId,
            uuid: uuid,
            create_by: user.id,
            create_date: date,
          });
        });
      }
    );
  });
};

group.joinGroup = (user, data) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id from `groups` join member using (group_id) where `groups`.`status`='1' and group_id=? and user_id=? limit 1";
    db.execute(sql, [data.group, user.id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 0) {
        const sqlJoin =
          "insert into member (user_id, group_id, join_date) values (?,?,?)";
        const date = new Date();
        db.execute(sqlJoin, [user.id, data.group, date], (err, result) => {
          if (err) return reject(err);
          getGroup(user, data.group)
            .then((data) => {
              resolve(data);
            })
            .catch((err) => {
              reject(err);
            });
        });
      } else {
        getGroup(user, data.group)
          .then((data) => {
            resolve(data);
          })
          .catch((err) => {
            reject(err);
          });
      }
    });
  });
};

group.getMemeberId = (id) => {
  return new Promise((resolve, reject) => {
    const sql = "select user_id from member where group_id=?";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      resolve(result.map((e) => e.user_id));
    });
  });
};

function getGroup(user, id) {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id, group_uuid, `name`, `profile`, description, create_date, join_date from `groups` join member using (group_id) where `groups`.`status`='1' and group_id=? and user_id=? limit 1";
    db.execute(sql, [id, user.id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const d = result[0];
        resolve({
          group_id: d.group_id,
          group_uuid: d.group_uuid,
          name: d.name,
          profile: JSON.parse(d.profile),
          description: d.description,
          create_date: d.create_date,
          join_date: d.join_date,
        });
      } else {
        resolve(null);
      }
    });
  });
}

module.exports = group;
