import axios from "axios";
import { showAlert } from "./alert";
const stripe = Stripe(
  "pk_test_51HKX9SIpxP2Hbiv0RRra1XUKNJqT02CI46ymz77dQubNUQtOwmZrARoPRCNI6vmPhZe9QnPjNyLuQuBpq0u98Sk400iJO0Rfpq"
);

export const book = async (id) => {
  try {
    // get session from server
    const session = await axios({
      method: "GET",
      url: `/api/v1/bookings/checkout-session/${id}`,
    });
    // create checkout form and charge credit card
    await stripe.redirectToCheckout({
      sessionId: session.data.session.id,
    });
  } catch (err) {
    console.log(err);
    showAlert("error", err);
  }
};
