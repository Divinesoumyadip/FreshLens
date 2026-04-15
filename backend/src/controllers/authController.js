const { auth, db } = require('../config/firebase');
const { COLLECTIONS } = require('../config/constants');

// POST /api/v1/auth/register  — called after Firebase client-side signup
const registerUser = async (req, res) => {
  try {
    const { fcmToken } = req.body;
    const userId = req.user.uid;
    const firebaseUser = await auth.getUser(userId);

    await db.collection(COLLECTIONS.USERS).doc(userId).set({
      email: firebaseUser.email,
      displayName: firebaseUser.displayName || '',
      fcmToken: fcmToken || null,
      reportsCount: 0,
      trustLevel: 'normal',
      createdAt: new Date(),
    }, { merge: true });

    res.json({ message: 'User registered', userId });
  } catch (err) {
    res.status(500).json({ error: 'Registration failed' });
  }
};

// PATCH /api/v1/auth/fcm-token
const updateFcmToken = async (req, res) => {
  try {
    const { fcmToken } = req.body;
    await db.collection(COLLECTIONS.USERS).doc(req.user.uid).update({ fcmToken });
    res.json({ message: 'FCM token updated' });
  } catch (err) {
    res.status(500).json({ error: 'Failed to update token' });
  }
};

module.exports = { registerUser, updateFcmToken };
