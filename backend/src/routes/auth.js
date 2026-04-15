const router = require('express').Router();
const { authenticate } = require('../middleware/authMiddleware');
const { registerUser, updateFcmToken } = require('../controllers/authController');

router.post('/register', authenticate, registerUser);
router.patch('/fcm-token', authenticate, updateFcmToken);

module.exports = router;
