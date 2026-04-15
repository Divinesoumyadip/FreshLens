const router = require('express').Router();
const multer = require('multer');
const { authenticate } = require('../middleware/authMiddleware');
const { reportLimiter, uploadLimiter } = require('../middleware/rateLimiter');
const { submitReport, getReport, getReportsByRestaurant, voteReport } = require('../controllers/reportController');

const upload = multer({ storage: multer.memoryStorage(), limits: { fileSize: 5 * 1024 * 1024 } });

router.post('/', authenticate, reportLimiter, upload.single('image'), submitReport);
router.get('/', authenticate, getReportsByRestaurant);
router.get('/:id', authenticate, getReport);
router.post('/:id/vote', authenticate, voteReport);

module.exports = router;
