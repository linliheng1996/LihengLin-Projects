import axios from "axios";
import { showAlert } from "./alert";

export const login = async (email, password) => {
  // console.log(email + " " + password);
  try {
    const res = await axios({
      method: "POST",
      url: "/api/v1/users/login",
      data: {
        email,
        password,
      },
    });
    if (res.data.status === "success") {
      window.setTimeout(() => {
        location.assign("/account");
      }, 1500);
      showAlert("success", "Logged in successfully!");
    }
    // console.log(res);
  } catch (err) {
    showAlert("danger", "Failed to log in.");
    console.log(err);
  }
};

export const logout = async () => {
  try {
    const res = await axios({
      method: "GET",
      url: "/api/v1/users/logout",
    });
    if ((res.data.status = "success")) location.assign("/");
  } catch (err) {
    showAlert("danger", "Error logging out.");
  }
};

export const signup = async (
  firstName,
  lastName,
  email,
  password,
  passwordConfirm
) => {
  try {
    const res = await axios({
      method: "POST",
      url: "api/v1/users/signup",
      data: {
        firstName,
        lastName,
        email,
        password,
        passwordConfirm,
      },
    });
    if (res.data.status === "success") {
      window.setTimeout(() => {
        location.assign("/account");
      }, 1500);
      showAlert("success", "Sign up successfully!");
    }
    // console.log(res);
  } catch (err) {
    showAlert("danger", "Failed to sign up.");
    console.log(err);
  }
};

export const forgotPassword = async (email) => {
  try {
    const res = await axios({
      method: "POST",
      url: "/api/v1/users/forgotPassword",
      data: {
        email,
      },
    });
    if (res.data.status === "success") {
      window.setTimeout(() => {
        location.assign("/");
      }, 1500);
      showAlert("success", "Email sent successfully!");
    }
    // console.log(res);
  } catch (err) {
    showAlert("danger", "Failed to send email.");
    console.log(err);
  }
};
