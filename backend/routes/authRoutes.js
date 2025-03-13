const express = require("express");
const router = express.Router();
const { admin } = require("../config/firebaseConfig");

// Verify Firebase ID Token
router.post("/verifyToken", async (req, res) => {
  const { idToken } = req.body;

  try {
    const decodedToken = await admin.auth().verifyIdToken(idToken);
    const uid = decodedToken.uid;

    res.status(200).json({
      message: "Token is valid",
      uid: uid,
    });
  } catch (error) {
    res.status(401).json({
      message: "Unauthorized",
      error: error.message,
    });
  }
});

module.exports = router;
