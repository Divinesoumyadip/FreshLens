# FreshLens — Setup Guide

## 1. Firebase Project Setup

1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create project: `freshlens`
3. Enable these services:
   - Authentication → Email/Password + Google
   - Firestore Database → Start in production mode
   - Storage → Default bucket
   - Cloud Messaging → Enabled by default

## 2. Android Setup

1. In Firebase Console → Project Settings → Add Android app
2. Package name: `com.freshlens`
3. Download `google-services.json`
4. Place it in `app/google-services.json`
5. Open in Android Studio → Sync → Run

## 3. Backend Setup

1. In Firebase Console → Project Settings → Service Accounts
2. Generate new private key → download JSON
3. Copy values to `backend/.env`:
```
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_CLIENT_EMAIL=firebase-adminsdk-xxx@your-project.iam.gserviceaccount.com
FIREBASE_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n"
FIREBASE_STORAGE_BUCKET=your-project.appspot.com
```

## 4. Deploy Backend to Render

1. Push repo to GitHub
2. Go to [render.com](https://render.com) → New Web Service
3. Connect GitHub repo → Render auto-reads `render.yaml`
4. Add env vars from step 3 in Render dashboard
5. Deploy → note your live URL

## 5. Train DishRecog Model

1. Open `ml_models/train_dishrecog.ipynb` in Google Colab
2. Runtime → T4 GPU
3. Run All (~45 mins)
4. Download `dishrecog.tflite` and `food101_labels.txt`
5. Place both in `app/src/main/assets/`
6. `git add . && git commit -m "feat: add trained DishRecog model" && git push`

## 6. Update API Base URL in Android

In `app/src/main/java/com/freshlens/data/network/ApiClient.kt`:
```kotlin
const val BASE_URL = "https://your-app.onrender.com/api/v1/"
```
