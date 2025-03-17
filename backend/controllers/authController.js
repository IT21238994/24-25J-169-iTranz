const { db } = require('../config/firebaseConfig');

exports.login = async (req, res) => {
  const { nic } = req.body;
  try {
    const passengerRef = db.ref(`passengers/${nic}`);
    const snapshot = await passengerRef.once('value');
    if (snapshot.exists()) {
      return res.json({ message: 'Login successful', userType: 'passenger', data: snapshot.val() });
    }
    const driverRef = db.ref(`drivers/${nic}`);
    const driverSnap = await driverRef.once('value');
    if (driverSnap.exists()) {
      return res.json({ message: 'Login successful', userType: 'driver', data: driverSnap.val() });
    }
    res.status(404).json({ message: 'NIC not found.' });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};