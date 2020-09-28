const User = require("./../models/userModel");
const Product = require("./../models/productModel");
const Booking = require("./../models/bookingModel");
const Review = require("./../models/reviewModel");
const factory = require("./handlerFactory");
const catchAsync = require("../utils/catchAsync");
const APIFeatures = require("./../utils/apiFeatures");

exports.getIndexPage = catchAsync(async (req, res) => {
  res.status(200).render("index", {
    title: "Time Market",
  });
});

exports.getHowItWorksPage = catchAsync(async (req, res) => {
  res.status(200).render("howItWorks", {
    title: "How It Works",
  });
});

exports.getWhatIsItPage = catchAsync(async (req, res) => {
  res.status(200).render("whatIsIt", {
    title: "What Is It",
  });
});

exports.getLoginPage = catchAsync(async (req, res) => {
  res.status(200).render("login", {
    title: "Log In",
  });
});

exports.getSignupPage = catchAsync(async (req, res) => {
  res.status(200).render("signup", {
    title: "Sign Up",
  });
});

exports.getForgotPasswordPage = catchAsync(async (req, res) => {
  res.status(200).render("forgotPassword", {
    title: "Forgot Password",
  });
});

exports.getAccountSettingsPage = catchAsync(async (req, res) => {
  res.status(200).render("accountSettings", {
    title: "Account Settings",
  });
});

exports.getProfilePage = catchAsync(async (req, res) => {
  res.status(200).render("profile", {
    title: "My Profile",
  });
});

exports.getMarketPage = catchAsync(async (req, res) => {
  const features = new APIFeatures(User.find(), req.query)
    .filter()
    .sort()
    .limitFields()
    .paginate();
  const providers = await features.query;
  res.status(200).render("market", {
    title: "Marketplace",
    providers,
  });
});

exports.getProviderProfile = catchAsync(async (req, res) => {
  const provider = await User.findById(req.params.id);
  const appointments = await Product.find({ user: req.params.id });
  const reviews = await Review.find({ provider: provider._id });
  // console.log("provider");
  // console.log(appointments);
  res.status(200).render("provider", {
    title: "Profile",
    provider,
    appointments,
    reviews,
  });
});

exports.getCreateAppointmentPage = catchAsync(async (req, res) => {
  res.status(200).render("createAppointment", {
    title: "Sell My Time",
  });
});

exports.getRequestAppointmentPage = catchAsync(async (req, res) => {
  res.status(200).render("requestAppointment", {
    title: "Request Appointments",
  });
});

exports.getAppointment = catchAsync(async (req, res) => {
  const appointment = await Product.findOne({ _id: req.params.id });
  if (!appointment) {
    return next(new AppError("There is no appointment with that id.", 404));
  }
  res.status(200).render("appointment", {
    title: "Appointment",
    appointment,
  });
});

exports.getMyPurchasedTime = catchAsync(async (req, res) => {
  const bookings = await Booking.find({ user: req.user.id });
  res.status(200).render("timePurchased", {
    title: "Time Purchased",
    bookings,
  });
});

exports.getMySoldTime = catchAsync(async (req, res) => {
  const apps = await Product.find({ user: req.user.id });
  const ids = apps.map((x) => x.id);
  const bookings = await Booking.find({
    product: {
      $in: ids,
    },
  });

  res.status(200).render("timeSold", {
    title: "Time Sold",
    bookings,
  });
});

exports.getMySellingPage = catchAsync(async (req, res) => {
  const appointments = await Product.find({ user: req.user.id });
  res.status(200).render("selling", {
    title: "Selling",
    appointments,
  });
});

exports.getCreateReviewPage = catchAsync(async (req, res) => {
  const provider = await User.findOne({ _id: req.params.id });
  res.status(200).render("createReview", {
    title: "Write Review",
    provider,
  });
});
