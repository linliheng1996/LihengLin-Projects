import axios from "axios";
import { showAlert } from "./alert";

export const updateBasicInfo = async (data) => {
  try {
    const res = await axios({
      method: "PATCH",
      url: "/api/v1/users/updateUser",
      data,
    });
    if (res.data.status === "success") {
      window.setTimeout(() => {
        location.assign("/account");
      }, 3000);
      showAlert("success", "Basic info updated successfully!");
    }
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert("danger", "Invalid Basic info.");
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};

export const updatePassword = async (
  passwordCurrent,
  password,
  passwordConfirm
) => {
  // console.log(passwordCurrent + " " + password + " " + passwordConfirm);
  try {
    const res = await axios({
      method: "PATCH",
      url: "/api/v1/users/updatePassword",
      data: {
        passwordCurrent,
        password,
        passwordConfirm,
      },
    });
    if (res.data.status === "success")
      showAlert("success", "Password updated successfully!");
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert("danger", "Invalid password.");
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};
