const Review = require("./../models/reviewModel");
const factory = require("./handlerFactory");
// const catchAsync = require('./../utils/catchAsync');

exports.setProviderUserIds = (req, res, next) => {
  // Allow nested routes
  // providerId is in userRoutes
  if (!req.body.provider) req.body.provider = req.params.providerId;
  if (!req.body.user) req.body.user = req.user.id;
  next();
};

exports.getAllReviews = factory.getAll(Review);
exports.getReview = factory.getOne(Review);
exports.createReview = factory.createOne(Review);
exports.updateReview = factory.updateOne(Review);
exports.deleteReview = factory.deleteOne(Review);
