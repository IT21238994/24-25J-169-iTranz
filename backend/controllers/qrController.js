const { db } = require('../config/firebaseConfig');
const { generateQRCode } = require('../utils/generateQRCode');

exports.generateDriverQRCode = async (req, res) => {
  const { nic } = req.body;
  try {
    const qrData = `driver:${nic}`;
    const qrImage = await generateQRCode(qrData);
    await db.ref(`drivers/${nic}/qrCode`).set(qrImage);
    res.json({ message: 'QR code generated.', qrImage });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};