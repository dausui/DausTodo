# DausNotes 📝⏱️

**DausNotes** adalah aplikasi catatan Android yang terintegrasi dengan timer Pomodoro untuk membantu Anda tetap fokus dan produktif. Dibangun dengan Kotlin menggunakan arsitektur MVVM dan komponen Android modern.

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-green.svg" alt="Platform">
  <img src="https://img.shields.io/badge/Language-Kotlin-blue.svg" alt="Language">
  <img src="https://img.shields.io/badge/Min%20SDK-24-orange.svg" alt="Min SDK">
  <img src="https://img.shields.io/badge/Target%20SDK-34-red.svg" alt="Target SDK">
  <img src="https://github.com/username/dausnotes/workflows/Android%20CI/badge.svg" alt="CI Status">
</div>

## 🌟 Fitur Utama

### 📖 Manajemen Catatan
- ✅ **CRUD Catatan**: Buat, baca, update, dan hapus catatan
- 🔍 **Pencarian**: Cari catatan berdasarkan judul atau konten
- 🏷️ **Kategori**: Kelompokkan catatan dengan kategori (Work, Personal, Study, Ideas, General)
- ⭐ **Favorit**: Tandai catatan penting sebagai favorit
- 📱 **UI Modern**: Material Design 3 dengan dark/light theme

### ⏱️ Timer Pomodoro
- 🍅 **Timer Klasik**: Sesi kerja 25 menit, istirahat pendek 5 menit, istirahat panjang 15 menit
- 🔗 **Link ke Catatan**: Hubungkan sesi timer dengan catatan tertentu
- 📊 **Statistik**: Lacak sesi harian, total fokus time, dan streak
- 🔔 **Notifikasi**: Background notifications dengan kontrol timer
- ⚙️ **Kustomisasi**: Atur durasi timer sesuai preferensi

### 📊 Dashboard
- 📈 **Ringkasan**: Overview total catatan, favorit, dan statistik pomodoro
- 📋 **Catatan Terbaru**: Akses cepat ke catatan yang baru dibuat/diupdate
- 🔥 **Streak Counter**: Lacak konsistensi penggunaan harian
- 📱 **Quick Actions**: Akses fitur utama dengan mudah

## 🏗️ Arsitektur & Teknologi

### 🎯 Arsitektur MVVM
```
📁 app/src/main/java/com/dausnotes/app/
├── 📁 data/
│   ├── 📁 database/          # Room Database & DAOs
│   ├── 📁 repository/        # Repository Pattern
│   └── 📁 model/            # Data Models
├── 📁 ui/
│   ├── 📁 notes/            # Notes Feature
│   ├── 📁 pomodoro/         # Timer Feature  
│   └── 📁 dashboard/        # Dashboard Feature
├── 📁 utils/                # Utility Classes
├── 📁 service/             # Background Services
└── MainActivity.kt         # Main Entry Point
```

### 🛠️ Tech Stack

**Core Android:**
- **Kotlin** - Bahasa pemrograman utama
- **Android Architecture Components** - ViewModel, LiveData, Navigation
- **Material Design 3** - UI components dan theming
- **View Binding** - Type-safe view binding

**Database & Storage:**
- **Room Database** - Local database dengan SQLite
- **Repository Pattern** - Abstraksi data layer
- **SharedPreferences** - User preferences

**Background Processing:**
- **Foreground Services** - Background timer
- **Notifications** - Timer alerts & progress
- **Work Manager** - Scheduled tasks

**Testing:**
- **JUnit 4** - Unit testing framework
- **Mockito** - Mocking framework
- **Espresso** - UI testing
- **Room Testing** - Database testing

## 🚀 Setup & Installation

### Prasyarat
- **Android Studio** 2023.1.1 (Hedgehog) atau lebih baru
- **JDK 17** atau lebih tinggi
- **Android SDK** dengan API level 24-34
- **Git** untuk version control

### 🔧 Clone & Build

```bash
# Clone repository
git clone https://github.com/username/dausnotes.git
cd dausnotes

# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug
```

### 📱 Build Requirements

```kotlin
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.dausnotes.app"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
}
```

## 🤖 CI/CD dengan GitHub Actions

### 🔄 Automated Workflows

**Pull Request Checks:**
- ✅ Lint analysis
- 🧪 Unit tests
- 🏗️ Build verification
- 📱 Instrumented tests (on main branch)

**Release Process:**
- 🏷️ Tag-based releases (`v*`)
- 🔐 Signed APK generation
- 📦 Automatic GitHub releases
- 📋 Release notes generation

### 🔑 Setup Secrets

Untuk release builds, tambahkan secrets berikut di GitHub repository:

```
KEYSTORE_BASE64      # Base64 encoded keystore file
KEYSTORE_PASSWORD    # Keystore password
KEY_ALIAS           # Signing key alias
KEY_PASSWORD        # Key password
```

### 📊 Build Status

```yaml
# Contoh workflow trigger
on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]
  push:
    tags: [ 'v*' ]
```

## 🗃️ Database Schema

### 📝 Notes Table
```sql
CREATE TABLE notes (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    category TEXT DEFAULT 'General',
    isFavorite INTEGER DEFAULT 0,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);
```

### ⏱️ Pomodoro Sessions Table
```sql
CREATE TABLE pomodoro_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    noteId INTEGER,
    duration INTEGER DEFAULT 25,
    isCompleted INTEGER DEFAULT 0,
    sessionType TEXT DEFAULT 'WORK',
    startTime INTEGER NOT NULL,
    endTime INTEGER,
    FOREIGN KEY(noteId) REFERENCES notes(id) ON DELETE SET NULL
);
```

## 🧪 Testing Strategy

### Unit Tests
- **Repository Tests** - Data layer logic
- **ViewModel Tests** - Business logic
- **Utility Tests** - Helper functions

### Integration Tests
- **Database Tests** - Room operations
- **Repository Integration** - End-to-end data flow

### UI Tests
- **Fragment Tests** - UI components
- **Navigation Tests** - Screen transitions
- **User Flow Tests** - Complete scenarios

```bash
# Run all tests
./gradlew test connectedAndroidTest

# Run with coverage
./gradlew testDebugUnitTestCoverageVerification
```

## 📁 Project Structure

```
DausNotes/
├── 📄 README.md
├── 📄 build.gradle.kts
├── 📄 settings.gradle.kts
├── 📁 .github/workflows/       # CI/CD workflows
├── 📁 app/
│   ├── 📄 build.gradle.kts
│   ├── 📄 proguard-rules.pro
│   ├── 📁 src/
│   │   ├── 📁 main/
│   │   │   ├── 📄 AndroidManifest.xml
│   │   │   ├── 📁 java/com/dausnotes/app/
│   │   │   └── 📁 res/
│   │   ├── 📁 test/            # Unit tests
│   │   └── 📁 androidTest/     # Instrumented tests
│   └── 📁 schemas/             # Room database schemas
└── 📁 gradle/
    └── 📁 wrapper/
```

## 🎨 Design System

### Material Design 3
- **Primary Color**: Blue (#2563eb)
- **Secondary Color**: Gray variations
- **Success Color**: Green (#10b981) 
- **Warning Color**: Amber (#f59e0b)
- **Error Color**: Red (#dc2626)

### Typography
- **Roboto** - Primary font family
- **Roboto Mono** - Timer display
- Responsive text scaling
- Accessibility compliant

### Dark/Light Theme
- System theme detection
- Manual theme switching
- Consistent color adaptation

## 🤝 Contributing

Kami menyambut kontribusi dari developer! Berikut cara berkontribusi:

### 🔀 Workflow
1. **Fork** repository ini
2. **Create feature branch** (`git checkout -b feature/AmazingFeature`)
3. **Commit changes** (`git commit -m 'Add some AmazingFeature'`)
4. **Push to branch** (`git push origin feature/AmazingFeature`)
5. **Open Pull Request**

### 📝 Guidelines
- Ikuti konvensi kode Kotlin yang ada
- Tambahkan unit tests untuk fitur baru
- Update dokumentasi jika diperlukan
- Pastikan CI checks pass

### 🐛 Bug Reports
Gunakan GitHub Issues dengan template:
- **Bug description** 
- **Steps to reproduce**
- **Expected vs actual behavior**
- **Device info** (Android version, device model)
- **App version**

## 📄 License

Proyek ini dilisensikan di bawah MIT License - lihat file [LICENSE](LICENSE) untuk detail.

```
MIT License

Copyright (c) 2024 DausNotes

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

## 📞 Contact & Support

- **Email**: support@dausnotes.com
- **GitHub Issues**: [Report Bug](https://github.com/username/dausnotes/issues)
- **Discussions**: [GitHub Discussions](https://github.com/username/dausnotes/discussions)

## 🗺️ Roadmap

### Version 1.1
- [ ] 📤 Export/Import catatan
- [ ] 🎨 Custom themes
- [ ] 🔄 Sync cloud storage
- [ ] 📸 Image attachments

### Version 1.2  
- [ ] 🔐 Biometric security
- [ ] 🏷️ Tags system
- [ ] 📈 Advanced analytics
- [ ] 🤝 Collaboration features

### Version 2.0
- [ ] 🌐 Web companion app
- [ ] 🔌 Plugin system
- [ ] 🎯 Smart suggestions
- [ ] 📊 Productivity insights

---

<div align="center">
  <p>Dibuat dengan ❤️ menggunakan Kotlin & Android</p>
  <p>© 2024 DausNotes. All rights reserved.</p>
</div>
