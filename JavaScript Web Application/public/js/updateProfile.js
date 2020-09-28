import axios from "axios";
import { showAlert } from "./alert";

export const updateProfile = async (data) => {
  try {
    // console.log(data);
    const res = await axios({
      method: "PATCH",
      url: "/api/v1/users/updateUser",
      data,
    });
    if (res.data.status === "success") {
      window.setTimeout(() => {
        location.assign("/profile");
      }, 3000);
      showAlert("success", "Profile updated successfully!");
    }
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert("danger", "Invalid profile.");
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};
