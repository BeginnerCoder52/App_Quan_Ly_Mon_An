# 🍽️ Retail Food Management (App Cửa Hàng Quản Lý Đồ Ăn)

An intelligent Android application designed to manage food retail inventory, track expiration dates using the **FEFO (First Expired, First Out)** strategy, and provide real-time business insights.

> **Course:** SE114.Q11 - Introduction to Mobile Applications (Nhập môn Ứng dụng di động)
> **Instructor:** Nguyễn Tấn Toàn
> **Semester:** 1/2025-2026

## 👥 Team 2 Members

| Student ID | Name | Role |
| --- | --- | --- |
| **22520008** | **Cao Thiên An** | Team Leader |
| **22520418** | **Nguyễn Lê Thanh Hiển** | Member |
| **23521779** | **Lê Kim Việt** | Member |

---

## 📋 Table of Contents

* [Overview](https://www.google.com/search?q=%23-overview)
* [Key Features](https://www.google.com/search?q=%23-key-features)
* [Tech Stack](https://www.google.com/search?q=%23-tech-stack)
* [Project Architecture](https://www.google.com/search?q=%23-project-architecture)
* [Project Structure](https://www.google.com/search?q=%23-project-structure)
* [Setup & Installation](https://www.google.com/search?q=%23-setup--installation)
* [Screenshots](https://www.google.com/search?q=%23-screenshots)

## 🎯 Overview

**Retail Food Management** is a native Android application built with **Jetpack Compose**. It addresses the challenge of inventory management in small to medium food retail stores. Unlike traditional apps that only track quantity, this system introduces **Batch Management (Inventory Lots)** to track the expiration date of every import, ensuring the store minimizes waste by selling products that expire first.

## ✨ Key Features

### 🔐 Authentication & Profile

* **Login/Register:** Secure authentication using **Firebase Auth**.
* **Profile Management:** View user details and logout functionality.

### 📦 Product Management

* **Catalog:** View, add, edit, and delete products.
* **Smart Scanning:** Integrated **Barcode Scanner (ML Kit)** to auto-fill product SKU/Barcode during input.
* **Search & Filter:** Quickly find products by name or category.
* **Real-time Stock:** View total stock calculated dynamically from available batches.

### 🏭 Inventory & Warehousing (Core)

* **Import Goods:** Create import bills with detailed information (Supplier, Quantity, Price).
* **Automatic Batch Creation:** Every import creates a new `InventoryLot` with a specific **Expiry Date**.


* **Export Goods (Sales):** Create export bills.
* **FEFO Logic:** The system automatically deducts stock from the batch with the *nearest expiry date* (First Expired, First Out).
* **Stock Validation:** Prevents selling more than the available quantity.


* **History:** View detailed history of Import and Export bills (`StockScreen`).

### 📊 Dashboard & Insights

* **Revenue Chart:** Visual representation of sales over time.
* **Alerts:**
* **Expiring Soon:** Warns about batches nearing their expiration date.
* **Low Stock:** Alerts when product quantity falls below the minimum threshold.


* **Activity Log:** A notification center tracking system activities (`NotificationScreen`).

## 🛠️ Tech Stack

* **Language:** [Kotlin](https://kotlinlang.org/) (100%)
* **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
* **Backend / Database:**
* **Firebase Authentication:** User management.
* **Google Cloud Firestore:** NoSQL Real-time database.


* **Async Processing:** Kotlin Coroutines & Flow.
* **AI/ML:** [Google ML Kit](https://developers.google.com/ml-kit) (Vision API for Barcode Scanning).
* **Dependency Injection:** Manual DI / ViewModel Factory.
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/).

## 🏗️ Project Architecture

The project follows the **Clean Architecture** principles using **MVVM**:

1. **Data Layer (`data/`)**:
* **Model**: Data classes representing Firestore entities (`Product`, `InventoryLot`, `Bills`).
* **Repository**: Handles data operations (transactions, FEFO logic) and communicates with Firebase.


2. **UI Layer (`ui/`)**:
* **Screens**: Composable functions for UI.
* **ViewModel**: Manages UI state and communicates with the Repository.



## 📁 Project Structure

Based on the actual source code:

```text
com.example.app_quan_ly_do_an
│
├── MainActivity.kt                # Entry point, scaffolding, auth check
│
├── data
│   ├── model                      # Data Entities
│   │   ├── FoodItem.kt            # (Legacy/Demo model)
│   │   ├── InventoryModels.kt     # Core models: Product, InventoryLot, Bills
│   │   └── User.kt                # User profile model
│   │
│   └── repository                 # Data Access Layer
│       ├── AuthRepository.kt      # Firebase Auth logic
│       └── InventoryRepository.kt # Firestore logic, Transactions, FEFO algo
│
└── ui
    ├── components                 # Reusable UI Widgets
    │   ├── BarcodeScanner.kt      # CameraX + ML Kit implementation
    │   ├── BottomNavigationBar.kt # Main navigation menu
    │   ├── FoodItemCard.kt        # Product display card
    │   └── ... (Chips, Placeholders, Rows)
    │
    ├── navigation                 # Navigation Graph
    │   ├── AppNavigation.kt       # NavHost, Composable definitions
    │   └── NavigationItem.kt      # Route objects
    │
    ├── theme                      # Design System (Color, Type)
    │
    ├── viewmodel                  # State Management
    │   ├── auth                   # Login/Register ViewModels
    │   ├── home                   # Dashboard logic
    │   ├── product                # Add/Edit/List Product logic
    │   ├── import_bill            # Import logic (Create Batch)
    │   ├── export_bill            # Export logic (Deduct Stock)
    │   └── NotificationViewModel.kt
    │
    └── screens                    # UI Screens
        ├── auth                   # Login/Register
        ├── home                   # Dashboard
        ├── notification           # Activity Logs
        ├── profile                # User Profile
        ├── product                # Product List, Details, Batch List
        └── stock                  # Warehouse Management
            ├── import_bill        # Add/Edit Import Bill
            ├── export_bill        # Add/Edit Export Bill
            └── tabs               # History Tabs

```

## 🚀 Setup & Installation

To run this project locally, follow these steps:

### Prerequisites

* **Android Studio:** Hedgehog (2023.1.1) or newer.
* **JDK:** Version 17 or higher.
* **Android SDK:** Min SDK 24, Target SDK 34/35.

### Steps

1. **Clone the Repository**
```bash
git clone https://github.com/your-username/RetailFoodManagement.git

```


2. **Firebase Configuration (Crucial Step)**
* This project relies on Firebase. You must provide your own `google-services.json`.
* Go to [Firebase Console](https://console.firebase.google.com/).
* Create a project and enable **Authentication** (Email/Password) and **Firestore Database**.
* Download `google-services.json` and place it in the `app/` folder.


3. **Open in Android Studio**
* Open the project directory.
* Wait for Gradle to sync dependencies.


4. **Run the App**
* Connect a physical device (recommended for Camera/Scanner features) or use an Emulator.
* Click the **Run** button (▶️).

---

**© 2025 Group 2 - SE114.Q11 - UIT**
