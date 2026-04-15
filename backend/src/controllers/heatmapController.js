const { db } = require('../config/firebase');
const { COLLECTIONS } = require('../config/constants');

// GET /api/v1/heatmap?lat=xx&lng=yy&radius=5000
// Returns heatmap points within radius (meters) using Firestore geo bounding box
const getHeatmapPoints = async (req, res) => {
  try {
    const { lat, lng, radius = 5000 } = req.query;
    if (!lat || !lng) return res.status(400).json({ error: 'lat and lng required' });

    const latF = parseFloat(lat);
    const lngF = parseFloat(lng);

    // Approx bounding box (1 deg lat ≈ 111km)
    const delta = parseFloat(radius) / 111000;
    const snap = await db.collection(COLLECTIONS.HEATMAP)
      .where('lat', '>=', latF - delta)
      .where('lat', '<=', latF + delta)
      .get();

    const points = snap.docs
      .map(d => d.data())
      .filter(p => Math.abs(p.lng - lngF) <= delta);

    res.json({ points, count: points.length });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch heatmap' });
  }
};

// GET /api/v1/heatmap/city?city=Kolkata
const getCityHeatmap = async (req, res) => {
  try {
    const { city } = req.query;
    if (!city) return res.status(400).json({ error: 'city required' });
    const snap = await db.collection(COLLECTIONS.HEATMAP)
      .where('city', '==', city)
      .orderBy('reportCount', 'desc')
      .limit(100)
      .get();
    const points = snap.docs.map(d => ({ id: d.id, ...d.data() }));
    res.json({ points, count: points.length });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch city heatmap' });
  }
};

module.exports = { getHeatmapPoints, getCityHeatmap };
