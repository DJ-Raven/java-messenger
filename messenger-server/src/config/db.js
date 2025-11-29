const mysql2 = require("mysql2");

const connect = mysql2.createPool({
  host: process.env.DATABASE_HOST,
  port: process.env.APP_DATABASE_PORT,
  user: process.env.DATABASE_USER,
  database: process.env.DATABASE_NAME,
  password: process.env.DATABASE_PASSWORD,
});

module.exports = connect;
