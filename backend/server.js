const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const { admin, db } = require("./config/firebaseConfig");
//const authRoutes = require("./routes/authRoutes");
//const userRoutes = require("./routes/userRoutes");
const paymentRoutes = require("./routes/paymentRoutes");
//const reservationRoutes = require("./routes/reservationRoutes");
const driverRoutes = require("./routes/driverRoutes");
//const dataRoutes = require("./routes/dataRoutes");
const stripe = require("stripe")("sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH");

const app = express();
const port = 3000;

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Routes
//app.use("/auth", authRoutes);
//app.use("/user", userRoutes);
app.use("/payment", paymentRoutes);
//app.use("/reservation", reservationRoutes);
app.use("/driver", driverRoutes);
//app.use("/data", dataRoutes);

app.get("/", (req, res) => {
  res.send("Backend is running");
});

// Calculate fare based on stop numbers
async function calculateFare(routeName, startStop, endStop) {
  const routeSnapshot = await db.ref(`routes/${routeName}`).once("value");
  const stops = routeSnapshot.val();

  if (!stops) throw new Error("Route not found");

  const startEntry = Object.values(stops).find(s => s.name === startStop);
  const endEntry = Object.values(stops).find(s => s.name === endStop);

  if (!startEntry || !endEntry) throw new Error("Invalid stop names");

  const distance = Math.abs(endEntry.stopNumber - startEntry.stopNumber);

  if (distance < 30) throw new Error("Minimum journey distance is 30 km");

  const fare = Math.abs(endEntry.fare - startEntry.fare);
  return { fare, distance };
}

// Payment request via NIC
app.post("/request-payment", async (req, res) => {
  const { passengerNIC, driverNIC, routeName, startStop, endStop } = req.body;
  try {
    const { fare, distance } = await calculateFare(routeName, startStop, endStop);

    const requestRef = db.ref("paymentRequests").push();
    await requestRef.set({
      passengerNIC,
      driverNIC,
      startStop,
      endStop,
      routeName,
      fare,
      distance,
      status: "pending",
    });

    res.send({ message: `Payment request for Rs.${fare} sent successfully.`, fare });
  } catch (err) {
    res.status(400).send({ error: err.message });
  }
});

// Confirm payment
app.post("/confirm-payment", async (req, res) => {
  const { requestId, approved } = req.body;
  try {
    const requestRef = db.ref(`paymentRequests/${requestId}`);
    const snapshot = await requestRef.once("value");
    const requestData = snapshot.val();

    if (!requestData) return res.status(404).send({ error: "Request not found" });

    if (approved) {
      const walletRef = db.ref(`wallets/${requestData.passengerNIC}`);
      const walletSnapshot = await walletRef.once("value");
      const balance = walletSnapshot.val()?.balance || 0;

      if (balance < requestData.fare || balance < 50) {
        return res.send({ success: false, message: "Insufficient balance (Minimum Rs.50 required)." });
      }

      await walletRef.update({ balance: balance - requestData.fare });
    }

    await requestRef.update({ status: approved ? "approved" : "declined" });
    res.send({ message: `Payment ${approved ? "approved and fare deducted" : "declined"}.` });
  } catch (err) {
    res.status(500).send({ error: err.message });
  }
});

// Reserve seat
app.post("/reserve-seat", async (req, res) => {
  const { busNumber, nic } = req.body;
  try {
    const seatRef = db.ref(`buses/${busNumber}/seats`);
    const snapshot = await seatRef.once("value");
    const seats = snapshot.val() || {};
    const availableSeat = Object.keys(seats).find(key => seats[key] === "available");

    if (!availableSeat) {
      return res.send({ success: false, message: "All seats are booked." });
    }

    await seatRef.update({ [availableSeat]: nic });
    res.send({ success: true, message: `Seat ${availableSeat} reserved successfully.` });
  } catch (err) {
    res.status(500).send({ error: err.message });
  }
});

// Payment Status tracking
app.get("/payment-status/:requestId", async (req, res) => {
  const { requestId } = req.params;
  try {
    const snapshot = await db.ref(`paymentRequests/${requestId}`).once("value");
    const data = snapshot.val();
    if (data) {
      res.send({ status: data.status });
    } else {
      res.status(404).send({ error: "Payment request not found" }); // Fixed this missing part
    }
  } catch (err) {
    res.status(500).send({ error: err.message });
  }
});

// Stripe payment intent
app.post("/create-payment-intent", async (req, res) => {
  const { amountLKR, startPoint, endPoint } = req.body;
  const USD_TO_LKR = 290;
  const amountUSD = (amountLKR / USD_TO_LKR).toFixed(2);
  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(amountUSD * 100),
      currency: "usd",
      description: `Payment from ${startPoint} to ${endPoint}`,
    });
    res.status(200).json({
      message: "Payment Intent created successfully",
      amountInLKR: amountLKR,
      clientSecret: paymentIntent.client_secret,
    });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// Stripe webhook
app.post("/webhook", bodyParser.raw({ type: "application/json" }), (req, res) => {
  const sig = req.headers["stripe-signature"];
  const endpointSecret = "whsec_wuzKKVoygsxoSqCdg1zgCHkCZr4J2NNl";
  let event;
  try {
    event = stripe.webhooks.constructEvent(req.body, sig, endpointSecret);
  } catch (err) {
    return res.status(400).send(`Webhook Error: ${err.message}`);
  }

  switch (event.type) {
    case "payment_intent.succeeded":
      console.log("Payment succeeded:", event.data.object);
      break;
    case "payment_intent.payment_failed":
      console.log("Payment failed:", event.data.object);
      break;
    default:
      console.log(`Unhandled event type: ${event.type}`);
  }

  res.json({ received: true });
});

// Start the server
app.listen(port, () => {
  console.log(`Server running on http://localhost:${port}`);
});
