const firebase = require("firebase-admin");
const serviceAccount = require("../keys/itranz--fyp-firebase-adminsdk-bmr9i-f965f7f035.json");

if (!firebase.apps.length) {
  firebase.initializeApp({
    credential: firebase.credential.cert(serviceAccount),
    databaseURL: "https://itranz--fyp-default-rtdb.firebaseio.com/",
  });
} else {
  firebase.app();
}

const db = firebase.database();
const admin = firebase.auth();

module.exports = { admin, db };



