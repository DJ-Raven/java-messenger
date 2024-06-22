const db = require("../config/db");
const { v4 } = require("uuid");
const utils = require("./utils/Utils");

const group = function () {};

group.check = (user, id) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id, group_uuid, `name`, `profile`, description, create_by, create_date, (select count(member_id) from member where member.group_id=`groups`.group_id) as total_member from `groups` where `groups`.`status`='1' and group_uuid=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const d = result[0];
        const group_id = d.group_id;
        const sql_check =
          "select join_date from member where user_id=? and group_id=? limit 1";
        db.execute(sql_check, [user.id, group_id], (err, result_check) => {
          if (err) return reject(err);
          const join_date =
            result_check.length === 1 ? result_check[0].join_date : null;
          resolve({
            group_id: group_id,
            group_uuid: d.group_uuid,
            name: d.name,
            total_member: d.total_member,
            profile: JSON.parse(d.profile),
            description: d.description,
            create_by:d.create_by,
            create_date: d.create_date,
            join_date: join_date,
          });
        });
      } else {
        resolve(null);
      }
    });
  });
};

group.create = (user, data) => {
  return new Promise((resolve, reject) => {
    validate(data)
      .then(async () => {
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
            db.execute(
              sqlJoin,
              [user.id, result.insertId, date],
              (err, res) => {
                if (err) return reject(err);
                resolve({
                  id: result.insertId,
                  uuid: uuid,
                  create_by: user.id,
                  create_date: date,
                });
              }
            );
          }
        );
      })
      .catch((e) => {
        reject(e);
      });
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

group.getGroupMember = (data) => {
  return new Promise((resolve, reject) => {
    const limit = 30;
    const start = (data.page - 1) * limit;
    const sql =
      "select user_id, user_uuid, first_name, last_name, `profile` from member join `user` using (user_id) where group_id=? order by join_date limit ?,?";
    db.execute(
      sql,
      [data.group, start.toString(), limit.toString()],
      (err, result) => {
        if (err) return reject(err);
        resolve(toListMember(result));
      }
    );
  });
};

function getGroup(user, id) {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id, group_uuid, `name`, `profile`, description, create_by, create_date, join_date, (select count(member_id) from member where member.group_id=`groups`.group_id) as total_member from `groups` join member using (group_id) where `groups`.`status`='1' and group_id=? and user_id=? limit 1";
    db.execute(sql, [id, user.id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const d = result[0];
        resolve({
          group_id: d.group_id,
          group_uuid: d.group_uuid,
          name: d.name,
          total_member: d.total_member,
          profile: JSON.parse(d.profile),
          description: d.description,
          create_by:d.create_by,
          create_date: d.create_date,
          join_date: d.join_date,
        });
      } else {
        resolve(null);
      }
    });
  });
}

function validate(data) {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id from `groups` where `name`=? and `status`='1' limit 1";
    db.execute(sql, [data.name], (err, result) => {
      if (err) return reject(err);
      if (result.length === 0) {
        resolve("Ok");
      } else {
        reject("Group name already exists. Please use a different one.");
      }
    });
  });
}

function toListMember(data) {
  return data.map((e) => {
    return {
      user_id: e.user_id,
      user_uuid: e.user_uuid,
      first_name: e.first_name,
      last_name:e.last_name,
      profile: JSON.parse(e.profile),
    };
  });
}

module.exports = group;
