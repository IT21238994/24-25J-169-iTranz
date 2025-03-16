// backend/routes/paymentRoutes.js
const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');
const db = admin.database();
const stripe = require('stripe')("sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH"); // Replace with your Stripe secret key

// Get Wallet Balance
router.post('/getWalletBalance', async (req, res) => {
    const { nic } = req.body;
    try {
        const snapshot = await db.ref(`wallets/${nic}`).once('value');
        const balance = snapshot.val();
        if (balance !== null) {
            res.json({ balance });
        } else {
            res.status(404).json({ message: 'Wallet not found' });
        }
    } catch (error) {
        res.status(500).json({ message: 'Server error', error: error.message });
    }
});

// Confirm Payment
router.post('/confirmPayment', async (req, res) => {
    const { nic, amount } = req.body;

    try {
        const walletRef = db.ref(`wallets/${nic}`);
        const snapshot = await walletRef.once('value');
        let balance = snapshot.val();

        if (balance === null || balance < amount) {
            return res.status(400).json({ message: 'Insufficient balance' });
        }

        // Deduct wallet balance
        balance -= amount;
        await walletRef.set(balance);

        // Create Stripe PaymentIntent (converted to LKR, adjust as needed)
        const paymentIntent = await stripe.paymentIntents.create({
            amount: Math.round(amount * 100 / 290), // Convert to the correct amount (LKR to USD)
            currency: 'lkr',
            payment_method_types: ['card'],
        });

        res.json({ clientSecret: paymentIntent.client_secret });
    } catch (error) {
        res.status(500).json({ message: 'Payment failed', error: error.message });
    }
});

// Update Payment Status
router.post('/updatePaymentStatus', async (req, res) => {
    const { passengerNIC, status } = req.body;

    try {
        const ref = db.ref(`payments/${passengerNIC}`);
        await ref.set({ status, timestamp: new Date().toISOString() });

        res.json({ message: 'Payment status updated' });
    } catch (error) {
        res.status(500).json({ message: 'Failed to update status', error: error.message });
    }
});

// Create Payment Intent (new route for Stripe integration)
router.post('/create-payment-intent', async (req, res) => {
    try {
        const { amount } = req.body; // Dynamically pass the amount
        const paymentIntent = await stripe.paymentIntents.create({
            amount: Math.round(amount * 100 / 290), // Convert to correct amount in USD
            currency: 'lkr', // Use LKR currency
        });
        res.json({ clientSecret: paymentIntent.client_secret });
    } catch (error) {
        res.status(500).send({ error: error.message });
    }
});

module.exports = router;
