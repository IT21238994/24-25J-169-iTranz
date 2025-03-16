const express = require('express');
const router = express.Router();
const { db } = require('../config/firebaseConfig');

router.post('/register', async (req, res) => {
  const { name, email, nic, familyNics } = req.body;

  try {
    const existingPassenger = await db.collection('passengers').doc(nic).get();
    if (existingPassenger.exists) {
      return res.status(400).json({ message: 'NIC already exists' });
    }

    const passengerData = { name, email, nic, familyNics, balance: 1000 }; // Starting balance Rs.1000
    await db.collection('passengers').doc(nic).set(passengerData);

    res.status(200).json({ message: 'Passenger Registered' });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
