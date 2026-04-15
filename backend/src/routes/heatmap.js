const router = require('express').Router();
const { authenticate } = require('../middleware/authMiddleware');
const { getHeatmapPoints, getCityHeatmap } = require('../controllers/heatmapController');

router.get('/', authenticate, getHeatmapPoints);
router.get('/city', authenticate, getCityHeatmap);

module.exports = router;
