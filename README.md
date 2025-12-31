## VOYARA – Mobile App Platform

Voyara Mobile App Platform is a cross-platform travel planning application built using **Kotlin Multiplatform (KMP)**.  
It allows users to plan trips, build itineraries, explore destinations, manage budgets, and interact with a travel community through a modern mobile experience.

This repository contains the **mobile application implementation** for both **Android** and **iOS**, along with reference system designs.


## Key Features

- Secure user authentication
- Trip creation and itinerary planning
- Destination & activity exploration
- Budget tracking
- Community interaction
- Calendar-based trip view
- Cross-platform support (Android & iOS)


##  Project Structure

composeApp/    → Shared UI and business logic (KMP + Compose)  
iosApp/        → iOS-specific module  
gradle/        → Gradle configuration  
build.gradle.kts, settings.gradle.kts  
gradlew / gradlew.bat  


## Tech Stack

### Mobile
- Kotlin Multiplatform (KMP)
- Kotlin
- Java

### UI
- Jetpack Compose
- Compose Multiplatform

### Build System
- Gradle (Kotlin DSL)

### Platforms
- Android
- iOS


## Application Flow (Reference Design)

![Voyara App Wireframe](assets/voyara_wireframe_flow.png)

This wireframe illustrates the planned user journey of the Voyara mobile app,
from authentication to trip planning, community interaction, and calendar view.



## 🔄 System Data Flow (DFD)

![Voyara DFD](assets/voyara_dfd.png)

The Data Flow Diagram shows how the mobile application interacts with backend
services for authentication, trip management, search, community features,
and analytics.


## Data Layer Overview

![Voyara Database Schema](assets/voyara_database_schema.png)

This schema represents how users, trips, destinations, and itineraries are
structured and stored to support the mobile application.


## APK Build

A debug APK is available for testing purposes:

composeApp-debug.apk

This APK can be installed directly on an Android device for demo and testing.


##  Run the Project

### Android
1. Open the project in Android Studio
2. Sync Gradle
3. Run the `composeApp` configuration

### iOS
1. Open `iosApp` in Xcode
2. Build and run on an iOS simulator or device


## APK Download

You can download and install the latest debug APK for testing the Voyara mobile app:

**[Download Voyara APK](assets/composeApp-debug.apk)**

> Note: This is a debug build intended for testing and demonstration purposes.


##  Summary

Voyara Mobile App Platform extends the core Voyara travel planning system to
mobile devices using a scalable and maintainable cross-platform architecture.
Kotlin Multiplatform enables shared logic while preserving a native user
experience on Android and iOS.
