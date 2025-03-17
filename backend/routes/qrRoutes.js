const express = require('express');
const router = express.Router();
const { generateDriverQRCode } = require('../controllers/qrController');

router.post('/generate-driver-qr', generateDriverQRCode);

module.exports = router;