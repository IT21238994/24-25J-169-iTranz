const express = require('express');
const router = express.Router();
const { db } = require('../config/firebaseConfig');
const QRCode = require('qrcode');

router.post('/register', async (req, res) => {
  const { nic, busNumber, busName, busRoute, seatCount, stripeId } = req.body;

  try {
    const existingDriver = await db.collection('drivers').doc(busNumber).get();
    if (existingDriver.exists) {
      return res.status(400).json({ message: 'Bus Number already exists' });
    }

    const driverData = { nic, busNumber, busName, busRoute, seatCount, stripeId };
    await db.collection('drivers').doc(busNumber).set(driverData);

    const qrData = { busNumber, nic };
    const qrCodeURL = await QRCode.toDataURL(JSON.stringify(qrData));

    await db.collection('drivers').doc(busNumber).update({ qrCodeURL });

    res.status(200).json({ message: 'Driver Registered', qrCodeURL });
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
});

module.exports = router;
