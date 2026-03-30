# StudyLink 📚

A real-time Android app helping university students find study partners, groups, and lasting friendships.

## What It Does

StudyLink lets students post and join study sessions instantly. Instead of studying alone or scrambling to find help before an exam, students can browse open sessions by course, topic, or location, join in one tap, and connect with peers who share their classes, clubs, and interests.

The app goes beyond a simple study matcher. Users build rich profiles with their major, year, courses, hobbies, and club memberships, turning a study session into the start of a real academic friendship.

## Features

- Real-time study session posting and browsing
- Google Sign-In authentication
- Rich user profiles with courses, clubs, hobbies and bio
- Club-only session filtering
- AI-powered smart search using Gemini
- Personalized session recommendations based on your profile
- Study statistics and achievement badges
- Accessibility settings including font size and high contrast
- Animated splash screen and smooth screen transitions
- Settings page with account management

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Navigation | Jetpack Navigation with animated transitions |
| Database | Cloud Firestore (real-time NoSQL) |
| Authentication | Firebase Auth (Google Sign-In) |
| AI | Gemini API (smart search + recommendations) |
| Architecture | Repository Pattern (MVVM-inspired) |

## Architecture
```
ui/           Screen composables (Login, Home, Sessions, Profile, Settings)
data/model/   Data classes (Session, User)
data/repository/  Firebase + Gemini logic layer
navigation/   NavGraph with animated transitions
ui/theme/     Warm Sunrise color palette, typography
```

The repository pattern keeps the UI layer completely separate from Firebase. Screens never call Firestore directly. They always go through SessionRepository or UserRepository, which makes the code easy to test, maintain, and extend.

## Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android SDK 24+
- A Firebase project
- A Google AI Studio API key

### Setup

1. Clone the repo
```bash
git clone https://github.com/Geomar2016/StudyLink.git
cd StudyLink
```

2. Create a Firebase project at firebase.google.com and add an Android app with package name `com.example.studylink`

3. Download `google-services.json` from Firebase and place it in the `app/` folder

4. Enable Google Sign-In in Firebase Authentication

5. Create a Firestore database in test mode

6. Get a Gemini API key from aistudio.google.com

7. Add your keys to `local.properties`
```
WEB_CLIENT_ID=your_web_client_id_here
GEMINI_API_KEY=your_gemini_api_key_here
```

8. Build and run in Android Studio

### Note on Secret Keys

`local.properties` and `google-services.json` are listed in `.gitignore` and will never be committed to this repo. You must supply your own Firebase project and API keys to run the app.

## Screenshots

Coming soon

## Demo

Coming soon

## Why We Built This

Most study apps treat students as isolated individuals. But university life is community driven. You are in clubs, associations, and cohorts. StudyLink reflects that reality by letting you filter sessions by club membership, match with peers who share your hobbies, and build a profile that shows who you actually are beyond just your GPA.

The social layer is the differentiator. You do not just find a study partner. You find a friend.

## License

MIT License. Open source and free to use, modify, and distribute.

## Built With

Made with dedication for students everywhere.
