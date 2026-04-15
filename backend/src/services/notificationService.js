const { messaging } = require('../config/firebase');

const sendToToken = async (fcmToken, title, body, data = {}) => {
  try {
    await messaging.send({
      token: fcmToken,
      notification: { title, body },
      data,
      android: { priority: 'high' }
    });
  } catch (err) {
    console.error('FCM send error:', err.message);
  }
};

// Notify nearby users about a new fraud report
const notifyNearbyUsers = async (tokens, restaurantName) => {
  if (!tokens.length) return;
  const message = {
    notification: {
      title: '⚠️ FreshLens Alert',
      body: `Fraud reported at ${restaurantName} near you.`
    },
    tokens,
    android: { priority: 'high' }
  };
  await messaging.sendEachForMulticast(message);
};

module.exports = { sendToToken, notifyNearbyUsers };
