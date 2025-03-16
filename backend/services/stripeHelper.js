// backend/services/stripeHelper.js
const Stripe = require('stripe');
const stripe = Stripe("sk_test_51QR81201Cm8uyizaJdtP3Stq7D3kREo9DWyk4jVzYNfcgRpYQrv8KpCleHRgQMJ3QUsVo8oaSoz1kcXkyaJHSL6P00tqErfdSH"); // Replace with real secret key

// Create Payment Intent
const createPaymentIntent = async (amount, currency) => {
    try {
        const paymentIntent = await stripe.paymentIntents.create({
            amount,
            currency,
            payment_method_types: ['card'], // For mobile apps
        });

        return paymentIntent.client_secret; // Send this to frontend for payment
    } catch (error) {
        console.error('❌ Stripe Error:', error);
        throw error;
    }
};

module.exports = { createPaymentIntent };
