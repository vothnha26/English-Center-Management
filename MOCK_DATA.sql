-- SQL MOCK DATA FOR MIS LANGUAGE CENTER (FULL 19 TABLES)
-- Created by Gemini CLI - 10/03/2026
-- Fixed column names, units, and enums (EnrollmentStatus, PaymentMethod) to match Java Entities exactly.

USE mis_language_center;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. DỌN DẸP TOÀN BỘ BẢNG
TRUNCATE TABLE payments;
TRUNCATE TABLE invoices;
TRUNCATE TABLE certificates;
TRUNCATE TABLE results;
TRUNCATE TABLE attendances;
TRUNCATE TABLE enrollments;
TRUNCATE TABLE schedules;
TRUNCATE TABLE placement_tests;
TRUNCATE TABLE classes;
TRUNCATE TABLE courses;
TRUNCATE TABLE tokens;
TRUNCATE TABLE notifications;
TRUNCATE TABLE user_accounts;
TRUNCATE TABLE staffs;
TRUNCATE TABLE teachers;
TRUNCATE TABLE students;
TRUNCATE TABLE rooms;
TRUNCATE TABLE branches;
TRUNCATE TABLE promotions;

SET FOREIGN_KEY_CHECKS = 1;

-- 2. LEVEL 0: DANH MỤC ĐỘC LẬP
INSERT INTO branches (branch_id, branch_name, address, phone) VALUES 
(1, 'Trung tâm Quận 1', '123 Cách Mạng Tháng 8, Q.1, TP.HCM', '02838111222'),
(2, 'Chi nhánh Thủ Đức', '45 Võ Văn Ngân, TP. Thủ Đức', '02838333444');

INSERT INTO rooms (room_id, room_name, capacity, status) VALUES 
(1, 'Phòng 101 - Lab', 30, 'Active'),
(2, 'Phòng 102 - Theory', 40, 'Active');

INSERT INTO promotions (promotion_id, promo_name, discount_value, discount_type, start_date, end_date, status) VALUES 
(1, 'SUMMER2026', 15.00, 'Percent', '2026-06-01', '2026-08-31', 'Active'),
(2, 'WELCOME_NEW', 200000, 'Amount', '2026-01-01', '2026-12-31', 'Active');

-- 3. LEVEL 1: HỒ SƠ CON NGƯỜI
INSERT INTO staffs (staff_id, full_name, role, phone, email, status) VALUES 
(1, 'Võ Thành Nhã (Manager)', 'MANAGER', '0901112223', 'vtn26xn@gmail.com', 'Active'),
(2, 'Võ Thành Nhã (Consultant)', 'CONSULTANT', '0904445556', 'vothanhnha.mt12@gmail.com', 'Active'),
(3, 'Võ Thành Nhã (Accountant)', 'ACCOUNTANT', '0907778889', 'vothanhnha152@gmail.com', 'Active');

INSERT INTO teachers (teacher_id, full_name, specialty, phone, email, hire_date, status) VALUES 
(1, 'John Smith', 'IELTS Expert', '0911222333', 'john@mis.edu.vn', '2025-01-01', 'Active'),
(2, 'Maria Garcia', 'TOEIC & Business English', '0944555666', 'maria@mis.edu.vn', '2025-02-01', 'Active');

INSERT INTO students (student_id, full_name, date_of_birth, gender, phone, email, address, registration_date, status) VALUES 
(1, 'Nguyễn Văn An', '2005-01-01', 'Male', '0988000111', 'vanan@gmail.com', 'Quận 3, TP.HCM', '2026-03-01', 'Active'),
(2, 'Lê Thị Bình', '2006-02-02', 'Female', '0988000222', 'thibinh@gmail.com', 'Quận 1, TP.HCM', '2026-03-02', 'Active');

-- 4. LEVEL 2: TÀI KHOẢN & BẢO MẬT (PASSWORD: 123456)
INSERT INTO user_accounts (user_id, username, password_hash, role, is_active, staff_id) VALUES 
(1, 'vothanhnha26@gmail.com', 'jZkw3X8zS27rbQU9MQqQuxdr98fSlvVqd7ZiZQH5B8=', 'ADMIN', 1, NULL),
(2, 'vtn26xn@gmail.com', 'jZkw3X8zS27rbQU9MQqQuxdr98fSlvVqd7ZiZQH5B8=', 'STAFF', 1, 1),
(3, 'vothanhnha.mt12@gmail.com', 'jZkw3X8zS27rbQU9MQqQuxdr98fSlvVqd7ZiZQH5B8=', 'STAFF', 1, 2),
(4, 'vothanhnha152@gmail.com', 'jZkw3X8zS27rbQU9MQqQuxdr98fSlvVqd7ZiZQH5B8=', 'STAFF', 1, 3);

INSERT INTO tokens (token_id, user_id, token_value, type, expiry_date, used) VALUES 
(1, 1, 'INIT-TOKEN-ADMIN', 'EMAIL_VERIFICATION', '2026-12-31 23:59:59', 1);

INSERT INTO notifications (notification_id, title, content, target_role, created_by_user, created_at) VALUES 
(1, 'Welcome', 'Chào mừng bạn đến với hệ thống quản lý trung tâm MIS', 'All', 1, NOW());

-- 5. LEVEL 2 & 3: HỌC THUẬT (COURSES, CLASSES, PLACEMENT TESTS)
INSERT INTO courses (course_id, course_name, level, duration, duration_unit, fee, status) VALUES 
(1, 'IELTS Intensive 6.5', 'Advanced', 12, 'Week', 7500000, 'Active'),
(2, 'TOEIC Target 500', 'Intermediate', 8, 'Week', 3200000, 'Active');

INSERT INTO placement_tests (test_id, student_id, test_date, score, suggest_level, note) VALUES 
(1, 1, '2026-03-01', 6.0, 'Advanced', 'Đầu vào tốt'),
(2, 2, '2026-03-02', 3.5, 'Intermediate', 'Cần tập trung căn bản');

INSERT INTO classes (class_id, class_name, course_id, teacher_id, room_id, start_date, end_date, max_student, status) VALUES 
(1, 'IELTS-26-01', 1, 1, 1, '2026-04-01', '2026-07-01', 15, 'Planned'),
(2, 'TOEIC-26-02', 2, 2, 2, '2026-04-05', '2026-06-05', 20, 'Planned');

-- 6. LEVEL 3: CHI TIẾT HỌC TẬP (ENROLLMENTS, SCHEDULES, ATTENDANCES)
-- ĐÃ SỬA: status 'Enrolled' thay vì 'Active'
INSERT INTO enrollments (enrollment_id, student_id, class_id, enrollment_date, status) VALUES 
(1, 1, 1, '2026-03-05', 'Enrolled'),
(2, 2, 2, '2026-03-06', 'Enrolled');

INSERT INTO schedules (schedule_id, class_id, room_id, study_date, start_time, end_time) VALUES 
(1, 1, 1, '2026-04-01', '08:00:00', '10:00:00'),
(2, 2, 2, '2026-04-05', '18:00:00', '20:00:00');

INSERT INTO attendances (attendance_id, student_id, class_id, attend_date, status, note) VALUES 
(1, 1, 1, '2026-04-01', 'Present', 'Đi học đúng giờ');

-- 7. LEVEL 4: KẾT THÚC KHÓA (RESULTS, CERTIFICATES)
INSERT INTO results (result_id, student_id, class_id, score, grade, comment) VALUES 
(1, 1, 1, 7.5, 'Excellent', 'Hoàn thành xuất sắc khóa học');

INSERT INTO certificates (certificate_id, student_id, class_id, cert_name, issue_date, serial_no) VALUES 
(1, 1, 1, 'Chứng chỉ IELTS 7.5', '2026-07-15', 'MIS-CERT-2026-001');

-- 8. LEVEL 4: TÀI CHÍNH (INVOICES, PAYMENTS)
INSERT INTO invoices (invoice_id, student_id, promotion_id, total_amount, issue_date, status, note) VALUES 
(1, 1, NULL, 7500000, '2026-03-05', 'Paid', 'Học phí IELTS Intensive'),
(2, 2, 2, 3000000, '2026-03-06', 'Issued', 'Học phí TOEIC Target (Đã giảm 200k)');

-- ĐÃ SỬA: payment_method 'Cash' thay vì 'Bank_Transfer'
INSERT INTO payments (payment_id, invoice_id, student_id, amount, payment_method, payment_date, reference_code) VALUES 
(1, 1, 1, 7500000, 'Cash', '2026-03-05', 'VNPAY-123456');
