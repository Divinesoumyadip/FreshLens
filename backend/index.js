const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const rateLimit = require('express-rate-limit');
require('dotenv').config();

const authRoutes = require('./src/routes/auth');
const reportRoutes = require('./src/routes/reports');
const heatmapRoutes = require('./src/routes/heatmap');
const restaurantRoutes = require('./src/routes/restaurants');
const userRoutes = require('./src/routes/users');
const kitchenSafeRoutes = require('./src/routes/kitchenSafe');

const app = express();

// Security
app.use(helmet());
app.use(cors({ origin: process.env.ALLOWED_ORIGINS || '*' }));
app.use(express.json({ limit: '10mb' }));

// Global rate limit
const limiter = rateLimit({
  windowMs: 15 * 60 * 1000, // 15 min
  max: 100,
  message: { error: 'Too many requests, slow down.' }
});
app.use(limiter);

// Routes
app.use('/api/v1/auth', authRoutes);
app.use('/api/v1/reports', reportRoutes);
app.use('/api/v1/heatmap', heatmapRoutes);
app.use('/api/v1/restaurants', restaurantRoutes);
app.use('/api/v1/users', userRoutes);
app.use('/api/v1/kitchen', kitchenSafeRoutes);

// Health check
app.get('/health', (req, res) => res.json({ status: 'ok', service: 'FreshLens API' }));

// 404
app.use((req, res) => res.status(404).json({ error: 'Route not found' }));

// Error handler
app.use((err, req, res, next) => {
  console.error(err.stack);
  res.status(500).json({ error: 'Internal server error' });
});

const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`FreshLens API running on port ${PORT}`));

module.exports = app;
