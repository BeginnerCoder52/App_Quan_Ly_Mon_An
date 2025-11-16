# 🍽️ App Quản Lý Đồ Ăn Tại Nhà

Ứng dụng Android giúp quản lý thực phẩm tại nhà, theo dõi hạn sử dụng và nhắc nhở người dùng về thực phẩm sắp hết hạn.

## 📋 Mục Lục
- [Tổng Quan](#tổng-quan)
- [Tính Năng](#tính-năng)
- [Công Nghệ Sử Dụng](#công-nghệ-sử-dụng)
- [Cấu Trúc Dự Án](#cấu-trúc-dự-án)
- [Hướng Dẫn Cài Đặt](#hướng-dẫn-cài-đặt)
- [Sử Dụng](#sử-dụng)

## 🎯 Tổng Quan

App Quản Lý Đồ Ăn là ứng dụng mobile giúp người dùng:
- Quản lý danh sách thực phẩm trong nhà
- Theo dõi hạn sử dụng
- Nhận thông báo khi thực phẩm sắp hết hạn
- Quét mã vạch để thêm sản phẩm nhanh chóng

## ✨ Tính Năng

### Tính năng chính
- **Đăng nhập/Đăng ký**: Xác thực người dùng qua Firebase Authentication
- **Quản lý sản phẩm**: Thêm, sửa, xóa, tìm kiếm thực phẩm
- **Thông báo hết hạn**: Nhắc nhở thực phẩm sắp hết hạn
- **Lưu trữ dữ liệu**: Dữ liệu được lưu trữ bằng SQLite
- **Quét mã vạch**: Thêm sản phẩm nhanh chóng bằng camera

### Tính năng hiện tại (Phase 1)
- ✅ Giao diện người dùng (UI/UX)
- ✅ Navigation giữa các màn hình
- ✅ Bottom Navigation Bar
- ✅ Màn hình Home với danh sách thực phẩm
- ⏳ Quét mã vạch (Coming soon)
- ⏳ Tích hợp SQLite (Coming soon)
- ⏳ Firebase Authentication (Coming soon)

## 🛠️ Công Nghệ Sử Dụng

- **Ngôn ngữ**: Kotlin
- **Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Navigation Compose
- **Database**: SQLite (Planned)
- **Authentication**: Firebase Auth (Planned)
- **Build System**: Gradle (Kotlin DSL)
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## 📁 Cấu Trúc Dự Án

```
App_Quan_Ly_Mon_An/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/app_quan_ly_do_an/
│   │   │   │   │
│   │   │   │   ├── data/                    # Lớp dữ liệu
│   │   │   │   │   └── model/
│   │   │   │   │       └── FoodItem.kt      # Model dữ liệu thực phẩm
│   │   │   │   │
│   │   │   │   ├── ui/                      # Giao diện người dùng
│   │   │   │   │   │
│   │   │   │   │   ├── components/          # Các component tái sử dụng
│   │   │   │   │   │   ├── BottomNavigationBar.kt  # Thanh điều hướng dưới
│   │   │   │   │   │   └── FoodItemCard.kt         # Card hiển thị thực phẩm
│   │   │   │   │   │
│   │   │   │   │   ├── navigation/          # Điều hướng ứng dụng
│   │   │   │   │   │   ├── NavigationItem.kt      # Định nghĩa các route
│   │   │   │   │   │   └── AppNavigation.kt        # NavHost chính
│   │   │   │   │   │
│   │   │   │   │   ├── screens/             # Các màn hình
│   │   │   │   │   │   ├── home/
│   │   │   │   │   │   │   └── HomeScreen.kt      # Màn hình chính
│   │   │   │   │   │   ├── history/
│   │   │   │   │   │   │   └── HistoryScreen.kt   # Lịch sử
│   │   │   │   │   │   ├── scanner/
│   │   │   │   │   │   │   └── ScannerScreen.kt   # Quét mã vạch
│   │   │   │   │   │   ├── notification/
│   │   │   │   │   │   │   └── NotificationScreen.kt # Thông báo
│   │   │   │   │   │   └── profile/
│   │   │   │   │   │       └── ProfileScreen.kt    # Tài khoản
│   │   │   │   │   │
│   │   │   │   │   └── theme/               # Theme ứng dụng
│   │   │   │   │       ├── Color.kt
│   │   │   │   │       ├── Theme.kt
│   │   │   │   │       └── Type.kt
│   │   │   │   │
│   │   │   │   └── MainActivity.kt          # Activity chính
│   │   │   │
│   │   │   └── AndroidManifest.xml
│   │   │
│   │   └── build.gradle.kts                 # Build config
│   │
│   └── build.gradle.kts (Module: app)
│
├── gradle/
│   └── libs.versions.toml                   # Version catalog
│
├── build.gradle.kts (Project)
└── settings.gradle.kts
```

## 📦 Chi Tiết Các Thư Mục

### 1. `data/model/`
Chứa các data class định nghĩa cấu trúc dữ liệu

**FoodItem.kt**
- Model chính cho thực phẩm
- Thuộc tính: id, name, expiryDate, quantity, category, imageUrl, points

### 2. `ui/components/`
Các Composable component có thể tái sử dụng

**BottomNavigationBar.kt**
- Bottom Navigation với 5 tab
- Xử lý navigation giữa các màn hình
- Highlight tab đang active

**FoodItemCard.kt**
- Card hiển thị thông tin một thực phẩm
- Hiển thị: tên, hạn sử dụng, số lượng
- Design giống app Quà Tặng Vip của Bách Hóa Xanh

### 3. `ui/navigation/`
Quản lý điều hướng trong app

**NavigationItem.kt**
- Sealed class định nghĩa các route
- 5 màn hình: Home, History, Scanner, Notification, Profile
- List navigationItems cho Bottom Navigation

**AppNavigation.kt**
- NavHost chính của ứng dụng
- Kết nối các route với màn hình tương ứng
- Xử lý navigation stack

### 4. `ui/screens/`
Các màn hình chính của ứng dụng

**home/HomeScreen.kt**
- Màn hình chính hiển thị danh sách thực phẩm
- Header với tổng điểm và ngày
- LazyColumn hiển thị danh sách FoodItemCard
- Background màu vàng giống Bách Hóa Xanh

**history/HistoryScreen.kt**
- Màn hình lịch sử giao dịch
- Hiển thị các thao tác đã thực hiện
- (Placeholder - sẽ phát triển sau)

**scanner/ScannerScreen.kt**
- Màn hình quét mã vạch
- Sử dụng camera để quét barcode
- (Placeholder - sẽ phát triển sau)

**notification/NotificationScreen.kt**
- Màn hình thông báo
- Hiển thị các thông báo về thực phẩm sắp hết hạn
- (Placeholder - sẽ phát triển sau)

**profile/ProfileScreen.kt**
- Màn hình tài khoản người dùng
- Thông tin cá nhân, cài đặt
- (Placeholder - sẽ phát triển sau)

### 5. `ui/theme/`
Định nghĩa theme và style cho ứng dụng

**Color.kt**
- Định nghĩa color palette
- Primary color: Vàng (#FFC107) - giống Bách Hóa Xanh

**Theme.kt**
- Material3 theme configuration
- Light/Dark theme support

**Type.kt**
- Typography definitions
- Font sizes và styles

### 6. `MainActivity.kt`
- Activity chính của ứng dụng
- Setup Scaffold với Bottom Navigation
- Initialize NavController
- Apply theme

## 🚀 Hướng Dẫn Cài Đặt

### Yêu cầu
- Android Studio Hedgehog (2023.1.1) trở lên
- JDK 11 trở lên
- Android SDK 24+
- Gradle 8.7+

### Các bước cài đặt

1. **Clone repository**
```bash
git clone https://github.com/your-username/app-quan-ly-do-an.git
cd app-quan-ly-do-an
```

2. **Mở project trong Android Studio**
- File → Open → Chọn thư mục project

3. **Sync Gradle**
- Android Studio sẽ tự động sync
- Hoặc nhấn "Sync Now" nếu có banner

4. **Build project**
```bash
./gradlew build
```

5. **Run app**
- Nhấn nút Run (▶️) trong Android Studio
- Hoặc sử dụng command:
```bash
./gradlew installDebug
```

## 💻 Sử Dụng

### Chạy ứng dụng
1. Kết nối thiết bị Android hoặc khởi động emulator
2. Nhấn Run trong Android Studio
3. App sẽ được cài đặt và tự động mở

### Navigation
- **Trang chủ**: Xem danh sách thực phẩm
- **Lịch sử**: Xem lịch sử thao tác
- **Quét mã**: Quét mã vạch sản phẩm
- **Thông báo**: Xem thông báo
- **Tài khoản**: Quản lý tài khoản

### Thêm thực phẩm (Coming soon)
1. Nhấn nút "+" hoặc "Quét mã"
2. Nhập thông tin hoặc quét mã vạch
3. Lưu thông tin

## 🔧 Cấu Hình

### Build Variants
- **Debug**: Build cho development
- **Release**: Build cho production

### Dependencies chính
```kotlin
// Compose
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.activity:activity-compose")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.5")

// Icons
implementation("androidx.compose.material:material-icons-extended")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose")
```

## 📝 TODO

### Phase 2: Backend Integration
- [ ] Tích hợp SQLite database
- [ ] CRUD operations cho thực phẩm
- [ ] Search và filter functionality

### Phase 3: Advanced Features
- [ ] Firebase Authentication
- [ ] Camera integration cho scanner
- [ ] Push notifications cho hết hạn
- [ ] Export/Import dữ liệu

### Phase 4: UI/UX Improvements
- [ ] Animations và transitions
- [ ] Dark mode support
- [ ] Custom themes
- [ ] Accessibility improvements

## 👥 Team

- **Developer 1**: UI/UX và Navigation (Current)
- **Developer 2**: Database và Backend
- **Developer 3**: Authentication và Security

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Liên Hệ

- **Email**: your.email@example.com
- **GitHub**: [@your-username](https://github.com/your-username)

## 🙏 Acknowledgments

- Design inspired by Bách Hóa Xanh's Quà Tặng Vip app
- Built with Jetpack Compose
- Material Design 3

---

**Version**: 1.0.0 (Phase 1 - UI/Navigation)  
**Last Updated**: November 16, 2025
