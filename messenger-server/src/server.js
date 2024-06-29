require("dotenv").config();
const express = require("express");
const { createServer } = require("http");

const basicAuth = require("express-basic-auth");
const bodyParser = require("body-parser");
const cookieParser = require("cookie-parser");
const socketService = require("./socket/SocketService");
const PORT = process.env.PORT || 3000;
const app = express();
const httpServer = createServer(app);

app.use(cookieParser());
app.use(bodyParser.json());
app.use(
  basicAuth({
    users: { user: "raven-messenger-server" },
    unauthorizedResponse: (req) => {
      return req.auth ? "Invalid credentials" : "No credentials provided";
    },
  })
);
app.use("/api/auth", require("./routes/AuthRoutes"));
app.use("/api/user", require("./routes/UserRoutes"));
app.use("/api/message", require("./routes/MessageRoutes"));
app.use("/api/profile", require("./routes/ProfileRoutes"));
app.use("/api/group", require("./routes/GroupRoutes"));
app.use("/api/check", require("./routes/Check"));

socketService(httpServer);

httpServer.listen(PORT, () => {
  console.log(`Server running on port ${PORT}`);
});
