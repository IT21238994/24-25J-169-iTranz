const express = require("express");
const { db } = require("../config/firebaseConfig");
const generateQRCode = require("../generateQRCode");

const router = express.Router();

// Route to add a new driver
router.post("/add", async (req, res) => {
  const { name, busName, route, seats, busNumber } = req.body;

  try {
    const driversRef = db.ref("drivers");
    const snapshot = await driversRef.once("value");

    const drivers = snapshot.val() || {};
    const existingBus = Object.values(drivers).find(driver => driver.busNumber === busNumber);

    if (existingBus) {
      return res.status(400).json({ message: "Bus has already been registered" });
    }

    const driverCount = snapshot.exists() ? snapshot.numChildren() + 1 : 1;
    const driverId = `driver${driverCount}`;

    const newDriver = {
      name,
      busName,
      route,
      seats,
      busNumber,
    };

    await driversRef.child(driverId).set(newDriver);

    // Generate the QR code for the driver
    const qrCodeBase64 = await generateQRCode(driverId);  // Generate QR code for driverId

    res.status(200).json({
      message: "Driver added successfully",
      driverId,
      qrCode: `data:image/png;base64,${qrCodeBase64}`  // Return the QR code base64 data
    });
  } catch (error) {
    res.status(500).json({ message: "Error adding driver", error });
  }
});

// Route to get all drivers
router.get("/getAll", async (req, res) => {
  try {
    const driversRef = db.ref("drivers");
    const snapshot = await driversRef.once("value");

    if (snapshot.exists()) {
      res.status(200).json(snapshot.val());
    } else {
      res.status(404).json({ message: "No drivers found" });
    }
  } catch (error) {
    res.status(500).json({ message: "Error retrieving drivers", error });
  }
});

// Route to update a driver by ID
router.put("/update/:driverId", async (req, res) => {
  const { driverId } = req.params;
  const { name, busName, route, seats } = req.body;

  try {
    const driverRef = db.ref(`drivers/${driverId}`);
    await driverRef.update({
      name,
      busName,
      route,
      seats,
    });

    res.status(200).json({ message: "Driver updated successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error updating driver", error });
  }
});

// Route to delete a driver by ID
router.delete("/delete/:driverId", async (req, res) => {
  const { driverId } = req.params;

  try {
    const driverRef = db.ref(`drivers/${driverId}`);
    await driverRef.remove();

    res.status(200).json({ message: "Driver deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error deleting driver", error });
  }
});

module.exports = router;

