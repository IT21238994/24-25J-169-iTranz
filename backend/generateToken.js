const { admin } = require("./config/firebaseConfig"); // Firebase Admin SDK setup

async function generateToken() {
  try {
    // Replace with a valid UID from Firebase Authentication Console
    const testUserUid = "YOUR_TEST_USER_UID"; // Example: "user123"

    // Generate a custom token
    const customToken = await admin.auth().createCustomToken(testUserUid);

    console.log("Generated Firebase Custom Token:");
    console.log(customToken);
  } catch (error) {
    console.error("Error generating custom token:", error);
  }
}

// Run the token generation function
generateToken();
