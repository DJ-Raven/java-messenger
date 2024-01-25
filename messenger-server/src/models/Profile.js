const db = require("../config/db");
const utils = require("./utils/Utils");

const profile = function () {};

profile.getProfile = (user) => {
  return new Promise((resolve, reject) => {
    const sql =
      "select user_id, user_uuid, profile, first_name, last_name, gender, bio, phone_number, create_date from `user` where user_id=? and `user`.`status`='1' limit 1";
    db.execute(sql, [user.id], (err, result) => {
      if (result.length === 1) {
        return resolve(toJsonProfile(result[0]));
      }
      resolve(null);
    });
  });
};

profile.updateProfileImage = (user, data) => {
  return new Promise(async (resolve, reject) => {
    const sql = "update `user` set profile=? where user_id=? limit 1";
    const imageinfo = await utils.getImageInfo(`upload/${data.image}`);
    data = Object.assign(data, JSON.parse(imageinfo));
    db.execute(sql, [JSON.stringify(data), user.id], (err, result) => {
      if (err) return reject(err);
      resolve(data);
    });
  });
};

profile.updateProfileUser = (user, data) => {
  return new Promise((resolve, reject) => {
    const sql =
      "update `user` set first_name=?, last_name=? where user_id=? limit 1";
    db.execute(
      sql,
      [data.first_name, data.last_name, user.id],
      (err, result) => {
        if (err) return reject(err);
        resolve({ first_name: data.first_name, last_name: data.last_name });
      }
    );
  });
};

profile.updateProfilePhone = (user, data) => {
  return new Promise((resolve, reject) => {
    const sql = "update `user` set phone_number=? where user_id=? limit 1";
    db.execute(sql, [data.phone_number, user.id], (err, result) => {
      if (err) return reject(err);
      resolve({ phone_number: data.phone_number });
    });
  });
};

profile.updateProfileGender = (user, data) => {
  return new Promise((resolve, reject) => {
    const sql = "update `user` set gender=? where user_id=? limit 1";
    db.execute(sql, [data.gender, user.id], (err, result) => {
      if (err) return reject(err);
      resolve({ gender: data.gender });
    });
  });
};

profile.updateProfileBio = (user, data) => {
  return new Promise((resolve, reject) => {
    const sql = "update `user` set bio=? where user_id=? limit 1";
    db.execute(sql, [data.bio, user.id], (err, result) => {
      if (err) return reject(err);
      resolve({ bio: data.bio });
    });
  });
};

function toJsonProfile(result) {
  return {
    user_id: result.user_id,
    user_uuid: result.user_uuid,
    profile: JSON.parse(result.profile),
    first_name: result.first_name,
    last_name: result.last_name,
    gender: result.gender,
    bio: result.bio,
    phone_number: result.phone_number,
    create_date: result.create_date,
  };
}

module.exports = profile;
