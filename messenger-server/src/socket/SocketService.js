const { Server } = require("socket.io");
const jwtVerify = require("../models/JwtVerify");
const message = require("../models/Message");
const users = require("./UserData");

module.exports = function (server) {
  const io = new Server(server);
  io.on("connection", (socket) => {
    const user = jwtVerify.getUser(socket.handshake.headers);
    if (user !== null) {
      users[user.id] = socket.id;
      socket.broadcast.emit("user_status", user.id, true);
      socket.on("disconnect", () => {
        const user = jwtVerify.getUser(socket.handshake.headers);
        if (user !== null) {
          delete users[user.id];
          io.emit("user_status", user.id, false);
        } else {
          disconnectSocket(socket);
        }
      });
      socket.on("message", (ms, callback) => {
        const user = jwtVerify.getUser(socket.handshake.headers);
        if (user !== null) {
          const data = {
            from_user: user.id,
            to_user: ms.to_user,
            message_type: ms.message_type,
            message: ms.message,
            reference_id: ms?.reference_id,
          };
          message
            .create(data)
            .then((data) => {
              const toSocketId = users[ms.to_user];
              callback(data.create_date);
              io.to(toSocketId).emit("message", data);
            })
            .catch((e) => {
              console.log(e);
            });
        } else {
          disconnectSocket(socket);
        }
      });
    } else {
      disconnectSocket(socket);
    }
  });
};

function disconnectSocket(socket) {
  socket.disconnect();
}
