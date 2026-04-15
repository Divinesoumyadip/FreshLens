module.exports = {
  COLLECTIONS: {
    USERS: 'users',
    REPORTS: 'fraud_reports',
    RESTAURANTS: 'restaurants',
    HEATMAP: 'heatmap_points',
    VOTES: 'report_votes',
  },
  REPORT_STATUS: {
    PENDING: 'pending',
    VERIFIED: 'verified',
    REJECTED: 'rejected',
  },
  FRAUD_TYPES: {
    FAKE_PHOTO: 'fake_photo',
    PORTION_FRAUD: 'portion_fraud',
    FAKE_REVIEW: 'fake_review',
    ALLERGEN_HIDDEN: 'allergen_hidden',
    PRICE_FRAUD: 'price_fraud',
  },
};

// KitchenSafe module
module.exports.KITCHEN_COLLECTIONS = {
  HYGIENE_REPORTS: 'hygiene_reports',
  KITCHEN_SCORES: 'kitchen_scores',
};

module.exports.VIOLATION_TYPES = {
  DIRTY_PACKAGING: 'dirty_packaging',
  CONTAMINATION_VISIBLE: 'contamination_visible',
  TAMPERED_SEAL: 'tampered_seal',
  FOREIGN_OBJECT: 'foreign_object',
  TEMPERATURE_ABUSE: 'temperature_abuse',
  PEST_EVIDENCE: 'pest_evidence',
  UNHYGIENIC_HANDLING: 'unhygienic_handling',
};

module.exports.KITCHEN_BADGE = {
  GREEN: 'GREEN',       // 80-100: Safe
  YELLOW: 'YELLOW',     // 50-79: Caution
  RED: 'RED',           // 20-49: Unsafe
  BLACKLISTED: 'BLACKLISTED', // 0-19: Avoid
  UNVERIFIED: 'UNVERIFIED',   // No reports yet
};
