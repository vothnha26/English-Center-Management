# Quy trình Vận hành & Checklist Kiểm thử - Phân hệ Giáo viên

Tài liệu này hướng dẫn luồng công việc (Workflow) thực tế của một Giáo viên trên hệ thống và các hạng mục cần kiểm tra để đảm bảo chất lượng.

## 1. Luồng vận hành (Workflow)

### Bước 1: Đăng nhập & Tổng quan (Dashboard)
- Giáo viên đăng nhập vào hệ thống.
- Hệ thống tự động nhận diện Role `TEACHER` và hiển thị Sidebar thu gọn.
- Kiểm tra các thẻ KPI: Lịch dạy hôm nay, Lớp chưa nhập điểm, Tỷ lệ đạt.
- Xem biểu đồ chuyên cần để nắm tình hình lớp.

### Bước 2: Kiểm tra Lịch dạy (Schedules)
- Truy cập mục **"Lịch dạy/học"**.
- Xem thời khóa biểu dạng lưới (Visual Calendar).
- Xác định phòng học và **Sĩ số tối đa** của lớp sắp dạy.

### Bước 3: Điểm danh (Attendance)
- Sử dụng nút **"ĐIỂM DANH NHANH"** trên Dashboard hoặc vào mục Điểm danh.
- Chọn lớp đang dạy.
- Sử dụng nút **"TẤT CẢ CÓ MẶT"** để tiết kiệm thời gian, sau đó tích bỏ những người vắng.
- Đối với học viên vắng có phép, chọn trạng thái **"Excused_Absent"**.
- Bấm **"LƯU ĐIỂM DANH"**.

### Bước 4: Nhập điểm & Xếp loại (Grading)
- Khi khóa học kết thúc, vào mục **"Quản lý Điểm số"**.
- Chọn lớp cần nhập điểm.
- Nhập điểm (0-10) trực tiếp vào bảng.
- Quan sát hệ thống tự gợi ý **Xếp loại** và **Cảnh báo đỏ** nếu điểm < 5.0.
- Nhập nhận xét chi tiết cho từng học viên.
- Bấm **"LƯU TẠM"** hoặc **"CHỐT & KẾT THÚC"**.

### Bước 5: Gửi Feedback (Email & PDF)
- Bấm **"GỬI EMAIL CẢ LỚP"** để hệ thống tự động gửi thông báo điểm qua mẫu HTML chuyên nghiệp.
- Chọn học viên tiêu biểu, bấm **"XUẤT PDF"** để tạo phiếu điểm chính thức gửi cho phụ huynh.

---

## 2. Bảng Checklist Kiểm thử (UAT)

| Hạng mục | Tính năng cần kiểm tra | Trạng thái | Ghi chú |
|:---|:---|:---:|:---|
| **Sidebar** | Ẩn các mục: Học viên, Giáo viên (Admin), Tài chính, Nhân sự | [ ] | Bảo mật |
| **Dashboard**| Hiện phím tắt: Điểm danh nhanh, Nhập điểm ngay | [ ] | Tiện ích |
| **Lịch học** | Hiển thị đúng Thứ trong tuần & Sĩ số lớp | [ ] | Trực quan |
| **Điểm danh** | Hoạt động của nút "Tất cả có mặt" & Trạng thái "Vắng có phép" | [ ] | Chính xác |
| **Báo cáo** | Tab "Tổng hợp chuyên cần" hiện đúng tỷ lệ nghỉ học | [ ] | Thống kê |
| **Nhập điểm** | Chỉnh sửa trực tiếp trên bảng (Grid Edit) | [ ] | Tốc độ |
| **Logic** | Tự động gợi ý xếp loại & Tô màu đỏ cho điểm yếu | [ ] | Trí tuệ |
| **Email** | Gửi email hàng loạt theo hàng chờ (Queue) | [ ] | Feedback |
| **PDF** | Xuất file PDF phiếu điểm có Logo & Định dạng chuẩn | [ ] | Chuyên nghiệp |
| **UI/UX** | Không mở Popup, không lỗi Tiếng Việt có dấu | [ ] | Thẩm mỹ |

---
*Tài liệu được tạo tự động bởi trợ lý AI để phục vụ mục đích kiểm thử thực chiến.*
