const https = require('https');

// FSSAI public license registry
// Docs: https://foscos.fssai.gov.in  (public API for license lookup)
const lookupFSSAI = async (licenseNumber) => {
  try {
    // FSSAI FOSCOS public endpoint
    const url = `https://foscos.fssai.gov.in/api/searchLicenseOrRegistration?key=${licenseNumber}&search_type=fl`;

    const data = await _fetchJSON(url);

    if (!data || !data.list || data.list.length === 0) {
      return {
        licenseNumber,
        status: 'NOT_FOUND',
        valid: false,
        message: 'License not found in FSSAI registry',
      };
    }

    const entry = data.list[0];
    const expiryDate = new Date(entry.license_expiry_date);
    const isExpired = expiryDate < new Date();

    return {
      licenseNumber,
      businessName: entry.business_name,
      address: entry.premise_address,
      state: entry.state_name,
      licenseType: entry.license_type,
      expiryDate: entry.license_expiry_date,
      status: entry.status,
      valid: !isExpired && entry.status === 'Active',
      expired: isExpired,
      badge: isExpired ? 'EXPIRED' : entry.status === 'Active' ? 'VALID' : 'SUSPENDED',
    };
  } catch (err) {
    console.error('FSSAI lookup error:', err.message);
    return {
      licenseNumber,
      status: 'LOOKUP_FAILED',
      valid: null,
      message: 'Could not reach FSSAI registry. Try again later.',
    };
  }
};

const _fetchJSON = (url) => {
  return new Promise((resolve, reject) => {
    https.get(url, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try { resolve(JSON.parse(data)); }
        catch { reject(new Error('Invalid JSON from FSSAI')); }
      });
    }).on('error', reject);
  });
};

module.exports = { lookupFSSAI };
