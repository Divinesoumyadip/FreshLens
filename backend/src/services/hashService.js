const crypto = require('crypto');

// SHA-256 hash of image buffer for tamper-proof fingerprinting
const hashImage = (buffer) => {
  return crypto.createHash('sha256').update(buffer).digest('hex');
};

const hashString = (str) => {
  return crypto.createHash('sha256').update(str).digest('hex');
};

// Duplicate report check
const generateReportFingerprint = ({ restaurantId, fraudType, userId }) => {
  return hashString(`${restaurantId}:${fraudType}:${userId}`);
};

module.exports = { hashImage, hashString, generateReportFingerprint };
