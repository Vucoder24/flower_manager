# 🌸 [FLOWER MANAGER SHOP APP] - Ứng Dụng Quản Lý Bán Hoa

Một ứng dụng di động **Android Native** toàn diện, được thiết kế để giúp chủ cửa hàng quản lý **danh mục hoa**, tạo và theo dõi **hóa đơn**, và cung cấp **báo cáo thống kê** doanh thu/lợi nhuận theo thời gian thực. Dự án tập trung vào logic nghiệp vụ phức tạp và hiệu suất xử lý dữ liệu.

---

## ✨ Các Tính Năng Chính

Đây là những điểm nổi bật của ứng dụng:

### 1. Quản lý Bán hàng & Giao dịch (Sales & Transaction Management)

* **Tạo Hóa đơn Nhanh chóng:** Dễ dàng lập hóa đơn với thông tin khách hàng, áp dụng giảm giá và ghi chú, có thể vuốt đơn sang phải ở tab **Hóa đơn dự kiến** để hoàn thành đơn.
* **Theo dõi Giao hàng & Logistics:** Quản lý trạng thái đơn hàng (**Chưa giao/Đã giao**) và phân loại các đơn hàng theo thời gian giao (**Chưa đến ngày, Đến ngày giao, Quá hạn**) để lập kế hoạch giao hàng.
* **Tính toán Lợi nhuận Tự động:** Sử dụng riêng biệt **Giá nhập (Wholesale)** và **Giá bán (Retail)** để tự động tính toán lợi nhuận trên từng giao dịch.

### 2. Quản lý Danh mục & Báo cáo (Inventory & Reporting)

* **Danh mục Hoa (CRUD):** Quản lý toàn diện các loại hoa (thêm, sửa, xóa) với thông tin giá cả và hình ảnh.
* **Báo cáo Thống kê Toàn diện:** Cung cấp các chỉ số kinh doanh quan trọng: **Tổng Doanh thu**, **Tổng Lợi nhuận**, và **Số đơn chưa giao**.
* **Phân tích Hiệu suất:** Xếp hạng **Top 10** sản phẩm bán chạy nhất (theo số lượng và doanh thu) và lọc báo cáo theo chu kỳ (**Hôm nay, Tuần này, Tháng này, Năm này**).

---

## 📸 Giao Diện Ứng Dụng (Screenshots)

Đây là các màn hình chính của ứng dụng:

| Thêm Hóa Đơn | Danh Sách Hoa | Lịch Sử Hóa Đơn |
| :---: | :---: | :---: |
| <img width="250" height="600" alt="image" src="https://github.com/user-attachments/assets/65029831-38e4-4b49-b92c-ac65915e20ce" />  | <img width="250" height="600" alt="image" src="https://github.com/user-attachments/assets/4c589e01-dd58-4ae7-ba89-c9634187eaef" />  |  <img width="250" height="600" alt="image" src="https://github.com/user-attachments/assets/737e9604-b0c8-4a53-80ea-a91830016279" />  |

| Báo Cáo Thống Kê | Hóa Đơn Dự Kiến | Thêm Hoa |
| :---: | :---: | :---: |
| <img width="250" height="600" alt="image" src="https://github.com/user-attachments/assets/63e80cf0-eeb5-4849-bb21-cfb675333e25" />  |  <img width="250" height="600" alt="image" src="https://github.com/user-attachments/assets/e69ce0df-9867-4b2e-8e82-a6879c8f8e62" />  | <img width="250" height="600" alt="image" src="https://github.com/user-attachments/assets/7a75a678-48ae-45e3-be56-0c4d38c1292d" />  |


---

## 🛠️ Công Nghệ & Kiến Trúc

Dự án được xây dựng trên nền tảng Android Native, sử dụng các công nghệ và kiến trúc:

| Lĩnh vực | Công nghệ/Thư viện | Mục đích |
| :--- | :--- | :--- |
| **Ngôn ngữ/Frontend** | **Kotlin**, **XML Layouts** | Ngôn ngữ hiện đại, tập trung vào hiệu suất và xây dựng giao diện người dùng. |
| **Kiến trúc** | **MVVM (Model-View-ViewModel)** | Đảm bảo tính module hóa, dễ bảo trì và dễ dàng kiểm thử đơn vị. |
| **Cơ sở Dữ liệu Cục bộ** | **Room Persistence Library** | Quản lý dữ liệu giao dịch và danh mục hoa một cách an toàn và hiệu quả. |
| **Bất đồng bộ** | **Coroutines/Live Data** | Xử lý các thao tác database và logic nghiệp vụ bất đồng bộ một cách hiệu quả, tránh chặn luồng giao diện người dùng (UI Thread).

---

### 📥 Tải Xuống Ứng Dụng (APK)

Nhấn vào nút dưới đây để tải về và cài đặt phiên bản mới nhất trên Android:

[![Tải Xuống APK](https://img.shields.io/badge/Tải%20Xuống%20APK-D2122D?style=for-the-badge&logo=android&logoColor=white)](https://drive.google.com/file/d/1esezDbnCmWWMzjJZQOP0a7_XyXhCsd6d/view?usp=sharing)

*Yêu cầu Android 12+ trở lên.*
