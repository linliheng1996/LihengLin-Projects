import { login, logout, signup, forgotPassword } from "./account";
import { updatePassword, updateBasicInfo } from "./updateSettings";
import { updateProfile } from "./updateProfile";
import {
  createAppointment,
  deleteBooking,
  deleteAppointment,
  updateAppointment,
} from "./appointment";
import { createReview } from "./review";
import { book } from "./stripe";
import "@babel/polyfill";

const loginForm = document.querySelector(".form-login");
const signupForm = document.querySelector(".form-signup");
const forgotPasswordForm = document.querySelector(".form-forgot-password");
const logoutButton = document.querySelector("#logout");
const updateBasicInfoForm = document.querySelector(".form-basic-info");
const updateProfileForm = document.querySelector(".form-profile");
const updatePasswordForm = document.querySelector(".form-pw-change");
const createAppointmentForm = document.querySelector(
  ".form-create-appointment"
);
const updateAppointmentForm = document.querySelector(
  ".form-update-appointment"
);
const createReviewForm = document.querySelector(".form-create-review");
const bookBtns = document.querySelectorAll("#book-appointment");
const deleteBookingBtns = document.querySelectorAll("#deleteBooking");
const deleteAppointmentBtns = document.querySelectorAll("#deleteAppointment");
const marketFilterSortForm = document.querySelector(".form-filter");

if (loginForm)
  loginForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    login(email, password);
  });

if (signupForm)
  signupForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const firstName = document.getElementById("firstName").value;
    const lastName = document.getElementById("lastName").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const passwordConfirm = document.getElementById("passwordConfirm").value;
    // console.log(passwordConfirm);
    signup(firstName, lastName, email, password, passwordConfirm);
  });

if (logoutButton) logoutButton.addEventListener("click", logout);

if (forgotPasswordForm)
  forgotPasswordForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const email = document.getElementById("email").value;
    forgotPassword(email);
  });

if (updateBasicInfoForm)
  updateBasicInfoForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const form = new FormData();
    // const email = document.getElementById("email").value;
    // console.log("email " + email);
    form.append("email", document.getElementById("email").value);
    const photo = document.getElementById("photo").files[0];
    if (photo) form.append("photo", document.getElementById("photo").files[0]);
    updateBasicInfo(form);
  });

if (updatePasswordForm)
  updatePasswordForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    document.querySelector(".btn-change-password").textContent = "Updating...";
    const passwordCurrent = document.getElementById("passwordCurrent").value;
    const password = document.getElementById("newPassword").value;
    const passwordConfirm = document.getElementById("passwordConfirm").value;
    await updatePassword(passwordCurrent, password, passwordConfirm);
    document.getElementById("passwordCurrent").value = "";
    document.getElementById("newPassword").value = "";
    document.getElementById("passwordConfirm").value = "";
    document.querySelector(".btn-change-password").textContent =
      "Change Password";
  });

if (updateProfileForm)
  updateProfileForm.addEventListener("submit", (e) => {
    e.preventDefault();
    const form = new FormData();
    form.append("firstName", document.getElementById("firstName").value);
    form.append("lastName", document.getElementById("lastName").value);
    form.append("gender", document.getElementById("gender").value);
    form.append("age", document.getElementById("age").value);
    form.append("major", document.getElementById("major").value);
    form.append(
      "majorCategory",
      document.getElementById("majorCategory").value
    );
    form.append("yearOfStudy", document.getElementById("yearOfStudy").value);
    form.append("linkedIn", document.getElementById("linkedIn").value);
    form.append(
      "personalWebsite",
      document.getElementById("personalWebsite").value
    );
    form.append("description", document.getElementById("description").value);

    updateProfile(form);
  });

if (createAppointmentForm)
  createAppointmentForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const title = document.getElementById("title").value;
    const price = document.getElementById("price").value;
    const duration = document.getElementById("duration").value;
    const description = document.getElementById("description").value;

    createAppointment(title, price, duration, description);
  });

if (updateAppointmentForm)
  updateAppointmentForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const appId = document.getElementById("appId").value;
    const title = document.getElementById("title").value;
    const price = document.getElementById("price").value;
    const duration = document.getElementById("duration").value;
    const description = document.getElementById("description").value;

    updateAppointment(appId, title, price, duration, description);
  });

if (createReviewForm)
  createReviewForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const review = document.getElementById("review").value;
    const rating = document.getElementById("rating").value;
    const providerId = document.getElementById("providerId").value;

    createReview(review, rating, providerId);
  });

if (bookBtns)
  for (var i = bookBtns.length - 1; i >= 0; i--) {
    bookBtns[i].addEventListener("click", (e) => {
      e.target.textContent = "Processing...";
      const { appId } = e.target.dataset;
      // console.log("appId" + appId);
      book(appId);
    });
  }

if (deleteBookingBtns)
  for (var i = deleteBookingBtns.length - 1; i >= 0; i--) {
    deleteBookingBtns[i].addEventListener("click", (e) => {
      const { appId } = e.target.dataset;
      // console.log("appId" + appId);
      deleteBooking(appId);
    });
  }

if (deleteAppointmentBtns)
  for (var i = deleteAppointmentBtns.length - 1; i >= 0; i--) {
    deleteAppointmentBtns[i].addEventListener("click", (e) => {
      const { appId } = e.target.dataset;
      // console.log("appId" + appId);
      deleteAppointment(appId);
    });
  }

if (marketFilterSortForm) {
  marketFilterSortForm.addEventListener("submit", async (e) => {
    e.preventDefault();
    const sortBy = document.getElementById("sortBy").value;
    var ratingFilter = $("input[name=ratingFilter]:checked").val();
    const majorCategory = document.getElementById("majorCategory").value;
    const yearOfStudy = document.getElementById("yearOfStudy").value;

    if (ratingFilter == null) ratingFilter = 1;
    var queryUrl = "";
    queryUrl += `sort=${sortBy}`;
    queryUrl += `&ratingsAverage[gte]=${ratingFilter}`;
    if (majorCategory !== "-") queryUrl += `&majorCategory=${majorCategory}`;
    if (yearOfStudy !== "-") queryUrl += `&yearOfStudy=${yearOfStudy}`;
    var cur = window.location.href.split("?")[0];
    window.location.replace(cur + "?" + queryUrl);
  });
}
