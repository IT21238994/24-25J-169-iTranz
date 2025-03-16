const express = require('express');
const router = express.Router();
const { db } = require('../config/firebaseConfig');

// Stripe configuration using your secret key
const stripe = require('stripe')('sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH');

// -------------------
// Route: Calculate Fare
// -------------------
router.post('/calculateFare', async (req, res) => {
  const { routeName, startStop, endStop } = req.body;

  try {
    const routeDoc = await db.collection('routes').doc(routeName).get();

    if (!routeDoc.exists) {
      return res.status(404).json({ message: 'Route not found' });
    }

    const stops = routeDoc.data().busStops;

    const start = stops.find(stop => stop.stopName === startStop);
    const end = stops.find(stop => stop.stopName === endStop);

    if (!start || !end) {
      return res.status(400).json({ message: 'Invalid stop names' });
    }

    const fare = Math.abs(end.fare - start.fare); // Calculate fare difference
    res.status(200).json({ fare });
  } catch (err) {
    console.error('Fare Calculation Error:', err);
    res.status(500).json({ error: err.message });
  }
});

// -------------------
// Route: Payment Processing
// -------------------
router.post('/pay', async (req, res) => {
  const { nic, fare, driverStripeId } = req.body;

  try {
    const passengerRef = db.collection('passengers').doc(nic);
    const passengerDoc = await passengerRef.get();

    if (!passengerDoc.exists) {
      return res.status(404).json({ message: 'Passenger not found' });
    }

    const balance = passengerDoc.data().balance;

    if (balance < fare) {
      return res.status(400).json({ message: 'Insufficient balance' });
    }

    // Convert fare from LKR to USD (1 USD = 290 LKR)
    const fareUSD = (fare / 290).toFixed(2);

    // Create payment intent with transfer to driver
    const paymentIntent = await stripe.paymentIntents.create({
      amount: Math.round(fareUSD * 100), // Stripe expects amount in cents
      currency: 'usd',
      payment_method_types: ['card'],
      transfer_data: {
        destination: driverStripeId, // Driver's Stripe account ID
      }
    });

    // Deduct fare from passenger's balance
    await passengerRef.update({ balance: balance - fare });

    res.status(200).json({
      message: 'Payment Successful',
      clientSecret: paymentIntent.client_secret,
    });
  } catch (err) {
    console.error('Payment Error:', err);
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
