// backend/routes/authRoutes.js
const express = require('express');
const router = express.Router();
const { login } = require('../controllers/authController');
const { db } = require('../config/firebaseConfig');

// Register Passenger
router.post('/register-passenger', async (req, res) => {
    const { name, email, nic, familyNICs = [] } = req.body;

    if (!name || !email || !nic) {
        return res.status(400).send({ error: 'Missing required passenger details.' });
    }

    try {
        const passengerRef = db.ref(`passengers/${nic}`);
        await passengerRef.set({ name, email, nic, familyNICs });
        res.send({ message: 'Passenger registered successfully.' });
    } catch (error) {
        res.status(500).send({ error: error.message });
    }
});

// Register Driver
router.post('/register-driver', async (req, res) => {
    const { nic, busNumber, busName, routeName, seatCount, stripeAccountId } = req.body;

    if (!nic || !busNumber || !busName || !routeName || !seatCount || !stripeAccountId) {
        return res.status(400).send({ error: 'Missing required driver details.' });
    }

    try {
        const driverRef = db.ref(`drivers/${nic}`);
        await driverRef.set({
            nic,
            busNumber,
            busName,
            routeName,
            seatCount,
            stripeAccountId
        });
        res.send({ message: 'Driver registered successfully.' });
    } catch (error) {
        res.status(500).send({ error: error.message });
    }
});
router.post('/login', login);

module.exports = router;
