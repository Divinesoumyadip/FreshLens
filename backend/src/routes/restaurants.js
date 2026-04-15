const router = require('express').Router();
const { authenticate } = require('../middleware/authMiddleware');
const { getTrustScore, upsertRestaurant } = require('../controllers/restaurantController');

router.get('/:id/trust-score', authenticate, getTrustScore);
router.post('/', authenticate, upsertRestaurant);

module.exports = router;
