const { addDoc, queryDocs, getDoc, setDoc } = require('../services/firebaseService');
const { uploadImage } = require('../services/firebaseService');
const { hashImage, generateReportFingerprint } = require('../services/hashService');
const { notifyNearbyUsers } = require('../services/notificationService');
const { COLLECTIONS, REPORT_STATUS, FRAUD_TYPES } = require('../config/constants');
const { v4: uuidv4 } = require('uuid');

// POST /api/v1/reports
const submitReport = async (req, res) => {
  try {
    const { restaurantId, restaurantName, fraudType, description, lat, lng, mlScores } = req.body;
    const userId = req.user.uid;

    // Validate fraud type
    if (!Object.values(FRAUD_TYPES).includes(fraudType)) {
      return res.status(400).json({ error: 'Invalid fraud type' });
    }

    // Duplicate check
    const fingerprint = generateReportFingerprint({ restaurantId, fraudType, userId });
    const existing = await queryDocs(COLLECTIONS.REPORTS, [['fingerprint', '==', fingerprint]]);
    if (existing.length > 0) {
      return res.status(409).json({ error: 'You already reported this fraud type for this restaurant' });
    }

    // Handle image upload
    let imageUrl = null;
    let imageHash = null;
    if (req.file) {
      imageHash = hashImage(req.file.buffer);
      imageUrl = await uploadImage(req.file.buffer, `${uuidv4()}.jpg`, req.file.mimetype);
    }

    const reportId = await addDoc(COLLECTIONS.REPORTS, {
      restaurantId,
      restaurantName,
      fraudType,
      description,
      location: { lat: parseFloat(lat), lng: parseFloat(lng) },
      mlScores: mlScores ? JSON.parse(mlScores) : {},
      imageUrl,
      imageHash,
      fingerprint,
      userId,
      status: REPORT_STATUS.PENDING,
      votes: { up: 0, down: 0 },
    });

    // Update heatmap point
    await setDoc(COLLECTIONS.HEATMAP, `${restaurantId}_${fraudType}`, {
      restaurantId,
      restaurantName,
      lat: parseFloat(lat),
      lng: parseFloat(lng),
      fraudType,
      reportCount: (await getDoc(COLLECTIONS.HEATMAP, `${restaurantId}_${fraudType}`))?.reportCount + 1 || 1,
    });

    res.status(201).json({ reportId, message: 'Report submitted successfully' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to submit report' });
  }
};

// GET /api/v1/reports/:id
const getReport = async (req, res) => {
  try {
    const report = await getDoc(COLLECTIONS.REPORTS, req.params.id);
    if (!report) return res.status(404).json({ error: 'Report not found' });
    res.json(report);
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch report' });
  }
};

// GET /api/v1/reports?restaurantId=xxx
const getReportsByRestaurant = async (req, res) => {
  try {
    const { restaurantId } = req.query;
    if (!restaurantId) return res.status(400).json({ error: 'restaurantId required' });
    const reports = await queryDocs(COLLECTIONS.REPORTS, [
      ['restaurantId', '==', restaurantId],
      ['status', '==', REPORT_STATUS.VERIFIED]
    ]);
    res.json({ reports, count: reports.length });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch reports' });
  }
};

// POST /api/v1/reports/:id/vote
const voteReport = async (req, res) => {
  try {
    const { vote } = req.body; // 'up' or 'down'
    if (!['up', 'down'].includes(vote)) return res.status(400).json({ error: 'Invalid vote' });
    const report = await getDoc(COLLECTIONS.REPORTS, req.params.id);
    if (!report) return res.status(404).json({ error: 'Report not found' });
    const updated = { ...report.votes, [vote]: (report.votes[vote] || 0) + 1 };
    await setDoc(COLLECTIONS.REPORTS, req.params.id, { votes: updated });
    res.json({ votes: updated });
  } catch (err) {
    res.status(500).json({ error: 'Failed to vote' });
  }
};

module.exports = { submitReport, getReport, getReportsByRestaurant, voteReport };
