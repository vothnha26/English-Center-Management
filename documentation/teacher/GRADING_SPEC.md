# Đặc tả Module: Quản lý Điểm số & Xếp loại

## 1. Luồng nghiệp vụ (Workflow)
- **Đối tượng:** Ưu tiên Giáo viên (nhập điểm), Quản lý (kiểm soát/chốt điểm).
- **Vị trí:** Mục riêng biệt "Quản lý Điểm số" trên Sidebar.
- **Hình thức:** Nhập bảng tập trung (Grid Edit). Giáo viên chọn lớp, hệ thống hiện danh sách học viên, giáo viên nhập điểm và nhấn Tab để chuyển nhanh giữa các ô.

## 2. Quy tắc tính toán & Hiển thị
- **Điểm số:** Nhập số (0-10).
- **Xếp loại (Grade):** Hệ thống tự động **Gợi ý** dựa trên điểm nhưng cho phép Giáo viên ghi đè (Override).
    - >= 9.0: Xuất sắc
    - >= 8.0: Giỏi
    - >= 6.5: Khá
    - >= 5.0: Trung bình
    - < 5.0: Yếu (Đánh dấu **Màu Đỏ** rực để cảnh báo).
- **Nhận xét:** Ô văn bản tự do cho từng học viên.

## 3. Danh sách cần bổ sung/Kiểm tra (CSDL & Entity)
- [ ] **Trường 'grade' trong Entity Result:** Cần kiểm tra xem là String hay Enum.
- [ ] **Trường 'comment' trong Entity Result:** Đảm bảo có độ dài đủ lớn (Text).
- [ ] **Trạng thái 'Locked':** Cần thêm trường `is_locked` vào bảng `results` hoặc `classes` để kiểm soát quy trình chốt điểm (Hiện tại CSDL chưa có, cần bổ sung sau).
- [ ] **Logic gợi ý:** Thực hiện tính toán ngay trên sự kiện thay đổi dữ liệu của bảng (Table Data Change Listener).

## 4. Giao diện (UI)
- **Top:** ComboBox chọn Lớp học (chỉ hiện các lớp đang hoạt động hoặc sắp kết thúc).
- **Center:** JTable hỗ trợ Edit trực tiếp trên cell.
- **Bottom:** Nút "Lưu tạm" và "Chốt điểm & Kết thúc lớp".
