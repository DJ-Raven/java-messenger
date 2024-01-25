const db = require("../config/db");
const users = require("../socket/UserData");

const user = function () {};

user.findAll = (data) => {
  return new Promise((resolve, reject) => {
    const limit = 30;
    let sql;
    let sqlData;
    const start = (data.page - 1) * limit;
    if (data.search === "") {
      sql =
        "select u.user_id, u.user_uuid, `profile`, first_name, last_name, gender, max(m.create_date) as last_time from `user` as u left join message as m on ((u.user_id=m.from_user and m.to_user=?) or (u.user_id=m.to_user and m.from_user=?)) and m.`status`='1' where u.user_id <> ? and u.`status`='1' group by u.user_id order by last_time desc limit ?,?";
      sqlData = [
        data.user.id,
        data.user.id,
        data.user.id,
        start.toString(),
        limit.toString(),
      ];
    } else {
      sql =
        "select u.user_id, u.user_uuid, `profile`, first_name, last_name, gender, max(m.create_date) as last_time from `user` as u left join message as m on ((u.user_id=m.from_user and m.to_user=?) or (u.user_id=m.to_user and m.from_user=?)) and m.`status`='1' where u.user_id <> ? and u.`status`='1' and (concat(first_name,' ',last_name) like ? or phone_number like ?) group by u.user_id order by last_time desc limit ?,?";
      sqlData = [
        data.user.id,
        data.user.id,
        data.user.id,
        `%${data.search}%`,
        `%${data.search}%`,
        start.toString(),
        limit.toString(),
      ];
    }
    db.execute(sql, sqlData, (err, result) => {
      if (err) return reject(err);
      resolve(toList(data.user.id, result));
    });
  });
};

user.findById = (id) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select user_id, user_uuid, `profile`, first_name, last_name, gender from `user` where `status`='1' and user_id=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const r = result[0];
        const data = {
          user_id: r.user_id,
          user_uuid: r.user_uuid,
          first_name: r.first_name,
          last_name: r.last_name,
          gender: r.gender,
          active: r.user_id in users,
          profile: JSON.parse(r.profile),
        };
        resolve(data);
      } else {
        resolve(null);
      }
    });
  });
};

function getLastMessage(user, target) {
  const sql =
    "select from_user, message, message_type from message where message.`status`='1' and ((to_user=? and from_user=?) or (from_user=? and to_user=?)) order by message_id desc limit 1";
  return db
    .promise()
    .query(sql, [user, target, user, target])
    .then((result) => {
      const data = result[0][0];
      if (data) {
        return {
          you: data.from_user === user,
          from_user: data.from_user,
          message: data.message,
          type: data.message_type,
        };
      }
    });
}

async function toList(user, result) {
  return Promise.all(
    result.map(async (e) => {
      return {
        user_id: e.user_id,
        user_uuid: e.user_uuid,
        first_name: e.first_name,
        last_name: e.last_name,
        gender: e.gender,
        active: e.user_id in users,
        profile: JSON.parse(e.profile),
        last_message: await getLastMessage(user, e.user_id),
      };
    })
  );
}

module.exports = user;
