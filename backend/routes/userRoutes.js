// Updated userRoute.js
const express = require("express");
const { db } = require("../config/firebaseConfig");

const router = express.Router();

// Helper function to validate NIC
function validateNIC(nic) {
  if (nic.endsWith("v")) {
    // NIC ending with "v" should have exactly 9 digits before "v"
    const numericPart = nic.slice(0, -1); // Remove "v"
    return /^[0-9]{9}$/.test(numericPart);
  } else {
    // NIC without "v" should have exactly 12 digits
    return /^[0-9]{12}$/.test(nic);
  }
}

// Route to register a new user
router.post("/register", async (req, res) => {
  const { name, email, wallet, nic } = req.body;

  // Validate NIC
  if (!validateNIC(nic)) {
    return res.status(400).json({ message: "Invalid NIC format" });
  }

  try {
    // Check if the NIC already exists in the database
    const usersRef = db.ref("users");
    const snapshot = await usersRef.once("value");
    const users = snapshot.val() || {};

    // Check if the NIC is already registered
    const existingUser = Object.values(users).find(user => user.nic === nic);
    if (existingUser) {
      return res.status(400).json({ message: "User already has an account with this NIC" });
    }

    // Generate a unique userId (auto-increment)
    const userCount = snapshot.exists() ? snapshot.numChildren() + 1 : 1;
    const userId = `user${userCount}`;

    // Save user data to Firebase
    const newUser = {
      name,
      email,
      wallet,
      nic,
    };

    await usersRef.child(userId).set(newUser);

    res.status(200).json({
      message: "User registered successfully",
      userId, // Return the generated userId
    });
  } catch (error) {
    res.status(500).json({ message: "Error registering user", error });
  }
});

// Route to get all users
router.get("/getAll", async (req, res) => {
  try {
    const usersRef = db.ref("users");
    const snapshot = await usersRef.once("value");

    if (snapshot.exists()) {
      res.status(200).json(snapshot.val());
    } else {
      res.status(404).json({ message: "No users found" });
    }
  } catch (error) {
    res.status(500).json({ message: "Error retrieving users", error });
  }
});

// Route to get a user by ID
router.get("/get/:userId", async (req, res) => {
  const { userId } = req.params;

  try {
    const userRef = db.ref(`users/${userId}`);
    const snapshot = await userRef.once("value");

    if (snapshot.exists()) {
      res.status(200).json(snapshot.val());
    } else {
      res.status(404).json({ message: "User not found" });
    }
  } catch (error) {
    res.status(500).json({ message: "Error retrieving user", error });
  }
});

// Route to update a user by ID
router.put("/update/:userId", async (req, res) => {
  const { userId } = req.params;
  const { name, email, wallet, nic } = req.body;

  // Validate NIC
  if (!validateNIC(nic)) {
    return res.status(400).json({ message: "Invalid NIC format" });
  }

  try {
    const userRef = db.ref(`users/${userId}`);
    await userRef.update({
      name,
      email,
      wallet,
      nic,
    });

    res.status(200).json({ message: "User updated successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error updating user", error });
  }
});

// Route to delete a user by ID
router.delete("/delete/:userId", async (req, res) => {
  const { userId } = req.params;

  try {
    const userRef = db.ref(`users/${userId}`);
    await userRef.remove();

    res.status(200).json({ message: "User deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error deleting user", error });
  }
});

module.exports = router;
