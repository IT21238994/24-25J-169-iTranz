const express = require('express');
const router = express.Router();
const admin = require('firebase-admin');

// Save NIC and FCM token to Firebase Realtime Database
router.post('/saveToken', async (req, res) => {
  const { nic, token } = req.body;
  if (!nic || !token) {
    return res.status(400).send({ error: 'NIC and token are required' });
  }

  try {
    await admin.database().ref(`fcmTokens/${nic}`).set(token);
    res.send({ message: '✅ Token saved successfully' });
  } catch (error) {
    console.error('❌ Error saving token:', error);
    res.status(500).send({ error: 'Internal Server Error' });
  }
});

module.exports = router;
