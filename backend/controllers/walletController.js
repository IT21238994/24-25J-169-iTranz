const { db } = require('../config/firebaseConfig');

exports.topUpWallet = async (req, res) => {
  const { nic, amount } = req.body;
  try {
    const walletRef = db.ref(`wallets/${nic}`);
    const snapshot = await walletRef.once('value');
    let newBalance = amount;
    if (snapshot.exists()) {
      const currentBalance = snapshot.val().balance;
      newBalance += currentBalance;
    }
    await walletRef.set({ balance: newBalance });
    res.json({ message: 'Wallet topped up successfully.', balance: newBalance });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};

exports.getWalletBalance = async (req, res) => {
  const { nic } = req.params;
  try {
    const walletRef = db.ref(`wallets/${nic}`);
    const snapshot = await walletRef.once('value');
    if (snapshot.exists()) {
      res.json({ balance: snapshot.val().balance });
    } else {
      res.json({ balance: 0 });
    }
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
};