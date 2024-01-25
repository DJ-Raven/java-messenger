const mysql2 = require("mysql2");

let connect;

if (process.env.CLEARDB_DATABASE_URL) {
  connect = mysql2.createPool(process.env.CLEARDB_DATABASE_URL);
} else {
  connect = mysql2.createPool({
    host: process.env.DB_HOST,
    port: process.env.DB_PORT,
    user: process.env.DB_USER,
    database: process.env.DB_NAME,
    password: process.env.DB_PASSWORD,
  });
}

module.exports = connect;
