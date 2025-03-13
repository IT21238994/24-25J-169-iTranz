const express = require("express");
const { db } = require("../config/firebaseConfig"); // Firebase setup
const router = express.Router();

// Add Passenger Details
router.post("/addPassenger", async (req, res) => {
  const { name, email, wallet } = req.body;

  try {
    const ref = db.ref("passengers").push();
    await ref.set({ name, email, wallet });
    res.status(201).json({ message: "Passenger added successfully", id: ref.key });
  } catch (error) {
    res.status(500).json({ message: "Error adding passenger", error });
  }
});

// Retrieve Passenger Details
router.get("/getPassengers", async (req, res) => {
  try {
    const snapshot = await db.ref("passengers").once("value");
    res.status(200).json(snapshot.val());
  } catch (error) {
    res.status(500).json({ message: "Error retrieving passengers", error });
  }
});

// Add Route Details
router.post("/addRoute", async (req, res) => {
  const { name, stops, charges } = req.body;

  try {
    const ref = db.ref("routes").push();
    await ref.set({ name, stops, charges });
    res.status(201).json({ message: "Route added successfully", id: ref.key });
  } catch (error) {
    res.status(500).json({ message: "Error adding route", error });
  }
});

// Retrieve Route Details
router.get("/getRoutes", async (req, res) => {
  try {
    const snapshot = await db.ref("routes").once("value");
    res.status(200).json(snapshot.val());
  } catch (error) {
    res.status(500).json({ message: "Error retrieving routes", error });
  }
});

module.exports = router;
