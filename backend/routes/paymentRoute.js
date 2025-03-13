//const express = require("express");
//const { db } = require("../config/firebaseConfig");  // Import Firebase configuration
//const stripe = require("stripe")("sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH");  // Replace with your actual Stripe secret key
//
//const router = express.Router();
//
//// Route to process payment
//router.post("/processPayment", async (req, res) => {
//  const { startBusStopName, endBusStopName, route, numberOfPersons, paymentMethodId } = req.body;
//
//  try {
//    // Fetch bus stops for the specific route from Firebase
//    const busStopsRef = db.ref(`busStops/${route}`);
//    const snapshot = await busStopsRef.once("value");
//    const busStops = snapshot.val();
//
//    // Find the start and end bus stops based on names
//    let startBusStop = null;
//    let endBusStop = null;
//    for (const busStop in busStops) {
//      if (busStops[busStop].name.toLowerCase() === startBusStopName.toLowerCase()) {
//        startBusStop = busStop;
//      }
//      if (busStops[busStop].name.toLowerCase() === endBusStopName.toLowerCase()) {
//        endBusStop = busStop;
//      }
//    }
//
//    // Check if the start and end bus stops are valid
//    if (!startBusStop || !endBusStop) {
//      return res.status(400).send("Invalid bus stop names");
//    }
//
//    // Calculate the fare based on the bus stop numbers
//    const startFare = busStops[startBusStop].fare;
//    const endFare = busStops[endBusStop].fare;
//    const fareToPayPerPerson = Math.abs(endFare - startFare);
//
//    // Calculate the total fare by multiplying the fare by the number of persons
//    const totalFare = fareToPayPerPerson * numberOfPersons;
//
//    // Create a payment intent with Stripe
//    const paymentIntent = await stripe.paymentIntents.create({
//      amount: totalFare * 100,
//      currency: "lkr",
//      payment_method: paymentMethodId,
//      confirmation_method: "manual",
//      confirm: true,
//    });
//
//    // Check for confirmation
//    if (paymentIntent.status === "succeeded") {
//      // Return the formatted response
//      res.status(200).send(`Start Point - ${startBusStopName}\nEnd Point - ${endBusStopName}\nNo. of Persons - ${numberOfPersons}\nAmount to Pay - Rs. ${totalFare.toFixed(2)}\nPayment Status - Success`);
//    } else {
//      res.status(500).send("Payment failed");
//    }
//
//  } catch (error) {
//    console.error(error);
//    res.status(500).send("Error processing payment");
//  }
//});
//
//module.exports = router;


const express = require("express");
const { db } = require("../config/firebaseConfig");  // Import Firebase configuration

const router = express.Router();

// Route to process payment
router.post("/processPayment", async (req, res) => {
  const { startBusStopName, endBusStopName, route, numberOfPersons } = req.body;

  try {
    // Fetch bus stops for the specific route from Firebase
    const busStopsRef = db.ref(`busStops/${route}`);
    const snapshot = await busStopsRef.once("value");
    const busStops = snapshot.val();

    // Find the start and end bus stops based on names
    let startBusStop = null;
    let endBusStop = null;
    for (const busStop in busStops) {
      if (busStops[busStop].name.toLowerCase() === startBusStopName.toLowerCase()) {
        startBusStop = busStop;
      }
      if (busStops[busStop].name.toLowerCase() === endBusStopName.toLowerCase()) {
        endBusStop = busStop;
      }
    }

    // Check if the start and end bus stops are valid
    if (!startBusStop || !endBusStop) {
      return res.status(400).send("Invalid bus stop names");
    }

    // Calculate the fare based on the bus stop numbers
    const startFare = busStops[startBusStop].fare;
    const endFare = busStops[endBusStop].fare;
    const fareToPayPerPerson = Math.abs(endFare - startFare);  // Absolute value of fare difference

    // Calculate the total fare by multiplying the fare by the number of persons
    const totalFare = fareToPayPerPerson * numberOfPersons;

    // Return the formatted response
    res.status(200).send(`Start Point - ${startBusStopName}\nEnd Point - ${endBusStopName}\nNo. of Persons - ${numberOfPersons}\nAmount to Pay - Rs. ${totalFare.toFixed(2)}`);
  } catch (error) {
    res.status(500).send("Error retrieving bus stop data");
  }
});

module.exports = router;
