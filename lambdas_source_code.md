# Danh sách các mã nguồn sử dụng Java Lambda (Trừ View)

Tài liệu này tổng hợp các hàm/phương thức sử dụng biểu thức Lambda trong ứng dụng, kèm theo đường dẫn file tương ứng.

---

## 1. com.trungtamdaotao.Main
**Đường dẫn:** `src/main/java/com/trungtamdaotao/Main.java`

```java
public static void main(String[] args) throws Exception {
    // ... (omitted setup)
    SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
}
```

---

## 2. com.trungtamdaotao.controller.student.StudentController
**Đường dẫn:** `src/main/java/com/trungtamdaotao/controller/student/StudentController.java`

```java
public Enrollment getEnrollmentById(Long id) {
    return enrollmentService.getAll().stream().filter(e -> e.getEnrollmentId().equals(id)).findFirst().orElse(null);
}
```

---

## 3. com.trungtamdaotao.model.dao.AbstractDAO
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/dao/AbstractDAO.java`

```java
@Override
public void save(T entity) {
    executeInsideTransaction(em -> em.persist(entity));
}

@Override
public void update(T entity) {
    executeInsideTransaction(em -> em.merge(entity));
}

@Override
public void delete(Long id) {
    executeInsideTransaction(em -> {
        T entity = em.find(entityClass, id);
        if (entity != null) em.remove(entity);
    });
}
```

---

## 4. com.trungtamdaotao.model.service.academic.AttendanceService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/academic/AttendanceService.java`

```java
public double getAttendanceRate(ClassEntity clazz, LocalDate startDate, LocalDate endDate) {
    List<Attendance> attendances = attendanceDAO.findAll().stream()
            .filter(a -> a.getClazz() != null && a.getClazz().equals(clazz))
            .filter(a -> a.getAttend_date() != null && 
                    !a.getAttend_date().isBefore(startDate) && 
                    !a.getAttend_date().isAfter(endDate))
            .collect(Collectors.toList());
    
    if (attendances.isEmpty()) {
        return 0.0;
    }
    
    long presentCount = attendances.stream()
            .filter(a -> a.getStatus() == AttendanceStatus.Present)
            .count();
    
    return (double) presentCount / attendances.size() * 100;
}

public long countAbsentByStudentAndClass(Student student, ClassEntity clazz) {
    return attendanceDAO.findByStudentAndClass(student, clazz).stream()
            .filter(a -> a.getStatus() == AttendanceStatus.Absent)
            .count();
}
```

---

## 5. com.trungtamdaotao.model.service.academic.ClassService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/academic/ClassService.java`

```java
public List<ClassEntity> searchClasses(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return classDAO.findAll();
    }
    String lowerKey = keyword.toLowerCase();
    return classDAO.findAll().stream()
            .filter(c -> c.getClassName().toLowerCase().contains(lowerKey))
            .collect(Collectors.toList());
}

public List<ClassEntity> getClassesByCourse(Course course) {
    if (course == null) {
        return classDAO.findAll();
    }
    return classDAO.findAll().stream()
            .filter(c -> c.getCourse() != null && c.getCourse().equals(course))
            .collect(Collectors.toList());
}

public List<ClassEntity> getClassesByCourseId(Long courseId) {
    if (courseId == null) {
        return classDAO.findAll();
    }
    return classDAO.findAll().stream()
            .filter(c -> c.getCourse() != null && c.getCourse().getCourse_id().equals(courseId))
            .toList();
}
```

---

## 6. com.trungtamdaotao.model.service.academic.CourseService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/academic/CourseService.java`

```java
public List<Course> searchCourses(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
        return courseDAO.findAll();
    }
    String lowerKey = keyword.toLowerCase();
    return courseDAO.findAll().stream()
            .filter(c -> c.getCourseName().toLowerCase().contains(lowerKey))
            .collect(Collectors.toList());
}

public List<Course> getActiveCourses() {
    return courseDAO.findAll().stream()
            .filter(c -> c.getStatus() == Status.Active)
            .collect(Collectors.toList());
}

public List<Course> getCoursesByStatus(Status status) {
    return courseDAO.findAll().stream()
            .filter(c -> c.getStatus() == status)
            .collect(Collectors.toList());
}
```

---

## 7. com.trungtamdaotao.model.service.academic.ScheduleService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/academic/ScheduleService.java`

```java
public List<Schedule> getSchedulesByClass(ClassEntity clazz) {
    if (clazz == null) {
        return scheduleDAO.findAll();
    }
    return scheduleDAO.findAll().stream()
            .filter(s -> s.getClazz() != null && s.getClazz().equals(clazz))
            .collect(Collectors.toList());
}
```

---

## 8. com.trungtamdaotao.model.service.finance.FinanceReportService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/finance/FinanceReportService.java`

```java
public BigDecimal getTotalRevenue() {
    return paymentDAO.findAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.Completed)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

public BigDecimal getRevenueBetween(LocalDateTime from, LocalDateTime to) {
    return paymentDAO.findAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.Completed)
            .filter(p -> !p.getPaymentDate().isBefore(from) && !p.getPaymentDate().isAfter(to))
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
}

public Map<String, BigDecimal> getMonthlyRevenue(int year) {
    Map<String, BigDecimal> result = new LinkedHashMap<>();
    for (int m = 1; m <= 12; m++) {
        result.put(year + "-" + String.format("%02d", m), BigDecimal.ZERO);
    }
    paymentDAO.findAll().stream()
            .filter(p -> p.getStatus() == PaymentStatus.Completed)
            .filter(p -> p.getPaymentDate().getYear() == year)
            .forEach(p -> {
                String key = year + "-" + String.format("%02d", p.getPaymentDate().getMonthValue());
                result.merge(key, p.getAmount(), BigDecimal::add);
            });
    return result;
}
```

---

## 9. com.trungtamdaotao.model.service.finance.PaymentService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/finance/PaymentService.java`

```java
public void recordPayment(Invoice invoice, BigDecimal amount,
                          PaymentMethod method, String referenceCode) {
    // ...
    BigDecimal totalPaid = paymentDAO.findByInvoiceId(invoice.getInvoiceId())
            .stream()
            .filter(pay -> pay.getStatus() == PaymentStatus.Completed)
            .map(Payment::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    // ...
}
```

---

## 10. com.trungtamdaotao.model.service.student.EnrollmentService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/student/EnrollmentService.java`

```java
public void enroll(Student student, ClassEntity clazz) {
    List<Enrollment> existing = enrollmentDAO.findByStudentId(student.getStudent_id());
    boolean hasActive = existing.stream().anyMatch(en -> en.getStatus() == EnrollmentStatus.Pending || en.getStatus() == EnrollmentStatus.Enrolled);
    // ...
}

public List<Enrollment> getPendingEnrollments() {
    return enrollmentDAO.findAll().stream()
        .filter(en -> en.getStatus() == EnrollmentStatus.Pending)
        .toList();
}
```

---

## 11. com.trungtamdaotao.model.service.student.StudentService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/student/StudentService.java`

```java
public List<Student> getActiveStudents() {
    return studentDAO.findAll().stream()
            .filter(s -> s.getStatus() == Status.Active)
            .collect(Collectors.toList());
}

public void addStudent(String fullName, String phone, String email,
                       String address, LocalDate dob, String plainPassword) throws Exception {
    // ...
    if (email != null && !email.isBlank()) {
        registrationService.registerUser(email, email, AccountRole.Student, acc -> acc.setStudent(s), plainPassword);
    }
}
```

---

## 12. com.trungtamdaotao.model.service.system.StaffService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/system/StaffService.java`

```java
public List<Staff> getActiveStaff() {
    return staffDAO.findAll().stream()
            .filter(s -> s.getStatus() == Status.Active)
            .collect(Collectors.toList());
}

public void addStaff(String fullName, StaffRole role, String phone, String email) throws Exception {
    // ...
    if (email != null && !email.isBlank()) {
        AccountRole accountRole = role == StaffRole.ADMIN ? AccountRole.Admin : AccountRole.Staff;
        registrationService.registerUser(email, email, accountRole, acc -> acc.setStaff(s));
    }
}
```

---

## 13. com.trungtamdaotao.model.service.teacher.TeacherService
**Đường dẫn:** `src/main/java/com/trungtamdaotao/model/service/teacher/TeacherService.java`

```java
public List<Teacher> getActiveTeachers() {
    return teacherDAO.findAll().stream()
            .filter(t -> t.getStatus() == Status.Active)
            .collect(Collectors.toList());
}

public void addTeacher(String fullName, String phone, String email,
                       String specialty, LocalDate hireDate) throws Exception {
    // ...
    if (email != null && !email.isBlank()) {
        registrationService.registerUser(email, email, AccountRole.Teacher, acc->acc.setTeacher(t));
    }
}
```

---

## 14. com.trungtamdaotao.util.security.UserPermissionImpl
**Đường dẫn:** `src/main/java/com/trungtamdaotao/util/security/UserPermissionImpl.java`

```java
public boolean canAccessModule(String moduleName) {
    // ...
    return STAFF_PERMISSIONS.getOrDefault(user.getStaff().getRole(), EnumSet.noneOf(PermissionType.class))
            .stream().anyMatch(p -> p.name().contains(moduleName));
}
```
