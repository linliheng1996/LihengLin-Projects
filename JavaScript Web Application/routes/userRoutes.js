const express = require("express");
const userController = require("./../controllers/userController");
const authController = require("./../controllers/authController");
const appointmentRouter = require("./../routes/appointmentRoutes");
const reviewRouter = require("./../routes/reviewRoutes");

const router = express.Router();

router.use("/:userId/appointments", appointmentRouter);
router.use("/:providerId/reviews", reviewRouter);

router.post("/signup", authController.signup);
router.post("/login", authController.login);
router.get("/logout", authController.logout);

router.patch(
  "/updatePassword",
  authController.protect,
  authController.updatePassword
);

router.patch(
  "/updateUser",
  authController.protect,
  userController.uploadUserPhoto,
  userController.updateUser
);
router.delete("/deleteUser", authController.protect, userController.deleteUser);

router
  .route("/")
  .post(userController.createUser)
  .get(authController.protect, userController.getAllUsers);

router.route("/:id").get(userController.getUser);
// .delete(
//   authController.protect,
//   authController.restrictTo("admin"),
//   userController.deleteUser
// );

router.post("/forgotPassword", authController.forgotPassword);
router.patch("/resetPassword/:token", authController.resetPassword);

module.exports = router;
