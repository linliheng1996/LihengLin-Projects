const mongoose = require("mongoose");
const User = require("./userModel");
const reviewSchema = new mongoose.Schema({
  review: {
    type: String,
    required: [true, "Review can not be empty!"],
  },
  rating: {
    type: Number,
    min: 1,
    max: 5,
  },
  createdAt: {
    type: Date,
    default: Date.now,
  },
  provider: {
    type: mongoose.Schema.ObjectId,
    ref: "User",
    required: [true, "Review must belong to a provider."],
  },
  user: {
    type: mongoose.Schema.ObjectId,
    ref: "User",
    required: [true, "Review must belong to a user"],
  },
});

// reviewSchema.index({ provider: 1, user: 1 }, { unique: true });

reviewSchema.pre(/^find/, function (next) {
  this.populate({
    path: "user",
    select: "firstName lastName photo",
  });
  next();
});

reviewSchema.statics.calcAverageRatings = async function (providerId) {
  // console.log(providerId);
  const stats = await this.aggregate([
    {
      $match: { provider: providerId },
    },
    {
      $group: {
        _id: "$provider",
        numRating: { $sum: 1 },
        avgRating: { $avg: "$rating" },
      },
    },
  ]);
  // console.log(stats);
  if (stats.length > 0) {
    await User.findByIdAndUpdate(providerId, {
      ratingsQuantity: stats[0].numRating,
      ratingsAverage: stats[0].avgRating,
    });
  } else {
    await User.findByIdAndUpdate(providerId, {
      ratingsQuantity: 0,
      ratingsAverage: 5.0,
    });
  }
};

reviewSchema.post("save", function () {
  this.constructor.calcAverageRatings(this.provider);
});

// get review using the query before find, so use pre
reviewSchema.pre(/^findOneAnd/, async function (next) {
  // passing current review to the post middleware
  this.cur = await this.findOne();
  next();
});

// calculate after updating the review, so use post
reviewSchema.post(/^findOneAnd/, async function () {
  await this.cur.constructor.calcAverageRatings(this.cur.provider);
});

const Review = mongoose.model("Review", reviewSchema);

module.exports = Review;
