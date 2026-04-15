const rateLimit = require('express-rate-limit');

const reportLimiter = rateLimit({
  windowMs: 60 * 60 * 1000, // 1 hour
  max: 20,
  message: { error: 'Too many reports submitted. Try after an hour.' }
});

const uploadLimiter = rateLimit({
  windowMs: 60 * 60 * 1000,
  max: 10,
  message: { error: 'Upload limit reached.' }
});

module.exports = { reportLimiter, uploadLimiter };
