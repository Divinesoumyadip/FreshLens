const router = require('express').Router();
const multer = require('multer');
const { authenticate } = require('../middleware/authMiddleware');
const { reportLimiter, uploadLimiter } = require('../middleware/rateLimiter');
const {
  submitHygieneReport,
  getKitchenSafeScore,
  getHygieneReports,
  fssaiLookup,
} = require('../controllers/kitchenSafeController');

const upload = multer({ storage: multer.memoryStorage(), limits: { fileSize: 5 * 1024 * 1024 } });

// Submit hygiene violation report with optional photo
router.post('/report', authenticate, reportLimiter, upload.single('image'), submitHygieneReport);

// Get KitchenSafe score for a restaurant
router.get('/score/:restaurantId', authenticate, getKitchenSafeScore);

// Get all verified hygiene reports for a restaurant
router.get('/reports/:restaurantId', authenticate, getHygieneReports);

// FSSAI license lookup
router.post('/fssai-lookup', authenticate, fssaiLookup);

module.exports = router;
