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
        "select u.user_id as id, u.user_uuid as uuid, concat(first_name,' ',last_name) as `name`, `profile`, 'user' as `type`, max(m.message_id) as last_message, u.create_date, max(m.create_date) as last_time from `user` as u left join message as m on ((u.user_id = m.from_user and m.to_user = ?) or (u.user_id = m.to_user and m.from_user = ?)) and m.`status` = '1' where u.user_id <> ? and u.`status` = '1' group by u.user_id union select g.group_id as id, g.group_uuid as uuid, g.`name` as `name`, `profile`, 'group' as `type`, max(m.message_id) as last_message, g.create_date, if(max(m.create_date) is null,if(mb.user_id=?,max(mb.join_date),null),max(m.create_date)) as last_time from `groups` as g left join member as mb on (g.group_id = mb.group_id) left join message as m on (g.group_id = m.to_group and mb.user_id = ?) and m.`status` = '1' where g.`status` = '1' group by g.group_id, mb.user_id order by last_time desc limit ?,?";
      sqlData = [
        data.user.id,
        data.user.id,
        data.user.id,
        data.user.id,
        data.user.id,
        start.toString(),
        limit.toString(),
      ];
    } else {
      sql =
        "select u.user_id as id, u.user_uuid as uuid, concat(first_name,' ',last_name) as `name`, `profile`, 'user' as `type`, max(m.message_id) as last_message, u.create_date, max(m.create_date) as last_time from `user` as u left join message as m on ((u.user_id = m.from_user and m.to_user = ?) or (u.user_id = m.to_user and m.from_user = ?)) and m.`status` = '1' where u.user_id <> ? and u.`status` = '1' and (concat(first_name,' ',last_name) like ? or phone_number like ?) group by u.user_id union select g.group_id as id, g.group_uuid as uuid, g.`name` as `name`, `profile`, 'group' as `type`, max(m.message_id) as last_message, g.create_date, if(max(m.create_date) is null,if(mb.user_id=?,max(mb.join_date),null),max(m.create_date)) as last_time from `groups` as g left join member as mb on (g.group_id = mb.group_id) left join message as m on (g.group_id = m.to_group and mb.user_id = ?) and m.`status` = '1' where g.`status` = '1' and (g.`name` like ?) group by g.group_id, mb.user_id order by last_time desc limit ?,?";
      sqlData = [
        data.user.id,
        data.user.id,
        data.user.id,
        `%${data.search}%`,
        `%${data.search}%`,
        data.user.id,
        data.user.id,
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

user.findUserById = (id) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select user_id, user_uuid, concat(first_name,' ',last_name) as `name`, `profile` from `user` where `status`='1' and user_id=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const r = result[0];
        const data = {
          id: r.user_id,
          uuid: r.user_uuid,
          name: r.name,
          type: "user",
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

user.findGroupById = (id) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select group_id, group_uuid, `name`, `profile` from `groups` where `status`='1' and group_id=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const r = result[0];
        const data = {
          id: r.group_id,
          uuid: r.group_uuid,
          name: r.name,
          type: "group",
          active: false,
          profile: JSON.parse(r.profile),
        };
        resolve(data);
      } else {
        resolve(null);
      }
    });
  });
};

user.getUserProfile = (id) => {
  return new Promise((resolve, reject) => {
    const sql = "select `profile` from `user` where user_id=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const data = result[0];
        resolve(JSON.parse(data.profile));
      } else {
        resolve(null);
      }
    });
  });
};

user.getUser = (id) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select user_id, user_uuid, `profile`, first_name, last_name, gender, bio, phone_number, create_date from `user` where user_uuid=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        const data = result[0];
        resolve({
          user_id: data.user_id,
          user_uuid: data.user_uuid,
          profile: JSON.parse(data.profile),
          first_name: data.first_name,
          last_name: data.last_name,
          gender: data.gender,
          bio: data.bio,
          phone_number: data.phone_number,
          create_date: data.create_date,
        });
      } else {
        resolve(null);
      }
    });
  });
};

function getLastMessage(user, id) {
  const sql =
    "select from_user, message, message_type from message where message_id=? limit 1";
  return db
    .promise()
    .query(sql, [id])
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
        id: e.id,
        uuid: e.uuid,
        name: e.name,
        type: e.type,
        active: e.type === "user" ? e.id in users : false,
        profile: JSON.parse(e.profile),
        last_message: e.last_message
          ? await getLastMessage(user, e.last_message)
          : undefined,
      };
    })
  );
}

module.exports = user;
