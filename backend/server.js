const express = require("express");
require("dotenv").config();
const { admin, db } = require("./config/firebaseConfig");
const stripe = require("stripe")("sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH");

const app = express();
const port = 3000;


app.get("/", (req, res) => {
  res.send("Backend is running");
});

app.get("/test", (req, res) => {
  res.json({ received: true });
});

app.listen(port, () => {
  console.log('Server running on http://localhost:3000');
});
