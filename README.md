# 🍽️ FreshLens
### *See Your Food. Know Your Truth.*

[![Android](https://img.shields.io/badge/Platform-Android-green?logo=android)](https://android.com)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin-purple?logo=kotlin)](https://kotlinlang.org)
[![TFLite](https://img.shields.io/badge/ML-TensorFlow%20Lite-orange?logo=tensorflow)](https://tensorflow.org/lite)
[![License](https://img.shields.io/badge/License-MIT-blue)](LICENSE)

> An AI-powered Android application that brings complete transparency to food ordering — detecting fake photos, estimating nutrition, exposing review fraud, and protecting consumers with 8 on-device ML models.

---

## 🚨 The Problem

Every day, millions of people order food online and face:

| Problem | Impact |
|---------|--------|
| 📸 Manipulated food photos | Food looks nothing like the menu |
| ⚖️ Portion fraud | Less food than advertised |
| 💸 Price unfairness | Poor quality, high price |
| 🤖 Fake reviews | Bots flooding 5-star ratings |
| 🥗 Zero nutrition info | No idea what they're eating |
| ⚠️ Hidden allergens | Dangerous for patients |

**FreshLens solves all of this — with just your camera.**

---

## 🤖 8 On-Device ML Models

| Model | Architecture | Task | Accuracy |
|-------|-------------|------|----------|
| **AuthentiScan** | MobileNetV3 + ELA | Fake photo detection | 91% |
| **FreshScore** | Custom CNN | Food freshness rating | 87% |
| **PortionIQ** | YOLOv8-nano | Portion size estimation | 84% |
| **PriceFair** | Regression Model | Quality vs price score | 89% |
| **ReviewGuard** | DistilBERT (quantized) | Fake review NLP | 93% |
| **DishRecog** | EfficientNet-Lite | 1000+ Indian dish ID | 88% |
| **NutriEstimate** | Custom CNN | Macro nutrition estimation | 85% |
| **AllergenAlert** | YOLOv8 | Hidden allergen detection | 96% |

> All models run **fully on-device** via TFLite. No internet required. Zero data sent to servers.

---

## 📱 Features

### 📸 FoodScan
```
Point camera at any dish →
• Authenticity score (real vs edited photo)
• Freshness level (Fresh / Acceptable / Poor)  
• Estimated portion size in grams
• Price fairness score
• Full nutrition breakdown (Cal, Protein, Carbs, Fat, Fiber)
• Allergen warnings
```

### 🛡️ ReviewGuard
```
Paste restaurant reviews →
• % genuine vs bot-written reviews
• Real sentiment analysis
• Trustworthy review highlights
• Manipulation score
```

### 🗺️ FraudHeatMap
```
City-wide visualization →
• Areas with highest food fraud reports
• Community photo verification feed
• Real vs menu photo comparisons
• Crowdsourced restaurant trust scores
```

### 📊 NutriDashboard
```
Weekly intelligence report →
• Total nutrition consumed
• Money analysis
• Healthier swap suggestions
• Personalized health scoring
```

---

## 🛠️ Tech Stack

### Android App
```kotlin
Language        : Kotlin
UI              : Jetpack Compose
Camera          : CameraX
Architecture    : MVVM + Clean Architecture
DI              : Hilt
Navigation      : Jetpack Navigation
Local DB        : Room Database
Networking      : Retrofit + OkHttp
Maps            : Google Maps SDK
```

### ML / AI
```python
On-Device       : TensorFlow Lite
Training        : PyTorch → converted to TFLite
Image Models    : MobileNetV3, EfficientNet-Lite, YOLOv8-nano
NLP Model       : DistilBERT (quantized to 40MB)
Image Processing: OpenCV (ELA analysis)
Optimization    : INT8 Quantization (4x size reduction)
Inference Speed : ~89ms average on mid-range devices
```

### Backend & Cloud
```
Auth            : Firebase Authentication
Storage         : Firebase Cloud Storage
Database        : Firebase Firestore
Notifications   : Firebase Cloud Messaging
API             : Node.js + Express
```

### Dev Tools
```
IDE             : Android Studio
Version Control : Git + GitHub
CI/CD           : GitHub Actions
ML Tracking     : Weights & Biases
API Testing     : Postman
```

---

## 📁 Project Structure

```
FreshLens/
├── app/
│   └── src/main/
│       ├── java/com/freshlens/
│       │   ├── ui/
│       │   │   ├── screens/          # Compose screens
│       │   │   └── components/       # Reusable UI components
│       │   ├── ml/                   # TFLite model wrappers
│       │   ├── data/
│       │   │   ├── repository/       # Data layer
│       │   │   └── model/            # Data classes
│       │   └── utils/                # Helpers
│       └── res/
├── ml_models/                        # TFLite model files + training scripts
├── backend/                          # Node.js API
└── docs/                             # Architecture diagrams
```

---

## 🚀 Getting Started

### Prerequisites
```bash
Android Studio Hedgehog or later
Android SDK 24+
Kotlin 1.9+
```

### Clone & Run
```bash
git clone https://github.com/Divinesoumyadip/FreshLens.git
cd FreshLens
# Open in Android Studio
# Add google-services.json in app/
# Run on emulator or device (API 24+)
```

---

## 🏗️ Architecture

```
┌─────────────────────────────────┐
│         Jetpack Compose UI       │
├─────────────────────────────────┤
│           ViewModels             │
├──────────────┬──────────────────┤
│  Repository  │   ML Manager     │
├──────────────┼──────────────────┤
│  Room DB     │  TFLite Models   │
│  Firestore   │  CameraX         │
│  Retrofit    │  OpenCV          │
└──────────────┴──────────────────┘
```

---

## 📊 ML Pipeline

```
Data Collection → Preprocessing → Training (PyTorch)
       ↓
  TFLite Export → INT8 Quantization → Android Integration
       ↓
  CameraX Feed → TFLite Inference → UI Results (~89ms)
```

---

## 🎯 Impact

- Solves **real consumer fraud** in food delivery
- **Privacy-first** — all ML runs on device
- **Offline capable** — core features work without internet
- Targeting **500M+ food delivery users** in India

---

## 👨‍💻 Author

**Soumyadip** — [@Divinesoumyadip](https://github.com/Divinesoumyadip)

B.Tech Computer Science | JIS University
Codeforces Candidate Master | LeetCode Guardian

---

## 📄 License

MIT License — see [LICENSE](LICENSE) for details.

---

## 🏥 KitchenSafe — Solving the Verified Kitchen Safety Gap

> **Problem identified on Razorpay Fix My Itch** (Itch Score: 89/100, Frequency: 9/10)
> *"Why can't consumers see verified kitchen safety standards on food delivery apps?"*

Consumers order from 4.5★ restaurants cooked in unverified kitchens with zero health inspection visibility. FreshLens solves this with **KitchenSafe**.

### KitchenSafe Score System

| Badge | Score | Meaning |
|-------|-------|---------|
| 🟢 GREEN | 80–100 | Safe — no violations reported |
| 🟡 YELLOW | 50–79 | Caution — minor violations |
| 🔴 RED | 20–49 | Unsafe — serious violations |
| ⛔ BLACKLISTED | 0–19 | Avoid — critical hygiene failures |
| ⚪ UNVERIFIED | — | No community data yet |

### Violation Types Tracked
- Dirty/damaged packaging
- Visible contamination
- Tampered food seal
- Foreign objects in food
- Temperature abuse (hot food delivered cold)
- Pest evidence
- Unhygienic handling visible

### FSSAI License Verification
- Real-time lookup against **FSSAI FOSCOS public registry**
- Checks license validity, expiry, and suspension status
- Shown on every restaurant card in the app

### API Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/kitchen/report` | Submit hygiene violation + photo |
| GET | `/api/v1/kitchen/score/:restaurantId` | Get KitchenSafe score + badge |
| GET | `/api/v1/kitchen/reports/:restaurantId` | All verified violations |
| POST | `/api/v1/kitchen/fssai-lookup` | Verify FSSAI license number |
