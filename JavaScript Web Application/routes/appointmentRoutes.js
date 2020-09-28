const express = require("express");
const router = express.Router({ mergeParams: true });
const appointmentController = require("./../controllers/appointmentController");
const authController = require("./../controllers/authController");

router.use(authController.protect);

router
  .route("/")
  .get(appointmentController.getAll)
  .post(
    appointmentController.setAppointmentUserId,
    appointmentController.createOne
  );

router
  .route("/:id")
  .get(appointmentController.getOne)
  .patch(appointmentController.updateOne)
  .delete(appointmentController.deleteOne);

module.exports = router;
