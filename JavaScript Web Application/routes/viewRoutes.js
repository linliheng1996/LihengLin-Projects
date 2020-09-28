const express = require("express");
const viewsController = require("./../controllers/viewsController");
const authController = require("./../controllers/authController");
const bookingController = require("./../controllers/bookingController");

const router = express.Router();

router.get("/home", authController.isLoggedIn, viewsController.getIndexPage);
router.get("/", authController.isLoggedIn, viewsController.getIndexPage);
router.get("/how-it-works", viewsController.getHowItWorksPage);
router.get("/what-is-it", viewsController.getWhatIsItPage);

router.get("/login", authController.isLoggedIn, viewsController.getLoginPage);
router.get("/signup", authController.isLoggedIn, viewsController.getSignupPage);
router.get("/forgot-password", viewsController.getForgotPasswordPage);
router.get(
  "/account",
  authController.protect,
  viewsController.getAccountSettingsPage
);
router.get(
  "/account-settings",
  authController.protect,
  viewsController.getAccountSettingsPage
);

router.get("/profile", authController.protect, viewsController.getProfilePage);
router.get("/market", authController.protect, viewsController.getMarketPage);
router.get("/market-preview", viewsController.getMarketPage);

router.get(
  "/users/:id",
  authController.protect,
  viewsController.getProviderProfile
);

router.get(
  "/sell-time",
  authController.protect,
  viewsController.getCreateAppointmentPage
);

router.get(
  "/appointments/:id",
  authController.isLoggedIn,
  viewsController.getAppointment
);

router.get(
  "/time-purchased",
  // bookingController.createBookingCheckout,
  authController.protect,
  viewsController.getMyPurchasedTime
);

router.get(
  "/time-sold",
  // bookingController.createBookingCheckout,
  authController.protect,
  viewsController.getMySoldTime
);

router.get(
  "/selling",
  authController.protect,
  viewsController.getMySellingPage
);

router.get(
  "/create-review/:id",
  authController.protect,
  viewsController.getCreateReviewPage
);

module.exports = router;
