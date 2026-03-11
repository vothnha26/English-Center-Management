# Đặc tả Phân hệ Giáo viên (Teacher Module)

## 1. Cơ chế Phân quyền & Giao diện
- **Kiến trúc:** Single Frame (MainMenuFrame). Hệ thống tự ẩn/hiện Menu dựa trên Vai trò (Role) sau khi đăng nhập.
- **Phạm vi dữ liệu:** Giáo viên chỉ thấy các lớp được gán tên mình trong `ClassEntity`.
- **Sidebar Giáo viên:**
    - Dashboard (Lịch dạy & Nhắc nhở).
    - Lớp học của tôi (Xem danh sách học viên).
    - Điểm danh nhanh (Quick Action).
    - Quản lý Điểm số & Feedback.

## 2. Tính năng Feedback qua Email
- **Quy trình:** Nhập điểm/Nhận xét -> Bấm "Gửi" -> Đưa vào Hàng chờ (Queue) -> Quản lý duyệt/Gửi hàng loạt.
- **Nội dung:** Mẫu HTML chuyên nghiệp (Center Branding).
- **Đính kèm:** Tự động xuất PDF phiếu điểm.
- **Đối tượng:** Gửi đồng thời tới Học viên và Phụ huynh.

## 3. Phân tích sự phù hợp với CSDL hiện tại (Gap Analysis)
Dựa trên các Entity hiện có, một số yêu cầu cần được lưu ý:
- **Email Phụ huynh:** Entity `Student` hiện chưa có trường `parentEmail`. 
    - *Giải pháp:* Tạm thời chỉ gửi cho Email của Học viên.
- **Hàng chờ & Lịch sử Email:** Hiện chưa có bảng `EmailQueue` hay `EmailHistory`.
    - *Giải pháp:* Tôi sẽ tạo một lớp Service mô phỏng hàng chờ trong bộ nhớ (In-memory Queue) và ghi Log ra Console/File cho bản Demo.
- **PDF Export:** Cần bổ sung thư viện hỗ trợ (như iText hoặc OpenPDF) vào `pom.xml`.

## 4. Danh sách công việc (To-do)
- [ ] Refactor `MainMenuFrame`: Thêm logic ẩn/hiện menu theo Role.
- [ ] Code `TeacherDashboardPanel`: Hiển thị lịch dạy hôm nay và nhắc nhở lớp chưa nhập điểm.
- [ ] Nâng cấp `GradeManagerPanel`: Thêm nút "Gửi Email cả lớp".
- [ ] Viết `EmailFeedbackService`: Sử dụng Java Lambda để xử lý hàng chờ và mẫu HTML.
