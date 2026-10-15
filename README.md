# DigiShield E2E

Bộ kiểm thử end-to-end cho trang đăng nhập Văn phòng điện tử UTC, sử dụng Java, Selenium WebDriver, JUnit 5 và Gradle. Kết quả kiểm thử được thu thập và tạo báo cáo bằng Allure.

## Yêu cầu

- Windows 10/11
- JDK 17 trở lên
- Google Chrome đã được cài đặt
- Kết nối Internet để Gradle tải dependencies và Selenium Manager tải ChromeDriver khi cần

Gradle Wrapper đã có sẵn trong dự án, không cần cài Gradle riêng.

## Chạy kiểm thử

Mở PowerShell tại thư mục dự án:

```powershell
cd D:\digishield-e2e
```

Chạy toàn bộ test:

```powershell
.\gradlew.bat test
```

Chạy riêng các test đăng nhập:

```powershell
.\gradlew.bat test --tests "e2e.tests.LoginE2ETest"
```

Chạy một test cụ thể:

```powershell
.\gradlew.bat test --tests "e2e.tests.LoginE2ETest.tc02_emptyBoth"
```

Mặc định Chrome chạy ở chế độ headless. Để xem trình duyệt chạy:

```powershell
.\gradlew.bat test -Pheadless=false
```

Đổi URL kiểm thử:

```powershell
.\gradlew.bat test -PbaseUrl="https://vanphongdientu.utc.edu.vn"
```

## Tài khoản kiểm thử

Một số test cần tài khoản hợp lệ. Đặt biến môi trường `UTC_USER` và `UTC_PASS` trong PowerShell trước khi chạy:

```powershell
$env:UTC_USER = "ten_dang_nhap"
$env:UTC_PASS = "mat_khau"
.\gradlew.bat test
```

Không ghi thông tin tài khoản hoặc mật khẩu vào mã nguồn. Nếu chưa đặt các biến môi trường này, những test cần đăng nhập hợp lệ sẽ bị bỏ qua.

## Tạo và xem báo cáo Allure

Chạy test trước để tạo dữ liệu kết quả:

```powershell
.\gradlew.bat test
```

Sau đó sinh báo cáo HTML:

```powershell
.\gradlew.bat allureReport
```

Báo cáo được tạo tại:

```text
build\reports\allure-report\allureReport\index.html
```

Hoặc chạy server để xem báo cáo trong trình duyệt:

```powershell
.\gradlew.bat allureServe
```

`allureReport` và `allureServe` dùng kết quả test đã có; hãy chạy `test` trước để cập nhật báo cáo theo lần chạy mới nhất. Nếu test thất bại, Gradle có thể trả mã lỗi nhưng kết quả vẫn được lưu để tạo báo cáo bằng lệnh `allureReport`.

Báo cáo HTML mặc định của Gradle cũng có tại:

```text
build\reports\tests\test\index.html
```

## Cấu trúc dự án

```text
src/test/java/e2e/
├── base/       # Khởi tạo và đóng WebDriver dùng chung
├── pages/      # Page Object cho các trang được kiểm thử
└── tests/      # Các test case end-to-end
```

## Ghi chú

- Các test đăng nhập nằm trong `LoginE2ETest`.
- `WatchlistE2ETest` hiện được đánh dấu bỏ qua vì ứng dụng UTC không có chức năng Watchlist.
- Kết quả test, báo cáo HTML và file build nằm trong `build/`; thư mục này không được đưa vào Git.
