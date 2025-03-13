const express = require("express");
const { db } = require("../config/firebaseConfig");

const router = express.Router();

// Route to reserve seats (multiple seats can be reserved at once)
router.post("/reserve", async (req, res) => {
  const { uid, route, seatsToReserve, busName, busNumber } = req.body;

  try {
    // Reference to the reservations node for the specific bus and route
    const busRef = db.ref(`reservations/${busNumber}/${route}`);

    // Check how many seats are already reserved
    const snapshot = await busRef.once("value");
    const existingReservations = snapshot.val() || {};

    const totalSeats = 40; // Assume there are 40 seats available on the bus
    const reservedSeats = Object.keys(existingReservations).length; // Count reserved seats

    if (reservedSeats + seatsToReserve > totalSeats) {
      return res.status(400).json({ message: "Not enough available seats" });
    }

    // Reserve the seats
    const newReservations = {};
    for (let i = 0; i < seatsToReserve; i++) {
      const seatNumber = reservedSeats + i + 1; // Increment the seat number for reservation
      newReservations[seatNumber] = {
        uid,
        busName,
        reservedAt: new Date().toISOString(),
      };
    }

    await busRef.update(newReservations);

    res.status(200).json({
      message: `${seatsToReserve} seats reserved successfully`,
      reservedSeats: newReservations,
    });
  } catch (error) {
    res.status(500).json({ message: "Error reserving seats", error });
  }
});

// Route to get all reservations (summary) for a specific bus and route
router.get("/getReservations/:busNumber/:route", async (req, res) => {
  const { busNumber, route } = req.params;

  try {
    const reservationsRef = db.ref(`reservations/${busNumber}/${route}`);
    const snapshot = await reservationsRef.once("value");

    if (snapshot.exists()) {
      const reservations = snapshot.val();
      // Prepare summary of reservations with username, userId, and number of seats booked
      const summary = Object.values(reservations).reduce((acc, reservation) => {
        const { uid, busName } = reservation;
        if (!acc[uid]) {
          acc[uid] = { username: uid, busName, seatsBooked: 0 };
        }
        acc[uid].seatsBooked += 1;
        return acc;
      }, {});

      res.status(200).json({
        message: "Reservations retrieved successfully",
        summary: Object.values(summary),
      });
    } else {
      res.status(404).json({ message: "No reservations found" });
    }
  } catch (error) {
    res.status(500).json({ message: "Error retrieving reservations", error });
  }
});

// Route to get all reservations (for any route and bus)
router.get("/getAllReservations", async (req, res) => {
  try {
    const reservationsRef = db.ref("reservations");
    const snapshot = await reservationsRef.once("value");

    if (snapshot.exists()) {
      res.status(200).json({ message: "All reservations retrieved successfully", reservations: snapshot.val() });
    } else {
      res.status(404).json({ message: "No reservations found" });
    }
  } catch (error) {
    res.status(500).json({ message: "Error retrieving all reservations", error });
  }
});

// Route to delete a reservation by bus number, route, and seat number
router.delete("/deleteReservation/:busNumber/:route/:seatNumber", async (req, res) => {
  const { busNumber, route, seatNumber } = req.params;

  try {
    const seatRef = db.ref(`reservations/${busNumber}/${route}/${seatNumber}`);
    const snapshot = await seatRef.once("value");

    if (!snapshot.exists()) {
      return res.status(404).json({ message: "Reservation not found" });
    }

    // Delete the reservation
    await seatRef.remove();
    res.status(200).json({ message: "Reservation deleted successfully" });
  } catch (error) {
    res.status(500).json({ message: "Error deleting reservation", error });
  }
});

module.exports = router;
