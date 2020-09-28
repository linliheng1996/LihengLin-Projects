const mongoose = require("mongoose");

const bookingSchema = new mongoose.Schema({
  product: {
    type: mongoose.Schema.ObjectId,
    ref: "Product",
    required: [true, "Must have a product"],
  },
  provider: {
    type: mongoose.Schema.ObjectId,
    ref: "User",
    required: [true, "Must have an provider"],
  },
  user: {
    type: mongoose.Schema.ObjectId,
    ref: "User",
    required: [true, "Must have an user"],
  },
  price: {
    type: Number,
    required: [true, "Must have a price"],
  },
  createdAt: {
    type: Date,
    default: Date.now(),
  },
  paid: {
    type: Boolean,
    default: true,
  },
});

bookingSchema.pre(/^find/, function (next) {
  this.populate("user").populate({
    path: "product",
    populate: { path: "user" },
  });
  next();
});

const Booking = mongoose.model("Booking", bookingSchema);

module.exports = Booking;
