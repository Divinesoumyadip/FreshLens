const { addDoc, queryDocs, getDoc, setDoc } = require('../services/firebaseService');
const { uploadImage } = require('../services/firebaseService');
const { hashImage } = require('../services/hashService');
const { lookupFSSAI } = require('../services/fssaiService');
const { COLLECTIONS } = require('../config/constants');
const { v4: uuidv4 } = require('uuid');

const KITCHEN_COLLECTIONS = {
  HYGIENE_REPORTS: 'hygiene_reports',
  KITCHEN_SCORES: 'kitchen_scores',
};

// POST /api/v1/kitchen/report
// User submits hygiene violation (dirty packaging, contamination photo, etc.)
const submitHygieneReport = async (req, res) => {
  try {
    const { restaurantId, restaurantName, violationType, description, lat, lng, mlHygieneScore } = req.body;
    const userId = req.user.uid;

    const VIOLATION_TYPES = [
      'dirty_packaging',
      'contamination_visible',
      'tampered_seal',
      'foreign_object',
      'temperature_abuse',
      'pest_evidence',
      'unhygienic_handling',
    ];

    if (!VIOLATION_TYPES.includes(violationType)) {
      return res.status(400).json({ error: 'Invalid violation type', validTypes: VIOLATION_TYPES });
    }

    let imageUrl = null;
    let imageHash = null;
    if (req.file) {
      imageHash = hashImage(req.file.buffer);
      imageUrl = await uploadImage(req.file.buffer, `hygiene_${uuidv4()}.jpg`, req.file.mimetype);
    }

    const reportId = await addDoc(KITCHEN_COLLECTIONS.HYGIENE_REPORTS, {
      restaurantId,
      restaurantName,
      violationType,
      description,
      location: { lat: parseFloat(lat), lng: parseFloat(lng) },
      mlHygieneScore: mlHygieneScore ? parseFloat(mlHygieneScore) : null,
      imageUrl,
      imageHash,
      userId,
      status: 'pending',
      votes: { up: 0, down: 0 },
    });

    // Update kitchen safety score
    await _recalculateKitchenScore(restaurantId, restaurantName);

    res.status(201).json({ reportId, message: 'Hygiene report submitted' });
  } catch (err) {
    console.error(err);
    res.status(500).json({ error: 'Failed to submit hygiene report' });
  }
};

// GET /api/v1/kitchen/score/:restaurantId
const getKitchenSafeScore = async (req, res) => {
  try {
    const { restaurantId } = req.params;
    const score = await getDoc(KITCHEN_COLLECTIONS.KITCHEN_SCORES, restaurantId);

    // Also fetch FSSAI license status
    const { fssaiLicense } = req.query;
    let fssaiData = null;
    if (fssaiLicense) {
      fssaiData = await lookupFSSAI(fssaiLicense);
    }

    if (!score) {
      return res.json({
        restaurantId,
        kitchenSafeScore: 100, // default — no reports yet
        badge: 'UNVERIFIED',
        totalHygieneReports: 0,
        fssai: fssaiData,
      });
    }

    res.json({ ...score, fssai: fssaiData });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch kitchen score' });
  }
};

// GET /api/v1/kitchen/reports/:restaurantId
const getHygieneReports = async (req, res) => {
  try {
    const reports = await queryDocs(KITCHEN_COLLECTIONS.HYGIENE_REPORTS, [
      ['restaurantId', '==', req.params.restaurantId],
      ['status', '==', 'verified'],
    ]);
    res.json({ reports, count: reports.length });
  } catch (err) {
    res.status(500).json({ error: 'Failed to fetch hygiene reports' });
  }
};

// POST /api/v1/kitchen/fssai-lookup
const fssaiLookup = async (req, res) => {
  try {
    const { licenseNumber } = req.body;
    if (!licenseNumber) return res.status(400).json({ error: 'licenseNumber required' });
    const data = await lookupFSSAI(licenseNumber);
    res.json(data);
  } catch (err) {
    res.status(500).json({ error: 'FSSAI lookup failed' });
  }
};

// Internal: recalculate KitchenSafe score after new report
const _recalculateKitchenScore = async (restaurantId, restaurantName) => {
  const reports = await queryDocs(KITCHEN_COLLECTIONS.HYGIENE_REPORTS, [
    ['restaurantId', '==', restaurantId],
  ]);

  const total = reports.length;
  // Severity weights per violation type
  const SEVERITY = {
    pest_evidence: 25,
    contamination_visible: 20,
    foreign_object: 20,
    temperature_abuse: 15,
    tampered_seal: 15,
    unhygienic_handling: 10,
    dirty_packaging: 5,
  };

  const totalDeduction = reports.reduce((acc, r) => acc + (SEVERITY[r.violationType] || 10), 0);
  const kitchenSafeScore = Math.max(0, 100 - totalDeduction);

  let badge = 'GREEN'; // 80-100
  if (kitchenSafeScore < 80) badge = 'YELLOW';
  if (kitchenSafeScore < 50) badge = 'RED';
  if (kitchenSafeScore < 20) badge = 'BLACKLISTED';

  await setDoc(KITCHEN_COLLECTIONS.KITCHEN_SCORES, restaurantId, {
    restaurantId,
    restaurantName,
    kitchenSafeScore,
    badge,
    totalHygieneReports: total,
    lastUpdated: new Date(),
  });
};

module.exports = { submitHygieneReport, getKitchenSafeScore, getHygieneReports, fssaiLookup };
