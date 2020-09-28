const express = require("express");
const router = express.Router();
const authController = require("./../controllers/authController");
const bookingController = require("./../controllers/bookingController");

router.use(authController.protect);

router.get("/checkout-session/:id", bookingController.getCheckoutSession);

router
  .route("/")
  .get(bookingController.getAll)
  .post(bookingController.createOne);

router
  .route("/:id")
  .get(bookingController.getOne)
  .patch(bookingController.updateOne)
  .delete(bookingController.deleteOne);

module.exports = router;
