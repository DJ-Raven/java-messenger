const db = require("../config/db");
const utils = require("./utils/Utils");
const { v4 } = require("uuid");

const message = function () {};

message.create = (data) => {
  return new Promise((resolve, reject) => {
    let sql = "";
    if (data.type === "group") {
      sql =
        "insert into message (message_uuid, from_user, to_group, message_type, reference_id, message, create_date) values (?,?,?,?,?,?,?)";
    } else if (data.type === "user") {
      sql =
        "insert into message (message_uuid, from_user, to_user, message_type, reference_id, message, create_date) values (?,?,?,?,?,?,?)";
    }
    const uuid = v4();
    const reference_id = data.reference_id ? data.reference_id : null;
    const date = new Date();
    db.execute(
      sql,
      [
        uuid,
        data.from_user,
        data.target,
        data.message_type,
        reference_id,
        data.message,
        date,
      ],
      (err, result) => {
        if (err) return reject(err);
        const res = {
          id: result.insertId,
          uuid: uuid,
          from_user: data.from_user,
          from_group: data.type === "group" ? data.target : undefined,
          from_name: data.from_name,
          target: data.target,
          type: data.type,
          message_type: data.message_type,
          message: data.message,
          create_date: date,
        };
        if (reference_id) {
          return message
            .getFile(reference_id, data.message_type)
            .then((result) => {
              res.file = result;
              resolve(res);
            });
        } else {
          resolve(res);
        }
      }
    );
  });
};

message.findUserMessage = (data) => {
  return new Promise((resolve, reject) => {
    const limit = 20;
    const sql =
      "select message_id, message_uuid, from_user, first_name, last_name, message_type, message, message.create_date, message.update_date, file_id, files.`name`, files.original_name, files.size, files.type, files.info from message join `user` on (from_user=user_id) left join files on (reference_id=file_id) where message.`status`='1' and ((from_user=? and to_user=?) or (from_user=? and to_user=?)) order by message_id desc limit ?,?";
    const from = data.user.id;
    const to = data.target;
    const start = (data.page - 1) * limit;
    db.execute(
      sql,
      [from, to, to, from, start.toString(), limit.toString()],
      (err, result) => {
        if (err) return reject(err);
        resolve(toList(result, "user"));
      }
    );
  });
};

message.findGroupMessage = (data) => {
  return new Promise((resolve, reject) => {
    const limit = 20;
    const sql =
      "select message_id, message_uuid, from_user, `user`.first_name, `user`.last_name, message_type, message, message.create_date, message.update_date, file_id, files.`name`, files.original_name, files.size, files.type, files.info from message join `user` on (from_user=user_id) left join files on (reference_id=file_id) where message.`status`='1' and to_group=? order by message_id desc limit ?,?";
    const to = data.target;
    const start = (data.page - 1) * limit;
    db.execute(sql, [to, start.toString(), limit.toString()], (err, result) => {
      if (err) return reject(err);
      resolve(toList(result, "group"));
    });
  });
};

message.upload = (data) => {
  return new Promise(async (resolve, reject) => {
    const sql =
      "insert into files (name, original_name, size, type, info) values (?,?,?,?,?)";
    if (data.type === "p") {
      const imageinfo = await utils.getImageInfo(`upload/${data.name}`);
      data.info = imageinfo;
    } else if (data.type === "f") {
      const fileinfo = JSON.stringify({});
      data.info = fileinfo;
    }
    db.execute(
      sql,
      [data.name, data.original_name, data.size, data.type, data.info],
      (err, result) => {
        if (err) return reject(err);
        resolve({
          id: result.insertId,
          name: data.name,
          original_name: data.original_name,
          size: data.size,
          type: data.type,
          info: JSON.parse(data.info),
        });
      }
    );
  });
};

message.getFile = (id, type) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select file_id, `name`, original_name, size, type, info from files where file_id=? limit 1";
    db.execute(sql, [id], (err, result) => {
      if (err) return reject(err);
      const data = result[0];
      data.message_type = type;
      const res = getFile(data);
      resolve(res);
    });
  });
};

message.checkPermission = (user, file) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select files.info from message join files on reference_id=files.file_id where files.`name`=? and message.`status`='1' and (to_group is not null or from_user=? or to_user=?) limit 1";
    db.execute(sql, [file, user.id, user.id], (err, result) => {
      if (err) return reject(err);
      if (result.length === 1) {
        resolve(true);
      } else {
        resolve(false);
      }
    });
  });
};

message.checkIsProfile = (user, file) => {
  return new Promise((resolve, reject) => {
    if (file.startsWith("profile/")) {
      resolve(true);
    } else {
      resolve(false);
      // can check profile permission here
    }
  });
};

function toList(result, type) {
  const list = result.map((e) => {
    const file = getFile(e);
    const file_type =
      file && utils.isMusicFile(file.name) ? "v" : e.message_type;

    return {
      id: e.message_id,
      uuid: e.message_uuid,
      from_user: e.from_user,
      from_name: {
        first_name: e.first_name,
        last_name: e.last_name,
      },
      message_type: file_type,
      type: type,
      message: e.message,
      create_date: e.create_date,
      ...e?.update_date,
      file: file,
    };
  });
  return list;
}

function getFile(data) {
  if (data.message_type == "t") {
    return undefined;
  }
  return {
    id: data.file_id,
    name: data.name,
    original_name: data.original_name,
    size: data.size,
    type: data.type,
    info: JSON.parse(data.info),
  };
}

module.exports = message;
