import axios from "axios";
import { showAlert } from "./alert";

export const createReview = async (review, rating, providerId) => {
  try {
    const res = await axios({
      method: "POST",
      url: `/api/v1/users/${providerId}/reviews`,
      data: {
        review,
        rating,
      },
    });
    if (res.data.status === "success") {
      window.setTimeout(() => {
        location.assign("/time-purchased");
      }, 1500);
      showAlert("success", "Review submitted successfully!");
    }
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert(
      "danger",
      "Invalid review. Please do not submit review more than once."
    );
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};
