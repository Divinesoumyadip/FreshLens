const { getDoc, setDoc, addDoc, queryDocs } = require('../services/firebaseService');
const { COLLECTIONS } = require('../config/constants');

// GET /api/v1/restaurants/:id/trust-score
const getTrustScore = async (req, res) => {
  try {
    const restaurant = await getDoc(COLLECTIONS.RESTAURANTS, req.params.id);
    if (!restaurant) return res.status(404).json({ error: 'Restaurant not found' });

    const reports = await queryDocs(COLLECTIONS.REPORTS, [
      ['restaurantId', '==', req.params.id],
      ['status', '==', 'verified']
    ]);

    // Simple trust score: start at 100, -10 per verified fraud report, min 0
    const trustScore = Math.max(0, 100 - (reports.length * 10));
    const fraudBreakdown = reports.reduce((acc, r) => {
      acc[r.fraudType] = (acc[r.fraudType] || 0) + 1;
      return acc;
    }, {});

    res.json({
      restaurantId: req.params.id,
      name: restaurant.name,
      trustScore,
      totalReports: reports.length,
      fraudBreakdown
    });
  } catch (err) {
    res.status(500).json({ error: 'Failed to compute trust score' });
  }
};

// POST /api/v1/restaurants  (upsert from Android app)
const upsertRestaurant = async (req, res) => {
  try {
    const { id, name, lat, lng, city, platform } = req.body;
    await setDoc(COLLECTIONS.RESTAURANTS, id, { name, lat, lng, city, platform });
    res.json({ message: 'Restaurant saved' });
  } catch (err) {
    res.status(500).json({ error: 'Failed to save restaurant' });
  }
};

module.exports = { getTrustScore, upsertRestaurant };
