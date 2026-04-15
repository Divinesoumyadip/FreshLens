const { getDoc } = require('../services/firebaseService');
const { COLLECTIONS } = require('../config/constants');

// GET /api/v1/users/me
const getProfile = async (req, res) => {
  try {
    const user = await getDoc(COLLECTIONS.USERS, req.user.uid);
    if (!user) return res.status(404).json({ error: 'User not found' });
    res.json(user);
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch profile' });
  }
};

module.exports = { getProfile };
