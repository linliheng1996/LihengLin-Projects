import axios from "axios";
import { showAlert } from "./alert";

export const createAppointment = async (
  title,
  price,
  duration,
  description
) => {
  try {
    // console.log(title, price, description);
    const res = await axios({
      method: "POST",
      url: "/api/v1/appointments",
      data: {
        title,
        price,
        duration,
        description,
      },
    });
    if (res.data.status === "success")
      showAlert("success", "Appointment created successfully!");
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert("danger", "Invalid info.");
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};

export const deleteBooking = async (bookingId) => {
  try {
    const res = await axios({
      method: "DELETE",
      url: `/api/v1/bookings/${bookingId}`,
    });
    // if (res.data.status === "success") {
    window.setTimeout(() => {
      location.assign("/time-purchased");
    }, 800);
    showAlert("success", "Booking deleted successfully!");
    // }
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert("danger", "Failed to delete booking.");
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};

export const deleteAppointment = async (appId) => {
  try {
    const res = await axios({
      method: "DELETE",
      url: `/api/v1/appointments/${appId}`,
    });
    // if (res.data.status === "success") {
    window.setTimeout(() => {
      location.assign("/selling");
    }, 800);
    showAlert("success", "Appointment deleted successfully!");
    // }
  } catch (err) {
    // showAlert("danger", err.response.data.message);
    showAlert("danger", "Failed to delete appointment.");
    console.log("error");
    // console.log("error: " + err.response.data.message);
  }
};

export const updateAppointment = async (
  appId,
  title,
  price,
  duration,
  description
) => {
  try {
    console.log("app id:" + appId);
    const res = await axios({
      method: "PATCH",
      url: `/api/v1/appointments/${appId}`,
      data: {
        title,
        price,
        duration,
        description,
      },
    });
    console.log(res);
    // if (res.data.status === "success") {
    window.setTimeout(() => {
      location.assign("/selling");
    }, 1500);
    showAlert("success", "Appointment updated successfully!");
    // }
  } catch (err) {
    showAlert("danger", "Failed to update appointment.");
    console.log("error");
  }
};
