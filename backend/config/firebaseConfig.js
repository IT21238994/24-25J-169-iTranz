const firebase = require("firebase-admin");
const serviceAccount = require("../keys/itranz--fyp-firebase-adminsdk-bmr9i-51bd10695d");

firebase.initializeApp({
  credential: firebase.credential.cert(serviceAccount),
  databaseURL: "https://itranz--fyp-default-rtdb.firebaseio.com/",
});

const db = firebase.database();
const admin = firebase.auth();

module.exports = { admin, db };
