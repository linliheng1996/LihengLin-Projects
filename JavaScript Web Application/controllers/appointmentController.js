const Product = require("./../models/productModel");
const User = require("./../models/userModel");
const catchAsync = require("./../utils/catchAsync");
const AppError = require("./../utils/appError");
const APIFeatures = require("./../utils/apiFeatures");
const factory = require("./handlerFactory");

exports.setAppointmentUserId = (req, res, next) => {
  // Allow nested routes
  if (!req.body.user) req.body.user = req.user;
  next();
};

exports.getAll = factory.getAll(Product);

exports.getOne = factory.getOne(Product);

exports.createOne = factory.createOne(Product);

exports.updateOne = factory.updateOne(Product);

exports.deleteOne = factory.deleteOne(Product);

exports.getAllByUser = catchAsync(async (req, res, next) => {
  let filter = {};
  if (req.params.userId) filter = { provider: req.params.userId };

  const features = new APIFeatures(Product.find(filter), req.query)
    .filter()
    .sort()
    .limitFields()
    .paginate();
  const doc = await features.query;

  res.status(200).json({
    status: "success",
    results: doc.length,
    data: {
      data: doc,
    },
  });
});
