const mongoose = require("mongoose");

const productSchema = new mongoose.Schema(
  {
    title: {
      type: String,
      required: [true, "A product must have a title"],
      trim: true,
      maxlength: [
        40,
        "A product title must have less or equal then 40 characters",
      ],
    },
    user: {
      type: mongoose.Schema.ObjectId,
      ref: "User",
      required: [true, "Product must belong to a user"],
    },
    price: {
      type: Number,
      required: [true, "A product must have a price"],
    },
    duration: {
      type: Number,
      required: [true, "A product must have a duration"],
    },
    description: {
      type: String,
      trim: true,
    },
    createdAt: {
      type: Date,
      default: Date.now(),
      select: false,
    },
    startDates: [Date],
  },
  {
    toJSON: { virtuals: true },
    toObject: { virtuals: true },
  }
);

productSchema.pre(/^find/, function (next) {
  this.populate({
    path: "user",
    select: "_id firstName lastName photo",
  });
  next();
});

const Product = mongoose.model("Product", productSchema);

module.exports = Product;
