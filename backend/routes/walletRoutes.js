const express = require('express');
const router = express.Router();
const { topUpWallet, getWalletBalance } = require('../controllers/walletController');

router.post('/top-up', topUpWallet);
router.get('/balance/:nic', getWalletBalance);

module.exports = router;