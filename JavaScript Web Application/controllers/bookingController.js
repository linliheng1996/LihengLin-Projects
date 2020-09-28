const Product = require("./../models/productModel");
const User = require("./../models/userModel");
const Booking = require("../models/bookingModel");
const catchAsync = require("./../utils/catchAsync");
const factory = require("./handlerFactory");
const AppError = require("./../utils/appError");
const stripe = require("stripe")(process.env.STRIPE_SECRET_KEY);

exports.getCheckoutSession = catchAsync(async (req, res, next) => {
  // get appointment
  const appointment = await Product.findById(req.params.id);
  // create checkout session
  const session = await stripe.checkout.sessions.create({
    payment_method_types: ["card"],
    // success_url: `${req.protocol}://${req.get(
    //   "host"
    // )}/my-appointments/?appointment=${req.params.id}&user=${
    //   req.user.id
    // }&price=${appointment.price}`,
    success_url: `${req.protocol}://${req.get("host")}/time-purchased`,
    cancel_url: `${req.protocol}://${req.get("host")}/market`,
    // success_url: "https://example.com/success",
    // cancel_url: "https://example.com/cancel",
    customer_email: req.user.email,
    //this is the id of appointment:
    client_reference_id: req.params.id,
    line_items: [
      {
        name: appointment.title,
        description: appointment.description,
        amount: appointment.price * 100,
        currency: "usd",
        quantity: 1,
      },
    ],
  });
  // create session as response
  res.status(200).json({
    status: "success",
    session,
  });
});

// exports.createBookingCheckout = catchAsync(async (req, res, next) => {
//   const { appointment, user, price } = req.query;
//   if (!appointment || !user || !price) return next();
//   await Booking.create({ product: appointment, user, price });
//   // console.log("url: " + req.originalUrl.split("?")[0]);
//   // res.redirect(req.originalUrl.split("?")[0]);
//   res.redirect("/my-appointments");
// });

const createBookingCheckout = async (session) => {
  console.log("debug here");
  const appointment = session.client_reference_id;
  const appointmentObj = await Product.findOne({ _id: appointment });
  console.log("debug here");
  console.log(appointmentObj);
  console.log(appointmentObj.user);
  const user = (await User.findOne({ email: session.customer_email })).id;
  const price = session.display_items[0].amount / 100;
  await Booking.create({
    product: appointment,
    provider: appointmentObj.user._id,
    user,
    price,
  });
};

exports.webhookCheckout = (req, res, next) => {
  const signature = req.headers["stripe-signature"];
  let event;
  try {
    event = stripe.webhooks.constructEvent(
      req.body,
      signature,
      process.env.STRIPE_WEBHOOK_SECRET
    );
  } catch (err) {
    return res.status(400).send(`Webhook error: ${err.message}`);
  }
  if (event.type === "checkout.session.completed") {
    createBookingCheckout(event.data.object);
  }
  res.status(200).json({ received: true });
};

exports.getAll = factory.getAll(Booking);

exports.getOne = factory.getOne(Booking);

exports.createOne = factory.createOne(Booking);

exports.updateOne = factory.updateOne(Booking);

exports.deleteOne = factory.deleteOne(Booking);
