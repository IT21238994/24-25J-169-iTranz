// backend/services/fcmService.js
const admin = require('firebase-admin');

// Initialize Firebase Admin SDK only once
if (!admin.apps.length) {
    const serviceAccount = require('../keys/itranz--fyp-firebase-adminsdk-bmr9i-51bd10695d.json'); // Your Firebase service key

    admin.initializeApp({
        credential: admin.credential.cert(serviceAccount),
        databaseURL: 'https://itranz--fyp-default-rtdb.firebaseio.com/' // Replace with your Firebase DB URL
    });
}

// Send FCM Notification to a Device
const sendNotification = async (fcmToken, title, body) => {
    const message = {
        notification: {
            title,
            body,
        },
        token: fcmToken,
    };

    try {
        const response = await admin.messaging().send(message);
        console.log('✅ FCM notification sent:', response);
        return response;
    } catch (error) {
        console.error('❌ Error sending FCM notification:', error);
        throw error;
    }
};

module.exports = { sendNotification };
