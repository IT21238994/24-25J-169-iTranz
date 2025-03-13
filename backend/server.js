const express = require("express");
const bodyParser = require("body-parser");
const cors = require("cors");
const { admin, db } = require("./config/firebaseConfig"); // Firebase configuration
const authRoutes = require("./routes/authRoutes");
const userRoutes = require("./routes/userRoutes");
const paymentRoute = require("./routes/paymentRoute");
const reservationRoutes = require("./routes/reservationRoutes");
const driverRoutes = require("./routes/driverRoutes"); // Import driver routes (now includes seat reservation)
const dataRoutes = require("./routes/dataRoutes"); // Data routes for additional operations
const stripe = require("stripe")("sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH"); // Replace with your Stripe secret key
//const sig = req.headers['stripe-signature'];  // Get the Stripe-Signature from the request header


const app = express();
const port = 3000;

// Middleware
app.use(bodyParser.json());
app.use(cors());

// Routes
app.use("/auth", authRoutes);
app.use("/user", userRoutes);
app.use("/api", require("./routes/paymentRoute"));
app.use("/reservation", reservationRoutes);
app.use("/driver", driverRoutes); // Use driver routes (now includes seat reservation)
app.use("/data", dataRoutes); // Connect data routes

// Test Route
app.get("/", (req, res) => {
  res.send("Backend is running");
});

// Verify Firebase ID token
app.post("/verifyToken", async (req, res) => {
  const { idToken } = req.body;
  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    const uid = decodedToken.uid;
    res.status(200).json({ message: "Token is valid", uid });
  } catch (error) {
    res.status(401).json({ message: "Unauthorized", error });
  }
});

// Save data to Firebase
app.post("/saveData", async (req, res) => {
  const { uid, data } = req.body;
  try {
    const ref = db.ref("users/" + uid);
    await ref.set(data);
    res.status(200).json({ message: "Data saved successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error saving data", error });
  }
});

// Create Payment Intent (Stripe integration)
app.post("/create-payment-intent", async (req, res) => {
  const { amount, currency = "usd", startPoint, endPoint } = req.body; // amount in USD by default
  try {
    if (amount < 50) {
      // Stripe's minimum amount threshold in cents (for USD)
      return res.status(400).json({ error: "Amount must be at least 50 cents." });
    }

    // Conversion rate (example: 1 USD = 290 LKR)
    const USD_TO_LKR = 290;  // Set your exchange rate
    const amountInLKR = amount * USD_TO_LKR; // Convert USD to LKR

    // Now create a Payment Intent with the converted LKR amount
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amountInLKR, // Amount in LKR
      currency: "lkr", // Set currency as LKR
      description: `Payment from ${startPoint} to ${endPoint}`,
    });

    res.status(200).json({
      message: "Payment Intent created successfully",
      amountInLKR, // Return the amount in LKR to display on the frontend
      clientSecret: paymentIntent.client_secret, // Send the client secret to frontend for confirmation
    });
  } catch (error) {
    console.error("Error creating payment intent:", error);
    res.status(500).json({ error: error.message });
  }
});

// Stripe Webhook for payment status
app.post("/webhook", bodyParser.raw({ type: "application/json" }), (req, res) => {
  const sig = req.headers["stripe-signature"];
  const endpointSecret = "whsec_wuzKKVoygsxoSqCdg1zgCHkCZr4J2NNl"; // Replace with your Stripe webhook secret

  let event;

  try {
    // Verify the webhook signature
    event = stripe.webhooks.constructEvent(req.body, sig, endpointSecret);
  } catch (err) {
    console.error(`Webhook signature verification failed: ${err.message}`);
    return res.status(400).send(`Webhook Error: ${err.message}`);
  }

  // Handle payment events
  switch (event.type) {
    case "payment_intent.succeeded":
      const paymentIntent = event.data.object;
      console.log("PaymentIntent was successful:", paymentIntent);
      // You can handle further actions here, such as updating your database or notifying the user
      break;
    case "payment_intent.payment_failed":
      const paymentError = event.data.object;
      console.log("Payment failed:", paymentError);
      // Handle payment failure actions
      break;
    default:
      console.log(`Unhandled event type: ${event.type}`);
  }

  // Acknowledge receipt of the event
  res.json({ received: true });
});

// Start Server
app.listen(port, () => {
  console.log(`Server running on http://localhost:${port}`);
});
