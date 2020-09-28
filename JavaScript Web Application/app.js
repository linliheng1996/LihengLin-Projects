const express = require("express");
const morgan = require("morgan");
const rateLimit = require("express-rate-limit");
// const helmet = require("helmet");
const mongoSanitize = require("express-mongo-sanitize");
const xss = require("xss-clean");
const hpp = require("hpp");
const compression = require("compression");
const cors = require("cors");
const path = require("path");
const cookieParser = require("cookie-parser");
const userRouter = require("./routes/userRoutes");
const viewRouter = require("./routes/viewRoutes");
const bookingRouter = require("./routes/bookingRoutes");
const bookingController = require("./controllers/bookingController");
const appointmentRouter = require("./routes/appointmentRoutes");
const reviewRouter = require("./routes/reviewRoutes");
const app = express();

app.enable("trust proxy");
app.set("view engine", "pug");
app.set("views", path.join(__dirname, "views"));

// security http headers
// app.use(helmet());

// log
// if (process.env.NODE_ENV === "development") {
//   app.use(morgan("dev"));
// }

// cors
app.use(cors());
app.options("*", cors());
// rate limit
const limiter = rateLimit({
  max: 100,
  windowMs: 60 * 60 * 1000,
  message: "Too many request from this IP",
});

app.use("/api", limiter);

// data sanitization against NoSQL
app.use(mongoSanitize());

// data sanitization against XSS
app.use(xss());

// prevent parameter pollution
app.use(hpp());

app.post(
  "/webhook-checkout",
  express.raw({ type: "application/json" }),
  bookingController.webhookCheckout
);

// body parser, read date from body to req.body
app.use(express.json({ limit: "10kb" }));
app.use(cookieParser());
app.use(express.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, "public")));
app.use(compression());

app.use((req, res, next) => {
  req.requestTime = new Date().toISOString();
  // console.log("jwt cookie: " + req.cookies.jwt);
  // console.log(req.headers);
  next();
});

app.use("/", viewRouter);
app.use("/api/v1/users", userRouter);
app.use("/api/v1/appointments", appointmentRouter);
app.use("/api/v1/bookings", bookingRouter);
app.use("/api/v1/reviews", reviewRouter);

module.exports = app;
